




package xxxx;



@CdrRead
@PrimitiveRead
public class CDRInputStream_1_0 extends CDRInputStreamBase 
    implements RestorableInputStream
{
    protected static final ORBUtilSystemException wrapper = ORBUtilSystemException.self;
    private static final OMGSystemException omgWrapper = OMGSystemException.self;
    private static final String K_READ_METHOD = "read";
    private static final int MAX_BLOCK_LENGTH = 0x7fffff00;

    protected BufferManagerRead bufferManagerRead;
    protected ByteBuffer byteBuffer;

    protected ORB orb;
    protected ValueHandler valueHandler = null;

    
    private CacheTable<Object> valueCache = null;
    
    
    private CacheTable<String> repositoryIdCache = null;

    
    private CacheTable<String> codebaseCache = null;

    
    

    
    protected int blockLength = MAX_BLOCK_LENGTH;

    
    protected int end_flag = 0;

    
    
    
    
    
    private int chunkedValueNestingLevel = 0;

    
    

    
    

    
    

    
    
    protected int valueIndirection = 0;

    
    
    protected int stringIndirection = 0;

    
    protected boolean isChunked = false;

    
    private RepositoryIdUtility repIdUtil;
    private RepositoryIdStrings repIdStrs;

    
    private CodeSetConversion.BTCConverter charConverter;
    private CodeSetConversion.BTCConverter wcharConverter;

    
    
    
    
    
    
    
    private boolean specialNoOptionalDataState = false;

    
    
    @SuppressWarnings("RedundantStringConstructorCall")
    final String newEmptyString() {
        return new String("");
    }
    
    
    public CDRInputStreamBase dup() 
    {
        CDRInputStreamBase result = null ;

        try {
            result = this.getClass().newInstance();
        } catch (Exception e) {
            throw wrapper.couldNotDuplicateCdrInputStream( e ) ;
        }
        result.init(this.orb,
                byteBuffer,
                byteBuffer.limit(),
                byteBuffer.order(),
                this.bufferManagerRead);

        return result;
    }

    @Override
    void init(org.omg.CORBA.ORB orb, ByteBuffer byteBuffer, int bufferSize, ByteOrder byteOrder, BufferManagerRead bufferManager) {
        this.orb = (ORB)orb;
        this.bufferManagerRead = bufferManager;
        this.byteBuffer = byteBuffer;
        this.byteBuffer.position(0);
        this.byteBuffer.order(byteOrder);
        this.byteBuffer.limit(bufferSize);
        this.markAndResetHandler = bufferManagerRead.getMarkAndResetHandler();
    }

    
    void performORBVersionSpecificInit() {
        createRepositoryIdHandlers();
    }

    private void createRepositoryIdHandlers()
    {
        repIdUtil = RepositoryIdFactory.getRepIdUtility();
        repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
    }

    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_0;
    }
    
    
    
    void setHeaderPadding(boolean headerPadding) {
        throw wrapper.giopVersionError();
    }

    protected final int computeAlignment(int index, int align) {
        if (align > 1) {
            int incr = index & (align - 1);
            if (incr != 0) {
                return align - incr;
            }
        }

        return 0;
    }

    @InfoMethod
    private void notChunked() { }

    @CdrRead
    protected void checkBlockLength(int align, int dataSize) {
        
        
        
        
        if (!isChunked) {
            notChunked() ;
            return;
        }

        
        
        
        
        
        
        
        if (specialNoOptionalDataState) {
            throw omgWrapper.rmiiiopOptionalDataIncompatible1() ;
        }

        boolean checkForEndTag = false;

        
        
        
        
        
        
        
        if (blockLength == get_offset()) {

            blockLength = MAX_BLOCK_LENGTH;
            start_block();

            
            
            
            
            
            
            if (blockLength == MAX_BLOCK_LENGTH) {
                checkForEndTag = true;
            }

        } else if (blockLength < get_offset()) {
            
            
            throw wrapper.chunkOverflow() ;
        }

        
        
        
        
        
        
        int requiredNumBytes = computeAlignment(byteBuffer.position(), align) + dataSize;

        if (blockLength != MAX_BLOCK_LENGTH &&
            blockLength < get_offset() + requiredNumBytes) {
            throw omgWrapper.rmiiiopOptionalDataIncompatible2() ;
        }

        
        
        
        if (checkForEndTag) {
            int nextLong = read_long();
            byteBuffer.position(byteBuffer.position() - 4);

            
            
            
            if (nextLong < 0) {
                throw omgWrapper.rmiiiopOptionalDataIncompatible3();
            }
        }
    }

    @CdrRead
    protected void alignAndCheck(int align, int n) {
        checkBlockLength(align, n);

        
        
        int alignResult = computeAlignment(byteBuffer.position(), align);
        byteBuffer.position(byteBuffer.position() + alignResult);

        if (byteBuffer.position() + n > byteBuffer.limit()) {
            grow(align, n);
        }
    }

    
    
    @CdrRead
    protected void grow(int align, int n) {
        byteBuffer = bufferManagerRead.underflow(byteBuffer);

    }

    
    
    

    public final void consumeEndian() {
        ByteOrder byteOrder = read_boolean() ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        byteBuffer.order(byteOrder);
    }

    public final boolean read_boolean() {
        return (read_octet() != 0);
    }

    public final char read_char() {
        alignAndCheck(1, 1);

        return getConvertedChars(1, getCharConverter())[0];
    }

    @CdrRead
    public char read_wchar() {
        
        if (ORBUtility.isForeignORB(orb)) {
            throw wrapper.wcharDataInGiop10() ;
        }

        
        return (char) byteBuffer.getShort();
    }

    @CdrRead
    public final byte read_octet() {
        alignAndCheck(1, 1);
        return byteBuffer.get();
    }

    @CdrRead
    public final short read_short() {
        alignAndCheck(2, 2);
        return byteBuffer.getShort();
    }

    public final short read_ushort() {
        return read_short();
    }

    @CdrRead
    public final int read_long() {
        alignAndCheck(4, 4);
        return byteBuffer.getInt();
    }

    public final int read_ulong() {
        return read_long();
    }

    @CdrRead
    public final long read_longlong() {
        alignAndCheck(8, 8);
        return byteBuffer.getLong();
    }

    public final long read_ulonglong() {
        return read_longlong();
    }

    public final float read_float() {
        return Float.intBitsToFloat(read_long());
    }

    public final double read_double() {
        return Double.longBitsToDouble(read_longlong());
    }

    protected final void checkForNegativeLength(int length) {
        if (length < 0) {
            throw wrapper.negativeStringLength(length);
        }
    }

    
    @CdrRead
    protected final String readStringOrIndirection(boolean allowIndirection) {
        String result = "" ;

        int len = read_long();

        
        
        
        if (allowIndirection) {
            if (len == 0xffffffff) {
                return null;
            } else {
                stringIndirection = get_offset() - 4;
            }
        }

        checkForNegativeLength(len);

        result = internalReadString(len);

        return result ;
    }

    @CdrRead
    private String internalReadString(int len) {
        
        
        
        if (len == 0) {
            return newEmptyString();
        }

        char[] result = getConvertedChars(len - 1, getCharConverter());

        
        read_octet();

        return new String(result, 0, getCharConverter().getNumChars());
    }

    public final String read_string() {
        return readStringOrIndirection(false);
    }

    @CdrRead
    public String read_wstring() {
        
        
        if (ORBUtility.isForeignORB(orb)) {
            throw wrapper.wcharDataInGiop10();
        }

        int len = read_long();

        
        
        
        
        if (len == 0) {
            return newEmptyString();
        }

        checkForNegativeLength(len);

        len--;
        char[] c = new char[len];

        for (int i = 0; i < len; i++) {
            c[i] = read_wchar();
        }

        
        read_wchar();

        return new String(c);
    }

    @CdrRead
    public final void read_octet_array(byte[] buffer, int offset, int length) {
        if ( buffer == null ) {
            throw wrapper.nullParam();
        }

        if (length == 0) {
            return;
        }

        alignAndCheck(1, 1);

        int numWritten = 0;
        while (numWritten < length) {
            if (!byteBuffer.hasRemaining()) grow(1, 1);

            int count = Math.min(length - numWritten, byteBuffer.remaining());
            byteBuffer.get(buffer, numWritten + offset, count);
            numWritten += count;
        }
    }

    @SuppressWarnings({"deprecation"})
    public org.omg.CORBA.Principal read_Principal() {
        int len = read_long();
        byte[] pvalue = new byte[len];
        read_octet_array(pvalue,0,len);

        org.omg.CORBA.Principal p = new PrincipalImpl();
        p.name(pvalue); 
        return p;
    }

    @CdrRead
    public TypeCode read_TypeCode() {
        TypeCodeImpl tc = new TypeCodeImpl(orb);
        tc.read_value(parent);
        return tc ;
    }
  
    @CdrRead
    public Any read_any() {
        Any any = null ;

        any = orb.create_any();
        TypeCodeImpl tc = new TypeCodeImpl(orb);

        

        
        
        
        
        
        
        try {
            tc.read_value(parent);
        } catch (MARSHAL ex) {
            if (tc.kind().value() != TCKind._tk_value) {
                throw ex;
            }
            
            
        }
        
        any.read_value(parent, tc);

        return any;
    }

    @CdrRead
    public org.omg.CORBA.Object read_Object() { 
        return read_Object(null);
    }

    @InfoMethod
    private void nullIOR() { }

    @InfoMethod
    private void className( String name ) { }

    @InfoMethod
    private void stubFactory( PresentationManager.StubFactory fact ) { }

    

    
    
    
    
    
    
    
    
    
    
    
    
    
    @CdrRead
    public org.omg.CORBA.Object read_Object(Class clz) 
    {
        
        IOR ior = IORFactories.makeIOR( orb, (InputStream)parent) ;
        if (ior.isNil()) {
            nullIOR() ;
            return null;
        }

        PresentationManager.StubFactoryFactory sff = ORB.getStubFactoryFactory() ;
        String codeBase = ior.getProfile().getCodebase() ;
        PresentationManager.StubFactory stubFactory = null ;

        if (clz == null) {
            RepositoryId rid = RepositoryId.cache.getId( ior.getTypeId() ) ;
            String className = rid.getClassName() ;
            className( className ) ;
            boolean isIDLInterface = rid.isIDLType() ;

            if (className == null || className.equals( "" )) {
                stubFactory = null;
            } else {
                try {
                    stubFactory = sff.createStubFactory(className,
                        isIDLInterface, codeBase, (Class<?>) null,
                        (ClassLoader) null);
                } catch (Exception exc) {
                    stubFactory = null;
                }
            }
            stubFactory( stubFactory ) ;
        } else if (StubAdapter.isStubClass( clz )) {
            stubFactory = PresentationDefaults.makeStaticStubFactory(
                clz ) ;
            stubFactory( stubFactory ) ;
        } else {
            
            boolean isIDL = ClassInfoCache.get( clz ).isAIDLEntity(clz) ;

            stubFactory = sff.createStubFactory( clz.getName(), 
                isIDL, codeBase, clz, clz.getClassLoader() ) ;
            stubFactory( stubFactory ) ;
        }

        return internalIORToObject( ior, stubFactory, orb );
    }

    
    @CdrRead
    public static org.omg.CORBA.Object internalIORToObject(
        IOR ior, PresentationManager.StubFactory stubFactory, ORB orb)
    {
        java.lang.Object servant = ior.getProfile().getServant() ;
        if (servant != null ) {
            if (servant instanceof Tie) {
                String codebase = ior.getProfile().getCodebase();
                org.omg.CORBA.Object objref = (org.omg.CORBA.Object)
                    Utility.loadStub( (Tie)servant, stubFactory, codebase, 
                        false);
                    
                
                
                if (objref != null) {
                    return objref;   
                } else {
                    throw wrapper.readObjectException() ;
                }
            } else if (servant instanceof org.omg.CORBA.Object) {
                if (!(servant instanceof 
                        org.omg.CORBA.portable.InvokeHandler)) {
                    return (org.omg.CORBA.Object)servant;
                }
            } else {
                throw wrapper.badServantReadObject();
            }
        }

        ClientDelegate del = ORBUtility.makeClientDelegate( ior ) ;

        org.omg.CORBA.Object objref = null ;
        if (stubFactory == null) {
            objref = new CORBAObjectImpl();
        } else {
            try {
                objref = stubFactory.makeStub() ;
            } catch (Throwable e) {
                wrapper.stubCreateError( e ) ;

                if (e instanceof ThreadDeath) {
                    throw (ThreadDeath) e;
                }

                
                objref = new CORBAObjectImpl() ;            
            }
        }
        
        StubAdapter.setDelegate( objref, del ) ;
        return objref;
    }
 
    @CdrRead
    public java.lang.Object read_abstract_interface() 
    {
        return read_abstract_interface(null);
    }

    public java.lang.Object read_abstract_interface(java.lang.Class clz) 
    {
        boolean object = read_boolean();

        if (object) {
            return read_Object(clz);
        } else {
            return read_value();
        }
    }

    @CdrRead
    public Serializable read_value() 
    {
        return read_value((Class<?>)null);
    }

    @InfoMethod
    private void indirectionValue( int indir ) { }

    @CdrRead
    private Serializable handleIndirection() {
        int indirection = read_long() + get_offset() - 4;

        indirectionValue( indirection ) ;

        if (valueCache != null && valueCache.containsVal(indirection)) {

            java.io.Serializable cachedValue
                = (java.io.Serializable)valueCache.getKey(indirection);
            return cachedValue;
        } else {
            
            
            
            
            throw new IndirectionException(indirection);
        }
    }

    private String readRepositoryIds(int valueTag,
                                     Class<?> expectedType,
                                     ClassInfoCache.ClassInfo cinfo,
                                     String expectedTypeRepId) {
        return readRepositoryIds(valueTag, expectedType,
                                 cinfo, expectedTypeRepId, null);
    }

    
    private String readRepositoryIds(int valueTag,
                                     Class<?> expectedType,
                                     ClassInfoCache.ClassInfo cinfo,
                                     String expectedTypeRepId,
                                     BoxedValueHelper factory) {
        switch(repIdUtil.getTypeInfo(valueTag)) {
            case RepositoryIdUtility.NO_TYPE_INFO :
                
                
                
                if (expectedType == null) {
                    if (expectedTypeRepId != null) {
                        return expectedTypeRepId;
                    } else if (factory != null) {
                        return factory.get_id();
                    } else {
                        throw wrapper.expectedTypeNullAndNoRepId( ) ;
                    }
                }
                return repIdStrs.createForAnyType(expectedType,cinfo);
            case RepositoryIdUtility.SINGLE_REP_TYPE_INFO :
                return read_repositoryId(); 
            case RepositoryIdUtility.PARTIAL_LIST_TYPE_INFO :
                return read_repositoryIds();
            default:
                throw wrapper.badValueTag( Integer.toHexString(valueTag) ) ;
        }
    }

    @CdrRead
    private Object readRMIIIOPValueType( int indirection, 
        Class<?> valueClass, String repositoryIDString ) {

        try {
            if (valueHandler == null) {
                valueHandler = ORBUtility.createValueHandler();
            }

            return valueHandler.readValue(parent, indirection, valueClass, 
                repositoryIDString, getCodeBase());
        } catch(SystemException sysEx) {
            
            
            throw sysEx;
        } catch(Exception ex) {
            throw wrapper.valuehandlerReadException( ex ) ;
        } catch(Error e) {
            throw wrapper.valuehandlerReadError( e ) ;
        }
    }

    @InfoMethod
    private void repositoryIdString( String str ) { } 

    @InfoMethod
    private void valueClass( Class cls ) { }

    @InfoMethod
    private void noProxyInterfaces() { }

    @CdrRead
    public Serializable read_value(Class expectedType) {
        Object value = null ;
        int vType = readValueTag();
        if (vType == 0) {
            return null;
        }

        if (vType == 0xffffffff) {
            value = handleIndirection();
        } else {
            ClassInfoCache.ClassInfo cinfo = null ;
            if (expectedType != null) {
                cinfo = ClassInfoCache.get(expectedType);
            }

            int indirection = get_offset() - 4;

            
            
            boolean saveIsChunked = isChunked;
            isChunked = repIdUtil.isChunkedEncoding(vType);

            String codebase_URL = null;
            if (repIdUtil.isCodeBasePresent(vType)) {
                codebase_URL = read_codebase_URL();
            }

            
            String repositoryIDString = readRepositoryIds(vType, expectedType,
                cinfo, null);
            repositoryIdString( repositoryIDString ) ;

            
            
            start_block();

            
            
            end_flag--;
            if (isChunked) {
                chunkedValueNestingLevel--;
            }

            if (repositoryIDString.equals(repIdStrs.getWStringValueRepId())) {
                value = read_wstring();
            } else if (repositoryIDString.equals(repIdStrs.getClassDescValueRepId())) {
                value = readClass();
            } else {
                Class valueClass = expectedType;

                
                
                if (valueClass == null || !repositoryIDString.equals(repIdStrs.createForAnyType(expectedType,cinfo))) {

                    valueClass = getClassFromString(repositoryIDString, codebase_URL, expectedType);
                    cinfo = ClassInfoCache.get( valueClass ) ;
                }

                valueClass( valueClass ) ;

                if (valueClass == null) {
                    
                    
                    
                    
                    RepositoryIdInterface repositoryID = repIdStrs.getFromString(repositoryIDString);

                    throw wrapper.couldNotFindClass(repositoryID.getClassName()) ;
                }

                if (cinfo.isEnum()) {
                    final Class enumClass = ClassInfoCache.getEnumClass( cinfo, 
                        valueClass ) ;
                    String enumValue = read_string() ;
                    value = Enum.valueOf( enumClass, enumValue ) ;
                } else if (valueClass != null && cinfo.isAIDLEntity(valueClass)) {
                    value = readIDLValue(indirection, repositoryIDString,
                        valueClass, cinfo, codebase_URL);
                } else {
                    value = readRMIIIOPValueType( indirection,
                        valueClass, repositoryIDString ) ;
                }
            }

            
            
            
            
            handleEndOfValue();

            
            
            
            readEndTag();

            
            if (valueCache == null) {
                valueCache = new CacheTable<Object>("Input valueCache", orb,
                    false);
            }
            valueCache.put(value, indirection);

            
            
            
            
            
            isChunked = saveIsChunked;
            start_block();
        }

        
        if (value.getClass()==EnumDesc.class) {
            EnumDesc desc = EnumDesc.class.cast( value ) ;

            Class cls = null ;
            try {
                cls = JDKBridge.loadClass( desc.className, null, null ) ;
            } catch (ClassNotFoundException cnfe) {
                throw wrapper.enumClassNotFound( cnfe, desc.className ) ;
            }

            
            Class current = cls ;
            while (current != null) {
                if (current.isEnum()) {
                    break ;
                }
                current = current.getSuperclass() ;
            }

            if (current != null) {
                value = Enum.valueOf( current, desc.value ) ;
            } else {
                throw wrapper.couldNotUnmarshalEnum( desc.className,
                    desc.value ) ;
            }
        }

        
        if (value.getClass()==ProxyDesc.class) {
            ProxyDesc desc = ProxyDesc.class.cast( value ) ;
            int numberOfInterfaces = desc.interfaces.length;

            
            if (numberOfInterfaces==0) {
                noProxyInterfaces() ;
                return null;
            }

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                RMIClassLoader.loadProxyClass( desc.codebase, desc.interfaces,
                    value.getClass().getClassLoader()) ;
            } catch (ClassNotFoundException cnfe) {
                throw wrapper.proxyClassNotFound( cnfe,
                    getInterfacesList(desc.interfaces)) ;
            } catch (MalformedURLException mue) {
                throw wrapper.malformedProxyUrl( mue,
                    getInterfacesList(desc.interfaces), desc.codebase) ;
            }

            Class[] list = new Class[desc.interfaces.length];
            for(int i=0; i < numberOfInterfaces; ++i) {
                try {
                    list[i] = JDKBridge.loadClass(desc.interfaces[i],
                        desc.codebase, cl);
                 } catch (ClassNotFoundException cnfe) {
                     throw wrapper.proxyClassNotFound(cnfe, desc.interfaces[i]);
                 }
            }

            try {
                value = Proxy.newProxyInstance(cl, list, desc.handler);
            } catch (IllegalArgumentException iage) {
                throw wrapper.proxyWithIllegalArgs(iage);
            } catch (NullPointerException npe) {
                throw wrapper.emptyProxyInterfaceList(npe);
            }
        }

        return (java.io.Serializable)value;
    }

    private List<String> getInterfacesList(String [] interfaces) {
        return Arrays.asList(interfaces);
    }

    @CdrRead
    @SuppressWarnings("deprecation")
    public Serializable read_value(BoxedValueHelper factory) {

        
        int vType = readValueTag();

        if (vType == 0) {
            return null;
        } else if (vType == 0xffffffff) { 
            int indirection = read_long() + get_offset() - 4;
            if (valueCache != null && valueCache.containsVal(indirection)) {
                Serializable cachedValue = 
                       (Serializable)valueCache.getKey(indirection);
                return cachedValue;
            } else {
                throw new IndirectionException(indirection);
            }
        } else {
            int indirection = get_offset() - 4;

            boolean saveIsChunked = isChunked;
            isChunked = repIdUtil.isChunkedEncoding(vType);

            java.lang.Object value = null;

            String codebase_URL = null;                 
            if (repIdUtil.isCodeBasePresent(vType)){
                codebase_URL = read_codebase_URL();
            }

            
            String repositoryIDString
                = readRepositoryIds(vType, null, null, null, factory);

            
            if (!repositoryIDString.equals(factory.get_id())) {
                factory = Utility.getHelper(null, codebase_URL, repositoryIDString);
            }

            start_block();
            end_flag--;
            if (isChunked) {
                chunkedValueNestingLevel--;
            }
            
            if (factory instanceof com.sun.org.omg.CORBA.portable.ValueHelper) {
                value = readIDLValueWithHelper(
                    (com.sun.org.omg.CORBA.portable.ValueHelper)factory, indirection);
            } else {
                valueIndirection = indirection;  
                value = factory.read_value(parent);
            }

            handleEndOfValue();
            readEndTag();

            
            if (valueCache == null) {
                valueCache = new CacheTable<Object>("Input valueCache", orb, false);
            }
            valueCache.put(value, indirection);
        
            
            isChunked = saveIsChunked;
            start_block();

            return (java.io.Serializable)value;
        }
    }

    @SuppressWarnings({"deprecation"})
    private boolean isCustomType(@SuppressWarnings("deprecation") com.sun.org.omg.CORBA.portable.ValueHelper helper) {
        try{
            TypeCode tc = helper.get_type();
            int kind = tc.kind().value();
            if (kind == TCKind._tk_value) {
                return (tc.type_modifier() == org.omg.CORBA.VM_CUSTOM.value);
            }
        } catch(BadKind ex) {
            throw wrapper.badKind(ex) ;
        }

        return false;
    }

    
    
    
    
    @CdrRead
    public java.io.Serializable read_value(java.io.Serializable value) {

        
        if (valueCache == null) {
            valueCache = new CacheTable<Object>("Input valueCache", orb, false);
        }
        valueCache.put(value, valueIndirection);

        if (value instanceof StreamableValue) {
            ((StreamableValue) value)._read(parent);
        } else if (value instanceof CustomValue) {
            ((CustomValue) value).unmarshal(parent);
        }
                        
        return value;
    }

    @CdrRead
    public java.io.Serializable read_value(java.lang.String repositoryId) {

        
        

        
        int vType = readValueTag();

        if (vType == 0) {
            return null;
        } else if (vType == 0xffffffff) { 
            int indirection = read_long() + get_offset() - 4;
            if (valueCache != null && valueCache.containsVal(indirection))
                {
                    java.io.Serializable cachedValue = 
                          (java.io.Serializable)valueCache.getKey(indirection);
                    return cachedValue;
                }
            else {
                throw new IndirectionException(indirection);
            }
        } else {
            int indirection = get_offset() - 4;

            

            boolean saveIsChunked = isChunked;
            isChunked = repIdUtil.isChunkedEncoding(vType);

            java.lang.Object value = null;

            String codebase_URL = null;                 
            if (repIdUtil.isCodeBasePresent(vType)){
                codebase_URL = read_codebase_URL();
            }

            
            String repositoryIDString
                = readRepositoryIds(vType, null, null, repositoryId);

            ValueFactory factory = 
               Utility.getFactory(null, codebase_URL, orb, repositoryIDString);

            start_block();
            end_flag--;
            if (isChunked) {
                chunkedValueNestingLevel--;
            }

            valueIndirection = indirection;  
            value = factory.read_value(parent);

            handleEndOfValue();
            readEndTag();

            
            if (valueCache == null) {
                valueCache = new CacheTable<Object>("Input valueCache", orb, false);
            }
            valueCache.put(value, indirection);
        
            
            isChunked = saveIsChunked;
            start_block();

            return (java.io.Serializable)value;
        }               
    }

    @InfoMethod
    private void readClassCodebases( String codebases, String repoId ) { }

    @CdrRead
    private Class<?> readClass() {

        String codebases, classRepId;

        if (orb == null ||
            ORBVersionFactory.getFOREIGN().equals(orb.getORBVersion()) ||
            ORBVersionFactory.getNEWER().compareTo(orb.getORBVersion()) <= 0) {

            codebases = (String)read_value(java.lang.String.class);
            classRepId = (String)read_value(java.lang.String.class);
        } else {
            
            
            classRepId = (String)read_value(java.lang.String.class);
            codebases = (String)read_value(java.lang.String.class);
        }

        readClassCodebases( codebases, classRepId ) ;

        Class<?> cl = null;

        RepositoryIdInterface repositoryID 
            = repIdStrs.getFromString(classRepId);
        
        try {
            cl = repositoryID.getClassFromType(codebases);
        } catch(ClassNotFoundException cnfe) {
            throw wrapper.cnfeReadClass( cnfe, repositoryID.getClassName() ) ;
        } catch(MalformedURLException me) {
            throw wrapper.malformedUrl( 
                me, repositoryID.getClassName(), codebases ) ;
        }

        return cl;
    }

    @SuppressWarnings({"deprecation"})
    @CdrRead
    private java.lang.Object readIDLValueWithHelper(
        com.sun.org.omg.CORBA.portable.ValueHelper helper, int indirection) 
    {
        
        Method readMethod;
        try {
            readMethod = helper.getClass().getDeclaredMethod(K_READ_METHOD,
                org.omg.CORBA.portable.InputStream.class, helper.get_class());
        }
        catch(NoSuchMethodException nsme) { 
            java.lang.Object result = helper.read_value(parent);
            return result;
        }

        
        
        java.lang.Object val = null;
        try {
            val = helper.get_class().newInstance();
        } catch(java.lang.InstantiationException ie) {
            throw wrapper.couldNotInstantiateHelper( ie,
                helper.get_class() ) ;
        } catch(IllegalAccessException iae){ 
            
            
            
            
            
            
            return helper.read_value(parent);
        }

        
        if (valueCache == null) {
            valueCache = new CacheTable<Object>("Input valueCache", orb, false);
        }
        valueCache.put(val, indirection);

        
        if (val instanceof CustomMarshal && isCustomType(helper)) {
            ((CustomMarshal)val).unmarshal(parent);
            return val;
        }

        
        try {
            readMethod.invoke(helper, parent, val );
            return val;
        } catch(IllegalAccessException iae2) {
            throw wrapper.couldNotInvokeHelperReadMethod( iae2,
                helper.get_class() ) ;
        } catch(InvocationTargetException ite){
            throw wrapper.couldNotInvokeHelperReadMethod( ite,
                helper.get_class() ) ;
        }
    }

    @CdrRead
    private java.lang.Object readBoxedIDLEntity(Class<?> clazz, String codebase)
    {
        Class<?> cls = null ;

        try {
            ClassLoader clazzLoader = clazz.getClassLoader();

            cls = Utility.loadClassForClass(clazz.getName()+"Helper", codebase,
                clazzLoader, clazz, clazzLoader);
            final Class<?> helperClass = cls ;

            
            
            
            Method readMethod = null;
            try {
                readMethod = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Method>() {
                    @SuppressWarnings("unchecked")
                        public Method run() throws NoSuchMethodException {
                            return helperClass.getDeclaredMethod(K_READ_METHOD,
                                org.omg.CORBA.portable.InputStream.class ) ;
                        }
                    }
                );
            } catch (PrivilegedActionException pae) {
                
                throw (NoSuchMethodException)pae.getException();
            }

            return readMethod.invoke(null, parent);
        } catch (ClassNotFoundException cnfe) {
            throw wrapper.couldNotInvokeHelperReadMethod( cnfe, cls ) ;
        } catch(NoSuchMethodException nsme) {
            throw wrapper.couldNotInvokeHelperReadMethod( nsme, cls ) ;
        } catch(IllegalAccessException iae) {
            throw wrapper.couldNotInvokeHelperReadMethod( iae, cls ) ;
        } catch(InvocationTargetException ite) {
            throw wrapper.couldNotInvokeHelperReadMethod( ite, cls ) ;
        }
    }

    @CdrRead
    @SuppressWarnings({"deprecation", "deprecation"})
    private java.lang.Object readIDLValue(int indirection, String repId, 
        Class<?> clazz, ClassInfoCache.ClassInfo cinfo, String codebase)
    {                                   
        ValueFactory factory ;

        
        
        
        
        
        
        
        
        
        try {
            
            factory = Utility.getFactory(clazz, codebase, orb, repId);
        } catch (MARSHAL marshal) {
            wrapper.marshalErrorInReadIDLValue( marshal ) ;

            
            if (!cinfo.isAStreamableValue(clazz) && 
                !cinfo.isACustomValue(clazz) && cinfo.isAValueBase(clazz)) {

                
                BoxedValueHelper helper = Utility.getHelper(clazz, codebase, 
                    repId);
                if (helper instanceof com.sun.org.omg.CORBA.portable.ValueHelper) {
                    return readIDLValueWithHelper(
                            (com.sun.org.omg.CORBA.portable.ValueHelper) helper,
                                indirection);
                } else {
                    return helper.read_value(parent);
                }
            } else {
                
                
                return readBoxedIDLEntity(clazz, codebase);
            }
        }

        
        valueIndirection = indirection;  
        return factory.read_value(parent);
    }

    @InfoMethod
    private void endTag( int endTag ) { }

    @InfoMethod
    private void chunkedNestingLevel( int nl ) { }

    @InfoMethod
    private void endFlag( int value ) { }

    
    @CdrRead
    private void readEndTag() {
        if (isChunked) {
            
            int anEndTag = read_long();
            endTag( anEndTag ) ;

            
            
            
            
            
            if (anEndTag >= 0) {
                throw wrapper.positiveEndTag( anEndTag, get_offset() - 4 ) ;
            }

            
            
            
            
            if (orb == null ||
                ORBVersionFactory.getFOREIGN().equals(orb.getORBVersion()) ||
                ORBVersionFactory.getNEWER().compareTo(orb.getORBVersion()) <= 0) {

                
                
                
                if (anEndTag < chunkedValueNestingLevel) {
                    throw wrapper.unexpectedEnclosingValuetype( anEndTag, chunkedValueNestingLevel );
                }

                
                
                
                
                
                
                if (anEndTag != chunkedValueNestingLevel) {
                    byteBuffer.position(byteBuffer.position() - 4);
                }
            } else {
                
                
                
                
                if (anEndTag != end_flag) {
                    byteBuffer.position(byteBuffer.position() - 4);
                }
            }

            
            
            chunkedValueNestingLevel++;
            chunkedNestingLevel( chunkedValueNestingLevel ) ;
        }

        
        end_flag++;
        endFlag( end_flag ) ;
    }

    protected int get_offset() {
        return byteBuffer.position();
    }

    @InfoMethod
    private void unreadLastLong() { }

    @CdrRead
    private void start_block() {
        
        if (!isChunked) {
            return;
        }

        
        
        blockLength = MAX_BLOCK_LENGTH;

        blockLength = read_long();

        
        

        if (blockLength > 0 && blockLength < MAX_BLOCK_LENGTH) {
            blockLength += get_offset();  
        } else {
            blockLength = MAX_BLOCK_LENGTH;
            byteBuffer.position(byteBuffer.position() - 4);
        }
    }

    @InfoMethod
    private void peekNextLong( long val ) { }

    
    
    
    
    
    
    @CdrRead
    private void handleEndOfValue() {
        
        
        if (!isChunked) {
            return;
        }

        
        while (blockLength != MAX_BLOCK_LENGTH) {
            end_block();
            start_block();
        }

        

        
        
        
        
        
        int nextLong = read_long();
        peekNextLong( nextLong ) ;
        byteBuffer.position(byteBuffer.position() - 4);

        
        
        
        
        
        if (nextLong < 0) {
            return;
        }

        if (nextLong == 0 || nextLong >= MAX_BLOCK_LENGTH) {

            
            
            
            
            
            
            
            
            
            
            
            read_value();
            handleEndOfValue();
        } else {
            
            
            
            
            throw wrapper.couldNotSkipBytes( nextLong , get_offset() ) ;
        }
    }

    @CdrRead
    private void end_block() {
        
        if (blockLength != MAX_BLOCK_LENGTH) {
            if (blockLength == get_offset()) {
                
                blockLength = MAX_BLOCK_LENGTH;
            } else {
                
                
                if (blockLength > get_offset()) {
                    skipToOffset(blockLength);
                } else {
                    throw wrapper.badChunkLength( blockLength, get_offset() ) ;
                }
            }
        }
    }
    
    @CdrRead
    private int readValueTag(){
        
        return read_long();
    }

    public org.omg.CORBA.ORB orb() {
        return orb;    
    }

    

    public final void read_boolean_array(boolean[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_boolean();
        }
    }

    public final void read_char_array(char[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_char();
        }
    }

    public final void read_wchar_array(char[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_wchar();
        }
    }

    public final void read_short_array(short[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_short();
        }
    }

    public final void read_ushort_array(short[] value, int offset, int length) {
        read_short_array(value, offset, length);
    }

    public final void read_long_array(int[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_long();
        }
    }

    public final void read_ulong_array(int[] value, int offset, int length) {
        read_long_array(value, offset, length);
    }

    public final void read_longlong_array(long[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_longlong();
        }
    }

    public final void read_ulonglong_array(long[] value, int offset, int length) {
        read_longlong_array(value, offset, length);
    }

    public final void read_float_array(float[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_float();
        }
    }

    public final void read_double_array(double[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_double();
        }
    }

    public final void read_any_array(org.omg.CORBA.Any[] value, int offset, int length) {
        for(int i=0; i < length; i++) {
            value[i+offset] = read_any();
        }
    }

    
    
    


    @CdrRead
    private String read_repositoryIds() {
                
        
        int numRepIds = read_long();
        if (numRepIds == 0xffffffff) {
            int indirection = read_long() + get_offset() - 4;
            if (repositoryIdCache != null && repositoryIdCache.containsVal(indirection)) {
                return repositoryIdCache.getKey(indirection);
            } else {
                throw wrapper.unableToLocateRepIdArray(indirection);
            }
        } else {
            
            int indirection = get_offset(); 
            String repID = read_repositoryId();
            if (repositoryIdCache == null) {
                repositoryIdCache = new CacheTable<String>("Input repositoryIdCache", orb, false);
            }
            repositoryIdCache.put(repID, indirection);

            
            
            for (int i = 1; i < numRepIds; i++) {
                read_repositoryId();
            }
                
            return repID;
        }
    }

    @CdrRead
    private String read_repositoryId() {
        String result = readStringOrIndirection(true);
        if (result == null) { 
            int indirection = read_long() + get_offset() - 4;

            if (repositoryIdCache != null) {
                result = repositoryIdCache.getKey(indirection);
            }
        } else {
            if (repositoryIdCache == null) {
                repositoryIdCache = new CacheTable<String>("Input repositoryIdCache", orb, false);
            }
            repositoryIdCache.put(result, stringIndirection);
        }

        if (result != null) {
            return result;
        }

        throw wrapper.badRepIdIndirection(byteBuffer.position()) ;                              
    }

    @CdrRead
    private String read_codebase_URL() {
        String result = readStringOrIndirection(true);
        if (result == null) { 
            int indirection = read_long() + get_offset() - 4;

            if (codebaseCache != null) {
                result = codebaseCache.getKey(indirection) ;
            }
        } else {
            if (codebaseCache == null) {
                codebaseCache = new CacheTable<String>("Input codebaseCache", orb, false);
            }
            codebaseCache.put(result, stringIndirection);
        }

        if (result != null) {
            return result;
        }

        throw wrapper.badCodebaseIndirection(byteBuffer.position()) ;                            
    }

    

    public java.lang.Object read_Abstract () {
        return read_abstract_interface();
    }

    public java.io.Serializable read_Value () {
        return read_value();
    }

    public void read_any_array (org.omg.CORBA.AnySeqHolder seq, int offset, int length) {
        read_any_array(seq.value, offset, length);
    }

    public void read_boolean_array (org.omg.CORBA.BooleanSeqHolder seq, int offset, int length) {
        read_boolean_array(seq.value, offset, length);
    }

    public void read_char_array (org.omg.CORBA.CharSeqHolder seq, int offset, int length) {
        read_char_array(seq.value, offset, length);
    }

    public void read_wchar_array (org.omg.CORBA.WCharSeqHolder seq, int offset, int length) {
        read_wchar_array(seq.value, offset, length);
    }

    public void read_octet_array (org.omg.CORBA.OctetSeqHolder seq, int offset, int length) {
        read_octet_array(seq.value, offset, length);
    }

    public void read_short_array (org.omg.CORBA.ShortSeqHolder seq, int offset, int length) {
        read_short_array(seq.value, offset, length);
    }

    public void read_ushort_array (org.omg.CORBA.UShortSeqHolder seq, int offset, int length) {
        read_ushort_array(seq.value, offset, length);
    }

    public void read_long_array (org.omg.CORBA.LongSeqHolder seq, int offset, int length) {
        read_long_array(seq.value, offset, length);
    }

    public void read_ulong_array (org.omg.CORBA.ULongSeqHolder seq, int offset, int length) {
        read_ulong_array(seq.value, offset, length);
    }

    public void read_ulonglong_array (org.omg.CORBA.ULongLongSeqHolder seq, int offset, int length) {
        read_ulonglong_array(seq.value, offset, length);
    }

    public void read_longlong_array (org.omg.CORBA.LongLongSeqHolder seq, int offset, int length) {
        read_longlong_array(seq.value, offset, length);
    }

    public void read_float_array (org.omg.CORBA.FloatSeqHolder seq, int offset, int length) {
        read_float_array(seq.value, offset, length);
    }

    public void read_double_array (org.omg.CORBA.DoubleSeqHolder seq, int offset, int length) {
        read_double_array(seq.value, offset, length);
    }

    public java.math.BigDecimal read_fixed(short digits, short scale) {
        
        StringBuffer buffer = read_fixed_buffer();
        if (digits != buffer.length()) {
            throw wrapper.badFixed(digits, buffer.length());
        }
        buffer.insert(digits - scale, '.');
        return new BigDecimal(buffer.toString());
    }

    
    public java.math.BigDecimal read_fixed() {
        return new BigDecimal(read_fixed_buffer().toString());
    }

    
    
    
    
    
    
    
    private StringBuffer read_fixed_buffer() {
        StringBuffer buffer = new StringBuffer(64);
        byte doubleDigit;
        int firstDigit;
        int secondDigit;
        boolean wroteFirstDigit = false;
        boolean more = true;
        while (more) {
            doubleDigit = this.read_octet();
            firstDigit = (doubleDigit & 0xf0) >> 4;
            secondDigit = doubleDigit & 0x0f;
            if (wroteFirstDigit || firstDigit != 0) {
                buffer.append(Character.forDigit(firstDigit, 10));
                wroteFirstDigit = true;
            }
            if (secondDigit == 12) {
                
                if ( ! wroteFirstDigit) {
                    
                    return new StringBuffer("0.0");
                } else {
                    
                    
                }
                more = false;
            } else if (secondDigit == 13) {
                
                buffer.insert(0, '-');
                more = false;
            } else {
                buffer.append(Character.forDigit(secondDigit, 10));
                wroteFirstDigit = true;
            }
        }
        return buffer;
    }

    private final static String _id = "IDL:omg.org/CORBA/DataInputStream:1.0";
    private final static String[] _ids = { _id };

    public String[] _truncatable_ids() {
        if (_ids == null) {
            return null;
        }

        return _ids.clone();
    }

    public int getBufferLength() {
        return byteBuffer.limit();
    }

    public void setBufferLength(int value) {
        byteBuffer.limit(value);
    }

    public void setIndex(int value) {
        byteBuffer.position(value);
    }

    @Override
    public ByteOrder getByteOrder() {
        return byteBuffer.order();
    }

    public void orb(org.omg.CORBA.ORB orb) {
        this.orb = (ORB)orb;
    }

    public BufferManagerRead getBufferManager() {
        return bufferManagerRead;
    }

    @CdrRead
    private void skipToOffset(int offset) {                                                        
        
        int len = offset - get_offset();

        int n = 0;

        while (n < len) {
            int wanted;
            int bytes;

            if (!byteBuffer.hasRemaining()) grow(1, 1);
            int avail = byteBuffer.remaining();

            wanted = len - n;
            bytes = (wanted < avail) ? wanted : avail;
            byteBuffer.position(byteBuffer.position() + bytes);
            n += bytes;
        }
    }


    

    protected MarkAndResetHandler markAndResetHandler = null;

    protected class StreamMemento {
        
        
        private int blockLength_;
        private int end_flag_;
        private int chunkedValueNestingLevel_;
        private int valueIndirection_;
        private int stringIndirection_;
        private boolean isChunked_;
        private ValueHandler valueHandler_;
        private ByteBuffer byteBuffer_;
        private boolean specialNoOptionalDataState_;

        public StreamMemento() {
            blockLength_ = blockLength;
            end_flag_ = end_flag;
            chunkedValueNestingLevel_ = chunkedValueNestingLevel;
            valueIndirection_ = valueIndirection;
            stringIndirection_ = stringIndirection;
            isChunked_ = isChunked;
            valueHandler_ = valueHandler;
            specialNoOptionalDataState_ = specialNoOptionalDataState;
            byteBuffer_ = byteBuffer.duplicate();
        }
    }

    public java.lang.Object createStreamMemento() {
        return new StreamMemento();
    }

    public void restoreInternalState(java.lang.Object streamMemento) {

        StreamMemento mem = (StreamMemento)streamMemento;

        blockLength = mem.blockLength_;
        end_flag = mem.end_flag_;
        chunkedValueNestingLevel = mem.chunkedValueNestingLevel_;
        valueIndirection = mem.valueIndirection_;
        stringIndirection = mem.stringIndirection_;
        isChunked = mem.isChunked_;
        valueHandler = mem.valueHandler_;
        specialNoOptionalDataState = mem.specialNoOptionalDataState_;
        byteBuffer = mem.byteBuffer_;
    }

    public int getPosition() {
        return get_offset();
    }

    public void mark(int readlimit) {
        markAndResetHandler.mark(this);
    }

    public void reset() {
        markAndResetHandler.reset();
    }

    

    
    
    
    
    CodeBase getCodeBase() {
        return parent.getCodeBase();
    }

    
    @CdrRead
    private Class<?> getClassFromString(String repositoryIDString,
                                     String codebaseURL,
                                     Class<?> expectedType)
    {
        RepositoryIdInterface repositoryID = repIdStrs.getFromString(repositoryIDString);

        ClassCodeBaseHandler ccbh = orb.classCodeBaseHandler() ;
        if (ccbh != null) {
            String className = repositoryID.getClassName() ;
            Class<?> result = ccbh.loadClass( codebaseURL, className ) ;

            if (result != null) {
                return result ;
            }
        }

        try {
            try {
                
                
                return repositoryID.getClassFromType(expectedType,
                                                     codebaseURL);
            } catch (ClassNotFoundException cnfeOuter) {
                
                try {
                  
                    if (getCodeBase() == null) {
                        return null; 
                    }
                    
                    
                    codebaseURL = getCodeBase().implementation(repositoryIDString);
                    
                    
                    
                    if (codebaseURL == null) {
                        return null;
                    }
                    
                    return repositoryID.getClassFromType(expectedType,
                                                         codebaseURL);
                } catch (ClassNotFoundException cnfeInner) {
                    
                    return null;
                }
            }
        } catch (MalformedURLException mue) {
            
            throw wrapper.malformedUrl( mue, repositoryIDString, codebaseURL ) ;
        }
    }


    
    char[] getConvertedChars(int numBytes,
                             CodeSetConversion.BTCConverter converter) {


        if (byteBuffer.remaining() >= numBytes) {
            
            
            
            int pos = byteBuffer.position();
            char[] result = converter.getChars(byteBuffer.slice(), 0, numBytes);
            byteBuffer.position(pos + numBytes);
            return result;
        } else {
            
            
            
            byte[] bytes = new byte[numBytes];

            
            
            
            
            
            
            read_octet_array(bytes, 0, bytes.length);

            return converter.getChars(bytes, 0, numBytes);
        }
    }

    protected CodeSetConversion.BTCConverter getCharConverter() {
        if (charConverter == null) {
            charConverter = parent.createCharBTCConverter();
        }
        
        return charConverter;
    }

    protected CodeSetConversion.BTCConverter getWCharConverter() {
        if (wcharConverter == null) {
            wcharConverter = parent.createWCharBTCConverter();
        }
    
        return wcharConverter;
    }

    

    void alignOnBoundary(int octetBoundary) {
        int needed = computeAlignment(byteBuffer.position(), octetBoundary);

        if (byteBuffer.position() + needed <= byteBuffer.limit())
        {
            byteBuffer.position(byteBuffer.position() + needed);
        }
    }

    public void resetCodeSetConverters() {
        charConverter = null;
        wcharConverter = null;
    }

    @InfoMethod
    private void valueTag( int value ) { }

    @CdrRead
    public void start_value() {
        
        int vType = readValueTag();
        valueTag( vType ) ;

        if (vType == 0) {
            
            
            
            
            
            
            
            specialNoOptionalDataState = true;

            return;
        }

        if (vType == 0xffffffff) {
            
            throw wrapper.customWrapperIndirection( );
        }

        if (repIdUtil.isCodeBasePresent(vType)) {
            throw wrapper.customWrapperWithCodebase();
        }
                        
        if (repIdUtil.getTypeInfo(vType) 
            != RepositoryIdUtility.SINGLE_REP_TYPE_INFO) {
            throw wrapper.customWrapperNotSingleRepid( );
        }


        
        
        read_repositoryId();

        
        
        
        
        start_block();
        end_flag--;
        chunkedValueNestingLevel--;
    }

    @CdrRead
    public void end_value() {

        if (specialNoOptionalDataState) {
            specialNoOptionalDataState = false;
            return;
        }

        handleEndOfValue();
        readEndTag();

        
        
        
        

        
        start_block();
    }

    @Override
    @CdrRead
    public void close() throws IOException {

        
        getBufferManager().close(byteBuffer);

        if (byteBuffer != null) {

            
            ByteBufferPool byteBufferPool = orb.getByteBufferPool();
            byteBufferPool.releaseByteBuffer(byteBuffer);
            byteBuffer = null;
        }
    }
}
