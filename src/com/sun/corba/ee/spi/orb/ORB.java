


package xxxx;



@OrbLifeCycle
@ManagedObject
@Description( "The Main ORB Implementation object" ) 
@AMXMetadata( type="ORB-Root" )
public abstract class ORB extends com.sun.corba.ee.org.omg.CORBA.ORB
    implements TypeCodeFactory
{   
    static {
        MethodMonitorFactoryDefaults.addPrefix( "com.sun.corba.ee", "ORB" ) ;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    public static final boolean orbInitDebug = AccessController.doPrivileged( 
        new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return Boolean.getBoolean( ORBConstants.INIT_DEBUG_PROPERTY );
            }
        }
    ) ;

    
    
    
    
    
    
    
    
    
    
    

    @Transport
    public boolean transportDebugFlag = false ;

    @Subcontract
    public boolean subcontractDebugFlag = false ;

    @Osgi
    public boolean osgiDebugFlag = false ;

    @Poa
    public boolean poaDebugFlag = false ;
    
    @PoaFSM
    public boolean poaFSMDebugFlag = false ;

    @Orbd
    public boolean orbdDebugFlag = false ;

    @Naming
    public boolean namingDebugFlag = false ;

    @TraceServiceContext
    public boolean serviceContextDebugFlag = false ;

    @TransientObjectManager
    public boolean transientObjectManagerDebugFlag = false ;

    @Shutdown
    public boolean shutdownDebugFlag = false;

    @Giop
    public boolean giopDebugFlag = false;

    public boolean giopSizeDebugFlag = false;
    public boolean giopReadDebugFlag = false;

    @TraceInterceptor
    public boolean interceptorDebugFlag = false ;

    @Folb
    public boolean folbDebugFlag = false ;

    public boolean cdrCacheDebugFlag = false ;

    @Cdr
    public boolean cdrDebugFlag = false ;

    @StreamFormatVersion
    public boolean streamFormatVersionDebugFlag = false ;

    @TraceValueHandler
    public boolean valueHandlerDebugFlag = false ;

    public boolean mbeanDebugFlag = false ;
    public boolean mbeanFineDebugFlag = false ;
    public boolean mbeanRuntimeDebugFlag = false ;

    @OrbLifeCycle
    public boolean orbLifecycleDebugFlag = false ;

    public boolean operationTraceDebugFlag = false ;

    @DynamicType
    public boolean dynamicTypeDebugFlag = false ;

    @IsLocal
    public boolean isLocalDebugFlag = false ;

    @ManagedAttribute
    @Description( "The current settings of the ORB debug flags" )
    private Map<String,Boolean> getDebugFlags() {
        Map<String,Boolean> result = new HashMap<String,Boolean>() ;
        for (Field fld : this.getClass().getFields()) {
            if (fld.getName().endsWith("DebugFlag")) {
                Boolean value = false ;
                try {
                    value = fld.getBoolean( this );
                    result.put( fld.getName(), value ) ;
                } catch (Exception exc) {
                }
            }
        }

        return result ;
    }

    @InfoMethod
    private void mbeanRegistrationSuspended(String oRBId) { }

    public enum DebugFlagResult { OK, BAD_NAME }

    @ManagedOperation
    @Description( "Enable debugging for several ORB debug flags")
    public DebugFlagResult setDebugFlags( String... names ) {
        return setDebugFlags( true, names ) ;
    }

    @ManagedOperation
    @Description( "Enable debugging for a particular ORB debug flag")
    public DebugFlagResult setDebugFlag( String name ) {
        return setDebugFlag( name, true ) ;
    }

    @ManagedOperation
    @Description( "Disable debugging for several ORB debug flags")
    public DebugFlagResult clearDebugFlags( String... names ) {
        return setDebugFlags( false, names ) ;
    }

    @ManagedOperation
    @Description( "Disable debugging for a particular ORB debug flag")
    public DebugFlagResult clearDebugFlag( String name ) {
        return setDebugFlag( name, false ) ;
    }
   
    private DebugFlagResult setDebugFlags( boolean flag, String... names ) {
        DebugFlagResult res = DebugFlagResult.OK ;
        for (String name : names) {
            DebugFlagResult lres = setDebugFlag( name, flag ) ;
            if (lres == DebugFlagResult.BAD_NAME) {
                res = DebugFlagResult.BAD_NAME ;
            }
        }
        return res ;
    }

    private DebugFlagResult setDebugFlag( String name, boolean value ) {
        try {
            Field fld = this.getClass().getField( name + "DebugFlag" ) ;
            fld.set( this, value ) ;

            Annotation[] annots = fld.getAnnotations() ;
            for (Annotation anno : annots) {
                Class<? extends Annotation> annoClass = anno.annotationType() ;

                if (annoClass.isAnnotationPresent(
                    MethodMonitorGroup.class )) {
                    if (value) {
                        MethodMonitorRegistry.register( annoClass,
                            MethodMonitorFactoryDefaults.dprint() );
                    } else {
                        MethodMonitorRegistry.clear( annoClass ) ;
                    }
                }
            }

            return DebugFlagResult.OK ;
        } catch (Exception exc) {
            return DebugFlagResult.BAD_NAME ;
        }
    }

    
    
    
    protected ManagedObjectManager mom ;
    
    
    
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;
    protected static final OMGSystemException omgWrapper =
        OMGSystemException.self ;

    
    
    private Map<String,TypeCodeImpl> typeCodeMap ;

    private TypeCodeImpl[] primitiveTypeCodeConstants ;

    
    ByteBufferPool byteBufferPool;

    
    WireObjectKeyTemplate wireObjectKeyTemplate;

    
    public abstract boolean isLocalHost( String hostName ) ;
    public abstract boolean isLocalServerId( int subcontractId, int serverId ) ;

    
    public abstract OAInvocationInfo peekInvocationInfo() ;
    public abstract void pushInvocationInfo( OAInvocationInfo info ) ;
    public abstract OAInvocationInfo popInvocationInfo() ;

    @ManagedAttribute
    @Description( "The ORB's transport manager" ) 
    public abstract TransportManager getCorbaTransportManager();

    public abstract LegacyServerSocketManager getLegacyServerSocketManager();

    private static PresentationManager presentationManager = PresentationDefaults.makeOrbPresentationManager();

    private UnaryFunction<String,Class<?>> classNameResolver = defaultClassNameResolver ;
    private ClassCodeBaseHandler ccbHandler = null ;

    @Override
    public synchronized void destroy() {
        typeCodeMap = null ;
        primitiveTypeCodeConstants = null ;
        byteBufferPool = null ;
        wireObjectKeyTemplate = null ;
    }

    
    @ManagedAttribute
    @Description( "The presentation manager, which handles stub creation" ) 
    public static PresentationManager getPresentationManager() 
    {
        
        return presentationManager;
        
    }

    
    public static PresentationManager.StubFactoryFactory 
        getStubFactoryFactory()
    {
    	PresentationManager gPM = getPresentationManager();
        boolean useDynamicStubs = gPM.useDynamicStubs() ;
        return useDynamicStubs ? gPM.getDynamicStubFactoryFactory() : gPM.getStaticStubFactoryFactory();
    }

    
    public abstract InvocationInterceptor getInvocationInterceptor() ;

    
    public abstract void setInvocationInterceptor( 
        InvocationInterceptor interceptor ) ;
    
    protected ORB()
    {

        typeCodeMap = new HashMap<String,TypeCodeImpl>();

        wireObjectKeyTemplate = new WireObjectKeyTemplate(this);
    }

    protected void initializePrimitiveTypeCodeConstants() {
        primitiveTypeCodeConstants = new TypeCodeImpl[] {
            new TypeCodeImpl(this, TCKind._tk_null),    
            new TypeCodeImpl(this, TCKind._tk_void),
            new TypeCodeImpl(this, TCKind._tk_short),           
            new TypeCodeImpl(this, TCKind._tk_long),    
            new TypeCodeImpl(this, TCKind._tk_ushort),  
            new TypeCodeImpl(this, TCKind._tk_ulong),   
            new TypeCodeImpl(this, TCKind._tk_float),   
            new TypeCodeImpl(this, TCKind._tk_double),  
            new TypeCodeImpl(this, TCKind._tk_boolean), 
            new TypeCodeImpl(this, TCKind._tk_char),    
            new TypeCodeImpl(this, TCKind._tk_octet),
            new TypeCodeImpl(this, TCKind._tk_any),     
            new TypeCodeImpl(this, TCKind._tk_TypeCode),        
            new TypeCodeImpl(this, TCKind._tk_Principal),
            new TypeCodeImpl(this, TCKind._tk_objref),  
            null,       
            null,       
            null,       
            new TypeCodeImpl(this, TCKind._tk_string),          
            null,       
            null,       
            null,       
            null,       
            new TypeCodeImpl(this, TCKind._tk_longlong),        
            new TypeCodeImpl(this, TCKind._tk_ulonglong),
            new TypeCodeImpl(this, TCKind._tk_longdouble),
            new TypeCodeImpl(this, TCKind._tk_wchar),           
            new TypeCodeImpl(this, TCKind._tk_wstring), 
            new TypeCodeImpl(this, TCKind._tk_fixed),   
            new TypeCodeImpl(this, TCKind._tk_value),   
            new TypeCodeImpl(this, TCKind._tk_value_box),
            new TypeCodeImpl(this, TCKind._tk_native),  
            new TypeCodeImpl(this, TCKind._tk_abstract_interface)
        } ;
    }

    
    public TypeCodeImpl get_primitive_tc(int kind) 
    {
        try {
            return primitiveTypeCodeConstants[kind] ;
        } catch (Throwable t) {
            throw wrapper.invalidTypecodeKind( t, kind ) ;
        }
    }

    public synchronized void setTypeCode(String id, TypeCodeImpl code) 
    {
        typeCodeMap.put(id, code);
    }

    public synchronized TypeCodeImpl getTypeCode(String id) 
    {
        return typeCodeMap.get(id);
    }

    
    
    
    
    
    
    public abstract void set_parameters( Properties props ) ;

    
    
    public abstract void setParameters( String[] args, Properties props ) ;

    
    @ManagedAttribute
    @Description( "The implementation version of the ORB" )
    public abstract ORBVersion getORBVersion() ;

    public abstract void setORBVersion( ORBVersion version ) ;

    @ManagedAttribute
    @Description( "The IOR used for the Full Value Description" ) 
    public abstract IOR getFVDCodeBaseIOR() ;

    
    public abstract void handleBadServerId( ObjectKey okey ) ;
    public abstract void setBadServerIdHandler( BadServerIdHandler handler ) ;
    public abstract void initBadServerIdHandler() ;
    
    public abstract void notifyORB() ;

    @ManagedAttribute 
    @Description( "The PortableInterceptor Handler" ) 
    public abstract PIHandler getPIHandler() ;

    public abstract void createPIHandler() ;

    
    
    public abstract boolean isDuringDispatch() ;
    public abstract void startingDispatch();
    public abstract void finishedDispatch();

    
    @ManagedAttribute
    @Description( "The transient ServerId of this ORB instance" ) 
    public abstract int getTransientServerId();

    @ManagedAttribute
    @Description( "The registry for all ServerContext factories" ) 
    public abstract ServiceContextFactoryRegistry getServiceContextFactoryRegistry() ;

    @ManagedAttribute
    @Description( "The cache used to opimize marshaling of ServiceContexts" ) 
    public abstract ServiceContextsCache getServiceContextsCache();

    @ManagedAttribute
    @Description( "The RequestDispatcher registry, which contains the request handling code" ) 
    public abstract RequestDispatcherRegistry getRequestDispatcherRegistry();

    @ManagedAttribute
    @Description( "The ORB configuration data" ) 
    public abstract ORBData getORBData() ;

    public abstract void setClientDelegateFactory( ClientDelegateFactory factory ) ;

    @ManagedAttribute
    @Description( "The ClientDelegateFactory, which is used to create the ClientDelegate that represents an IOR" )
    public abstract ClientDelegateFactory getClientDelegateFactory() ;

    public abstract void setCorbaContactInfoListFactory( ContactInfoListFactory factory ) ;

    @ManagedAttribute
    @Description( "The CorbaContactInfoListFactory, which creates the contact info list that represents "
        + "possible endpoints in an IOR" ) 
    public abstract ContactInfoListFactory getCorbaContactInfoListFactory() ;

    
    public abstract void setResolver( Resolver resolver ) ;

    
    @ManagedAttribute
    @Description( "ORB Name resolver" ) 
    public abstract Resolver getResolver() ;

    
    public abstract void setLocalResolver( LocalResolver resolver ) ;

    
    @ManagedAttribute
    @Description( "ORB Local Name resolver" ) 
    public abstract LocalResolver getLocalResolver() ;

    
    public abstract void setURLOperation( Operation stringToObject ) ;

    
    public abstract Operation getURLOperation() ;

    
    public abstract void setINSDelegate( ServerRequestDispatcher insDelegate ) ;

    
    @ManagedAttribute
    @Description( "Finder of Factories for TaggedComponents of IORs" )
    public abstract TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() ;

    @ManagedAttribute
    @Description( "Finder of Factories for TaggedProfiles of IORs" )
    public abstract IdentifiableFactoryFinder<TaggedProfile> 
        getTaggedProfileFactoryFinder() ;

    @ManagedAttribute
    @Description( "Finder of Factories for TaggedProfileTemplates of IORs" )
    public abstract IdentifiableFactoryFinder<TaggedProfileTemplate> 
        getTaggedProfileTemplateFactoryFinder() ;

    @ManagedAttribute
    @Description( "Factory for creating ObjectKeys" )
    public abstract ObjectKeyFactory getObjectKeyFactory() ;

    public abstract void setObjectKeyFactory( ObjectKeyFactory factory ) ;

    

    public static Logger getLogger( String name ) 
    {
        return Logger.getLogger( name, ORBConstants.LOG_RESOURCE_FILE ) ;
    }

    
    
    
    
    @ManagedAttribute
    @Description( "The ByteBuffer pool used in the ORB" ) 
    public ByteBufferPool getByteBufferPool()
    {
        if (byteBufferPool == null)
            byteBufferPool = new ByteBufferPoolImpl(this);

        return byteBufferPool;
    }

    public WireObjectKeyTemplate getWireObjectKeyTemplate() {
        return wireObjectKeyTemplate;
    }

    public abstract void setThreadPoolManager(ThreadPoolManager mgr);

    @ManagedAttribute
    @Description( "The ORB's threadpool manager" ) 
    public abstract ThreadPoolManager getThreadPoolManager();

    @ManagedAttribute
    @Description( "The ORB's object copier manager" ) 
    public abstract CopierManager getCopierManager() ;

    
    @NameValue
    public String getUniqueOrbId()  {
        return "###DEFAULT_UNIQUE_ORB_ID###" ;
    }
    
    
    
    
    @ManagedData
    @Description( "A servant, which implements a remote object in the server" )
    @InheritedAttributes( {
        @InheritedAttribute( methodName="_get_delegate", id="delegate", 
            description="Delegate that implements this servant" ),
        @InheritedAttribute( methodName="_orb", id="orb",
            description="The ORB for this Servant" ),
        @InheritedAttribute( methodName="toString", id="representation",
            description="Representation of this Servant" ),
        @InheritedAttribute( methodName="_all_interfaces", id="typeIds",
            description="The types implemented by this Servant" ) } 
    )
    public interface DummyServant{}

    
    
    

    private ObjectName rootParentObjectName = null ;

    public void setRootParentObjectName( ObjectName oname ) {
        rootParentObjectName = oname ;
    }

    @OrbLifeCycle
    public void createORBManagedObjectManager() {
        if (rootParentObjectName == null) {
            mom = ManagedObjectManagerFactory.createStandalone( "com.sun.corba" ) ;
        } else {
            mom = ManagedObjectManagerFactory.createFederated( rootParentObjectName ) ;
        }

        if (mbeanFineDebugFlag) {
            mom.setRegistrationDebug( ManagedObjectManager.RegistrationDebugLevel.FINE ) ;
        } else if (mbeanDebugFlag) {
            mom.setRegistrationDebug( ManagedObjectManager.RegistrationDebugLevel.NORMAL ) ;
        } else {
            mom.setRegistrationDebug( ManagedObjectManager.RegistrationDebugLevel.NONE ) ;
        }

        mom.addAnnotation( Servant.class, DummyServant.class.getAnnotation( ManagedData.class ) ) ;
        mom.addAnnotation( Servant.class, DummyServant.class.getAnnotation( Description.class ) ) ;
        mom.addAnnotation( Servant.class, DummyServant.class.getAnnotation( InheritedAttributes.class ) ) ;

        mom.setRuntimeDebug( mbeanRuntimeDebugFlag ) ;

        mom.stripPrefix( "com.sun.corba.ee", "com.sun.corba.ee.spi", "com.sun.corba.ee.spi.orb", 
            "com.sun.corba.ee.impl" ) ;

        mom.suspendJMXRegistration() ;

        mbeanRegistrationSuspended( getORBData().getORBId() ) ;

        mom.createRoot( this, getUniqueOrbId() ) ;
    }

    
    
    
    
    
    
    
    
    protected IOR getIOR( org.omg.CORBA.Object obj ) 
    {
        if (obj == null)
            throw wrapper.nullObjectReference() ;

        IOR ior = null ;
        if (StubAdapter.isStub(obj)) {
            org.omg.CORBA.portable.Delegate del = StubAdapter.getDelegate( 
                obj ) ;

            if (del instanceof ClientDelegate) {
                ClientDelegate cdel = (ClientDelegate)del ;
                ContactInfoList ccil = cdel.getContactInfoList() ;
                ior = ccil.getTargetIOR() ;
                if (ior == null)
                    throw wrapper.nullIor() ;

                return ior ;
            } 

            if (obj instanceof ObjectImpl) {
                
                
                ObjectImpl oi = ObjectImpl.class.cast( obj ) ;
                org.omg.CORBA.ORB oiorb = oi._orb() ;

                
                
                
                
                
                
                org.omg.CORBA.portable.OutputStream os = oiorb.create_output_stream() ;
                os.write_Object( obj ) ;
                org.omg.CORBA.portable.InputStream is = os.create_input_stream() ;
                ior = IORFactories.makeIOR( this,  
                    org.omg.CORBA_2_3.portable.InputStream.class.cast( is ) ) ; 
                return ior ;
            } else {
                throw wrapper.notAnObjectImpl() ;
            }
        } else
            throw wrapper.localObjectNotAllowed() ;
    }


    
    public IOR getIOR( org.omg.CORBA.Object obj, boolean connectIfNecessary ) {
        
        
        return getIOR( obj ) ;
    }

    
    public ObjectKeyCacheEntry extractObjectKeyCacheEntry(byte[] objKey) {
        return null ;
    }

    
    public boolean orbIsShutdown() {
        return true ;
    }

    private static UnaryFunction<String,Class<?>> defaultClassNameResolver =
        new UnaryFunction<String,Class<?>>() {
            public Class<?> evaluate( String name ) {
                try {
                    return ORBClassLoader.getClassLoader().loadClass( name ) ;
                } catch (ClassNotFoundException exc) {
                    throw new RuntimeException( exc ) ;
                }
            }

            @Override
            public String toString() {
                return "ORBClassNameResolver" ;
            }
        } ;

    public static UnaryFunction<String,Class<?>> defaultClassNameResolver() {
        return defaultClassNameResolver ;
    }

    public UnaryFunction<String,Class<?>> makeCompositeClassNameResolver(
        final UnaryFunction<String,Class<?>> first,
        final UnaryFunction<String,Class<?>> second ) {

        return new UnaryFunction<String,Class<?>>() {
            public Class<?> evaluate( String className ) {
                Class<?> result = first.evaluate( className ) ;
                if (result == null) {
                    return second.evaluate( className ) ;
                } else {
                    return result ;
                }
            }

            @Override
            public String toString() {
                return "CompositeClassNameResolver[" + first + "," + second + "]" ;
            }
        } ;
    }

    public UnaryFunction<String,Class<?>> classNameResolver() {
        return classNameResolver ;
    }

    public void classNameResolver( UnaryFunction<String,Class<?>> arg ) {
        classNameResolver = arg ;
    }

    public ManagedObjectManager mom() {
        return mom ;
    }

    public ClassCodeBaseHandler classCodeBaseHandler() {
        return ccbHandler ;
    }

    public void classCodeBaseHandler( ClassCodeBaseHandler ccbh ) {
        ccbHandler = ccbh ;
    }

    public abstract ClientInvocationInfo createOrIncrementInvocationInfo() ;
    public abstract ClientInvocationInfo getInvocationInfo();
    public abstract void releaseOrDecrementInvocationInfo();

    public abstract TransportManager getTransportManager();

    
}


