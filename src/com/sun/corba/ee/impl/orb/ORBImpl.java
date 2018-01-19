


package com.sun.corba.ee.impl.orb ;














         

@OrbLifeCycle
@Subcontract
public class ORBImpl extends com.sun.corba.ee.spi.orb.ORB
{
    private boolean set_parameters_called = false ;

    protected TransportManager transportManager;
    protected LegacyServerSocketManager legacyServerSocketManager;

    private ThreadLocal<StackImpl<OAInvocationInfo>>
        OAInvocationInfoStack ;

    private ThreadLocal<StackImpl<ClientInvocationInfo>>
        clientInvocationInfoStack ;

    
    private CodeBase codeBase = null ; 
    private IOR codeBaseIOR = null ;

    
    private final List<Request>  dynamicRequests =
        new ArrayList<Request>();

    private final SynchVariable svResponseReceived = new SynchVariable();


    private final Object runObj = new Object();
    private final Object shutdownObj = new Object();
    private final AtomicInteger numWaiters = new AtomicInteger() ;
    private final Object waitForCompletionObj = new Object();
    private static final byte STATUS_OPERATING = 1;
    private static final byte STATUS_SHUTTING_DOWN = 2;
    private static final byte STATUS_SHUTDOWN = 3;
    private static final byte STATUS_DESTROYED = 4;
    private final ReadWriteLock statueLock = new ReentrantReadWriteLock() ;
    private byte status = STATUS_OPERATING;

    private final java.lang.Object invocationObj = new java.lang.Object();
    private AtomicInteger numInvocations = new AtomicInteger();

    
    
    private ThreadLocal<Boolean> isProcessingInvocation = 
        new ThreadLocal<Boolean> () {
        @Override
            protected Boolean initialValue() {
                return false ;
            }
        };

    
    
    private Map<Class<?>,TypeCodeImpl> typeCodeForClassMap ;

    
    private Map<String,ValueFactory> valueFactoryCache = 
        new HashMap<String,ValueFactory>();

    
    
    
    private ThreadLocal<ORBVersion> orbVersionThreadLocal ; 

    private RequestDispatcherRegistry requestDispatcherRegistry ;

    private CopierManager copierManager ;

    private int transientServerId ;

    private ServiceContextFactoryRegistry serviceContextFactoryRegistry ;

    private ServiceContextsCache serviceContextsCache;

    
    private ResourceFactory<TOAFactory> toaFactory =
        new ResourceFactory<TOAFactory>(
            new NullaryFunction<TOAFactory>()  {
                public TOAFactory evaluate() {
                    return (TOAFactory)requestDispatcherRegistry.getObjectAdapterFactory(
                        ORBConstants.TOA_SCID) ;
                }
            }
        );

    
    private ResourceFactory<POAFactory> poaFactory =
        new ResourceFactory<POAFactory>(
            new NullaryFunction<POAFactory>()  {
                public POAFactory evaluate() {
                    return (POAFactory)requestDispatcherRegistry.getObjectAdapterFactory(
                        ORBConstants.TRANSIENT_SCID) ;
                }
            }
        );

    
    
    private PIHandler pihandler ;

    private ORBData configData ;

    private BadServerIdHandler badServerIdHandler ;

    private ClientDelegateFactory clientDelegateFactory ;

    private ContactInfoListFactory corbaContactInfoListFactory ;

    
    
    
    
    
    
    
    
    
    

    
    private Resolver resolver ;

    
    private LocalResolver localResolver ;

    
    private ServerRequestDispatcher insNamingDelegate ;

    
    
    
    
    private final Object resolverLock = new Object() ;

    
    private Operation urlOperation ;
    private final Object urlOperationLock = new java.lang.Object() ;

    private TaggedComponentFactoryFinder
        taggedComponentFactoryFinder ;

    private IdentifiableFactoryFinder<TaggedProfile>
        taggedProfileFactoryFinder ;

    private IdentifiableFactoryFinder<TaggedProfileTemplate>
        taggedProfileTemplateFactoryFinder ;

    private ObjectKeyFactory objectKeyFactory ;

    private boolean orbOwnsThreadPoolManager = false ;

    private ThreadPoolManager threadpoolMgr;

    private InvocationInterceptor invocationInterceptor ;

    private WeakCache<ByteArrayWrapper, ObjectKeyCacheEntry> objectKeyCache =
        new WeakCache<ByteArrayWrapper, ObjectKeyCacheEntry> () {
            @Override
            protected ObjectKeyCacheEntry lookup(ByteArrayWrapper key) {
                ObjectKey okey = ORBImpl.this.getObjectKeyFactory().create(
                    key.getObjKey());
                ObjectKeyCacheEntry entry = new ObjectKeyCacheEntryImpl( okey ) ;
                return entry ;
            }
        } ;

    public InvocationInterceptor getInvocationInterceptor() {
        return invocationInterceptor ;
    }

    public void setInvocationInterceptor( InvocationInterceptor interceptor ) {
        this.invocationInterceptor = interceptor ;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public ORBData getORBData() 
    {
        return configData ;
    }
 
    public PIHandler getPIHandler()
    {
        return pihandler ;
    }

    public void createPIHandler() 
    {
        this.pihandler = new PIHandlerImpl( this, configData.getOrbInitArgs() ) ;
    }

    
    public ORBImpl()
    {
        
    }

    public ORBVersion getORBVersion()
    {
        return orbVersionThreadLocal.get() ;
    }

    public void setORBVersion(ORBVersion verObj)
    {
        orbVersionThreadLocal.set(verObj);
    }


    @OrbLifeCycle
    private void initManagedObjectManager() {
        createORBManagedObjectManager() ;
        mom.registerAtRoot( configData ) ;
    }



    
    
    private void preInit( String[] params, Properties props )
    {
        
        
        
        
        
        
        
        
        
        
        transientServerId = (int)System.currentTimeMillis();

        orbVersionThreadLocal  = new ThreadLocal<ORBVersion>() {
            @Override
            protected ORBVersion initialValue() {
                
                return ORBVersionFactory.getORBVersion() ;
            }
        };

        requestDispatcherRegistry = new RequestDispatcherRegistryImpl( 
            ORBConstants.DEFAULT_SCID);
        copierManager = new CopierManagerImpl() ;

        taggedComponentFactoryFinder = 
            new TaggedComponentFactoryFinderImpl(this) ;
        taggedProfileFactoryFinder = 
            new TaggedProfileFactoryFinderImpl(this) ;
        taggedProfileTemplateFactoryFinder = 
            new TaggedProfileTemplateFactoryFinderImpl(this) ;

        OAInvocationInfoStack = 
            new ThreadLocal<StackImpl<OAInvocationInfo>> () {
                @Override
                protected StackImpl<OAInvocationInfo> initialValue() {
                    return new StackImpl<OAInvocationInfo>();
                } 
            };

        clientInvocationInfoStack = 
            new ThreadLocal<StackImpl<ClientInvocationInfo>>() {
                @Override
                protected StackImpl<ClientInvocationInfo> initialValue() {
                    return new StackImpl<ClientInvocationInfo>();
                }
            };

        serviceContextFactoryRegistry = 
            ServiceContextDefaults.makeServiceContextFactoryRegistry( this ) ;
    }

    @InfoMethod
    private void configDataParsingComplete(String oRBId) { }

    @InfoMethod
    private void transportInitializationComplete(String oRBId) { }

    @InfoMethod
    private void userConfiguratorExecutionComplete(String oRBId) { }

    @InfoMethod
    private void interceptorInitializationComplete(String oRBId) { }

    @InfoMethod
    private void mbeansRegistereed(String oRBId) { }

    @InfoMethod
    private void initializationComplete(String oRBId) { }

    @InfoMethod
    private void startingShutdown(String oRBId) { }

    @InfoMethod
    private void startingDestruction(String oRBId) { }

    @InfoMethod
    private void isLocalServerIdInfo(int subcontractId, int serverId,
        int transientServerId, boolean aTransient,
        boolean persistentServerIdInitialized, int psid) { }

    
    
    private class ConfigParser extends ParserImplBase {
        
        
        public Class<?> configurator ;

        public ConfigParser( boolean disableORBD ) {
            
            configurator = ORBConfiguratorImpl.class ;            

            if (!disableORBD) {
                
                
                String cname = 
                    "com.sun.corba.ee.impl.activation.ORBConfiguratorPersistentImpl" ;

                try {
                    configurator = Class.forName(cname);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ORBImpl.class.getName()).log(Level.FINE, null, ex);
                }
            }
        }

        public PropertyParser makeParser()
        {
            PropertyParser parser = new PropertyParser() ;
            parser.add( ORBConstants.SUN_PREFIX + "ORBConfigurator",
                OperationFactory.classAction( classNameResolver() ),
                    "configurator" ) ;
            return parser ;
        }
    }

    
    
    private static final Map<String,Integer> idcount =
        new HashMap<String,Integer>() ;
    private String rootName = null ;

    @Override
    public synchronized String getUniqueOrbId() {
        if (rootName == null) {
            String orbid = getORBData().getORBId() ;
            if (orbid.length() == 0) {
                orbid = "orb";
            }

            int num = 1 ;
            
            
            
            synchronized (idcount) {
                if (idcount.containsKey( orbid )) {
                    num = idcount.get( orbid ) + 1 ;
                }

                idcount.put( orbid, num ) ;
            }

            if (num != 1) {
                rootName = orbid + "_" + num ;
            } else {
                rootName = orbid ;
            }
        }

        return rootName ;
    }

    @OrbLifeCycle
    private void postInit( String[] params, DataCollector dataCollector ) {
        
        
        
        configData = new ORBDataParserImpl( this, dataCollector) ;
        if (orbInitDebug) {
            System.out.println( "Contents of ORB configData:" ) ;
            System.out.println( ObjectUtility.defaultObjectToString( configData ) ) ;
        }
        configData.setOrbInitArgs( params ) ;

        
        
        setDebugFlags( configData.getORBDebugFlags() ) ;
        configDataParsingComplete( getORBData().getORBId() ) ;

        initManagedObjectManager() ;

        
        
        
        
        
        

        
        initializePrimitiveTypeCodeConstants() ;

        
        
        transportManager = new TransportManagerImpl(this);
        getLegacyServerSocketManager();

        transportInitializationComplete( getORBData().getORBId() ) ;

        super.getByteBufferPool();
        serviceContextsCache = new ServiceContextsCache(this);

        
        ConfigParser parser = new ConfigParser( configData.disableORBD() ) ;
        parser.init( dataCollector ) ;

        ORBConfigurator configurator =  null ;
        String name = "NO NAME AVAILABLE" ;
        if (parser.configurator == null) {
            throw wrapper.badOrbConfigurator( name ) ;
        } else {
            try {
                configurator = 
                    (ORBConfigurator)(parser.configurator.newInstance()) ;
            } catch (Exception iexc) {
                name = parser.configurator.getName() ;
                throw wrapper.badOrbConfigurator( iexc, name ) ;
            }
        }

        
        
        
        try {
            configurator.configure( dataCollector, this ) ;
        } catch (Exception exc) {
            throw wrapper.orbConfiguratorError( exc ) ;
        }

        userConfiguratorExecutionComplete( getORBData().getORBId()  ) ;

        
        
        
        
        getThreadPoolManager();

        
        
        
        
        
        pihandler.initialize() ;

        interceptorInitializationComplete( getORBData().getORBId() ) ;

        
        if (configData.registerMBeans()) {
            mom.resumeJMXRegistration() ;
            mbeansRegistereed( getORBData().getORBId() ) ;
        }
    }

    private POAFactory getPOAFactory() {
        return poaFactory.get() ;
    }

    private TOAFactory getTOAFactory() {
        return toaFactory.get() ;
    }

    public void check_set_parameters() {
        if (set_parameters_called) {
            throw wrapper.setParameterCalledAgain() ;
        } else {
            set_parameters_called = true ;
        }
    }

    @OrbLifeCycle
    public void set_parameters( Properties props )
    {
        preInit( null, props ) ;
        DataCollector dataCollector = 
            DataCollectorFactory.create( props, getLocalHostName() ) ;
        postInit( null, dataCollector ) ;
        initializationComplete( getORBData().getORBId() ) ;
    }

    @OrbLifeCycle
    protected void set_parameters(Applet app, Properties props)
    {
        preInit( null, props ) ;
        DataCollector dataCollector = 
            DataCollectorFactory.create( app, props, getLocalHostName() ) ;
        postInit( null, dataCollector ) ;
        initializationComplete( getORBData().getORBId() ) ;
    }

    public void setParameters( String[] params, Properties props ) {
        set_parameters( params, props ) ;
    }

  
    protected void set_parameters (String[] params, Properties props)
    {
        preInit( params, props ) ;
        DataCollector dataCollector = 
            DataCollectorFactory.create( params, props, getLocalHostName() ) ;
        postInit( params, dataCollector ) ;
    }



    public synchronized org.omg.CORBA.portable.OutputStream create_output_stream()
    {
        return OutputStreamFactory.newEncapsOutputStream(this);
    }

    
    @Override
    public synchronized org.omg.CORBA.Current get_current()
    {
        checkShutdownState();

        

        throw wrapper.genericNoImpl() ;
    }

    
    public synchronized NVList create_list(int count)
    {
        checkShutdownState();
        return new NVListImpl(this, count);
    }

    
    @Override
    public synchronized NVList create_operation_list(org.omg.CORBA.Object oper)
    {
        checkShutdownState();
        throw wrapper.genericNoImpl() ;
    }

    
    public synchronized NamedValue create_named_value(String s, Any any, int flags)
    {
        checkShutdownState();
        return new NamedValueImpl(this, s, any, flags);
    }

    
    public synchronized org.omg.CORBA.ExceptionList create_exception_list()
    {
        checkShutdownState();
        return new ExceptionListImpl();
    }

    
    public synchronized org.omg.CORBA.ContextList create_context_list()
    {
        checkShutdownState();
        return new ContextListImpl(this);
    }

    
    public synchronized org.omg.CORBA.Context get_default_context()
    {
        checkShutdownState();
        throw wrapper.genericNoImpl() ;
    }

    
    public synchronized org.omg.CORBA.Environment create_environment()
    {
        checkShutdownState();
        return new EnvironmentImpl();
    }

    public synchronized void send_multiple_requests_oneway(Request[] req)
    {
        checkShutdownState();

        
        for (int i = 0; i < req.length; i++) {
            req[i].send_oneway();
        }
    }

    
    public synchronized void send_multiple_requests_deferred(Request[] req)
    {
        checkShutdownState();
        dynamicRequests.addAll(Arrays.asList(req));

        
        for (Request r : req) {
            AsynchInvoke invokeObject = new AsynchInvoke( this, 
                (com.sun.corba.ee.impl.corba.RequestImpl)r, true);
            new Thread(invokeObject).start();
        }
    }

    
    public synchronized boolean poll_next_response()
    {
        checkShutdownState();

        
        synchronized(dynamicRequests) {
            for (Request r : dynamicRequests) {
                if (r.poll_response()) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public org.omg.CORBA.Request get_next_response()
        throws org.omg.CORBA.WrongTransaction
    {
        synchronized( this ) {
            checkShutdownState();
        }

        while (true) {
            
            synchronized ( dynamicRequests ) {
                Iterator<Request> iter = dynamicRequests.iterator() ;
                while (iter.hasNext()) {
                    Request curr = iter.next() ;
                    if (curr.poll_response()) {
                        curr.get_response() ;
                        iter.remove() ;
                        return curr ;
                    }
                }
            }

            
            synchronized(this.svResponseReceived) {
                while (!this.svResponseReceived.value()) {
                    try {
                        this.svResponseReceived.wait();
                    } catch(java.lang.InterruptedException ex) {
                        
                    }
                }
                
                this.svResponseReceived.reset();
            }
        }
    }

    
    public void notifyORB() 
    {
        synchronized (this.svResponseReceived) {
            this.svResponseReceived.set();
            this.svResponseReceived.notify();
        }
    }

    
    public synchronized String object_to_string(org.omg.CORBA.Object obj)
    {
        checkShutdownState();

        
        if (obj == null) {
            IOR nullIOR = IORFactories.makeIOR( this ) ;
            return nullIOR.stringify();
        }

        IOR ior = null ;

        try {
            ior = getIOR( obj, true ) ;
        } catch (BAD_PARAM bp) {
            
            if (bp.minor == ORBUtilSystemException.LOCAL_OBJECT_NOT_ALLOWED) {
                throw omgWrapper.notAnObjectImpl( bp ) ;
            } else {
                throw bp;
            }
        }

        return ior.stringify() ;
    }

    
    public org.omg.CORBA.Object string_to_object(String str)
    {
        Operation op ;

        synchronized (this) {
            checkShutdownState();
            op = urlOperation ;
        }

        if (str == null) {
            throw wrapper.nullParam();
        }

        synchronized (urlOperationLock) {
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object)op.operate( str ) ;
            return obj ;
        }
    }

    
    
    public synchronized IOR getFVDCodeBaseIOR()
    {
        if (codeBaseIOR == null) {
            ValueHandler vh = ORBUtility.createValueHandler();
            codeBase = (CodeBase)vh.getRunTimeCodeBase();
            codeBaseIOR = getIOR( codeBase, true ) ;
        }

        return codeBaseIOR;
    }

    
    public TypeCode get_primitive_tc(TCKind tcKind) {
        return get_primitive_tc( tcKind.value() ) ; 
    }

    
    public synchronized TypeCode create_struct_tc(String id,
                                     String name,
                                     StructMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_struct, id, name, members);
    }

    
    public synchronized TypeCode create_union_tc(String id,
                                    String name,
                                    TypeCode discriminator_type,
                                    UnionMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this,
                                TCKind._tk_union,
                                id,
                                name,
                                discriminator_type,
                                members);
    }

    
    public synchronized TypeCode create_enum_tc(String id,
                                   String name,
                                   String[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_enum, id, name, members);
    }

    
    public synchronized TypeCode create_alias_tc(String id,
                                    String name,
                                    TypeCode original_type)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_alias, id, name, original_type);
    }

    
    public synchronized TypeCode create_exception_tc(String id,
                                        String name,
                                        StructMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_except, id, name, members);
    }

    
    public synchronized TypeCode create_interface_tc(String id,
                                        String name)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_objref, id, name);
    }

    
    public synchronized TypeCode create_string_tc(int bound)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_string, bound);
    }

    
    public synchronized TypeCode create_wstring_tc(int bound) {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_wstring, bound);
    }

    public synchronized TypeCode create_sequence_tc(int bound, 
        TypeCode element_type) {

        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_sequence, bound, element_type);
    }


    @SuppressWarnings("deprecation")
    public synchronized TypeCode create_recursive_sequence_tc(int bound,
                                                 int offset) {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_sequence, bound, offset);
    }


    public synchronized TypeCode create_array_tc(int length,
                                    TypeCode element_type)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_array, length, element_type);
    }


    @Override
    public synchronized org.omg.CORBA.TypeCode create_native_tc(String id,
                                                   String name)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_native, id, name);
    }

    @Override
    public synchronized org.omg.CORBA.TypeCode create_abstract_interface_tc(
                                                               String id,
                                                               String name)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_abstract_interface, id, name);
    }

    @Override
    public synchronized org.omg.CORBA.TypeCode create_fixed_tc(short digits, short scale)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_fixed, digits, scale);
    }

    @Override
    public synchronized org.omg.CORBA.TypeCode create_value_tc(String id,
                                                  String name,
                                                  short type_modifier,
                                                  TypeCode concrete_base,
                                                  ValueMember[] members)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_value, id, name,
                                type_modifier, concrete_base, members);
    }

    @Override
    public synchronized org.omg.CORBA.TypeCode create_recursive_tc(String id) {
        checkShutdownState();
        return new TypeCodeImpl(this, id);
    }

    @Override
    public synchronized org.omg.CORBA.TypeCode create_value_box_tc(String id,
                                                      String name,
                                                      TypeCode boxed_type)
    {
        checkShutdownState();
        return new TypeCodeImpl(this, TCKind._tk_value_box, id, name, 
            boxed_type);
    }

    public synchronized Any create_any()
    {
        checkShutdownState();
        return new AnyImpl(this);
    }

    
    

    
    

    public synchronized void setTypeCodeForClass(Class c, TypeCodeImpl tci) 
    {
        if (typeCodeForClassMap == null) {
            typeCodeForClassMap = new WeakHashMap<Class<?>, TypeCodeImpl>(64);
        }

        
        if ( ! typeCodeForClassMap.containsKey(c)) {
            typeCodeForClassMap.put(c, tci);
        }
    }

    public synchronized TypeCodeImpl getTypeCodeForClass(Class c) 
    {
        if (typeCodeForClassMap == null) {
            return null;
        }
        return typeCodeForClassMap.get(c);
    }



    public String[] list_initial_services() {
        Resolver res ;

        synchronized( this ) {
            checkShutdownState();
            res = resolver ;
        }

        synchronized (resolverLock) {
            java.util.Set<String> keys = res.list() ;
            return keys.toArray( new String[keys.size()] ) ;
        }
    }

    public org.omg.CORBA.Object resolve_initial_references(
        String identifier) throws InvalidName {
        Resolver res ;

        synchronized( this ) {
            checkShutdownState();
            res = resolver ;
        }

        org.omg.CORBA.Object result = res.resolve( identifier ) ;
        
        if (result == null) {
            throw new InvalidName(identifier + " not found");
        } else {
            return result;
        }
    }

    @Override
    public void register_initial_reference(
        String id, org.omg.CORBA.Object obj ) throws InvalidName {
        ServerRequestDispatcher insnd ;

        if ((id == null) || (id.length() == 0)) {
            throw new InvalidName("Null or empty id string");
        }

        synchronized (this) {
            checkShutdownState();
        }

        synchronized (resolverLock) {
            insnd = insNamingDelegate ;

            java.lang.Object obj2 = localResolver.resolve( id ) ;
            if (obj2 != null) {
                throw new InvalidName(id + " already registered");
            }

            localResolver.register( id, 
                NullaryFunction.Factory.makeConstant( obj )) ;
        }
      
        synchronized (this) {
            if (StubAdapter.isStub(obj)) {
                requestDispatcherRegistry.registerServerRequestDispatcher(insnd, id);
            }
        }
    }



    @Override
    public void run() 
    {
        synchronized (this) {
            checkShutdownState();
        }

        synchronized (runObj) {
            try {
                runObj.wait();
            } catch ( InterruptedException ex ) {}
        }
    }

    @Override
    @OrbLifeCycle
    public void shutdown(boolean wait_for_completion) {
        boolean wait = false ;

        synchronized (this) {
            checkShutdownState();
            
            
            
            
            
            if (wait_for_completion &&
                isProcessingInvocation.get() == Boolean.TRUE) {
                throw omgWrapper.shutdownWaitForCompletionDeadlock() ;
            }

            if (status == STATUS_SHUTTING_DOWN) {
                if (wait_for_completion) {
                    wait = true ;
                } else {
                    return ;
                }
            }

            status = STATUS_SHUTTING_DOWN ;
        } 

        
        synchronized (shutdownObj) {
            
            
            
            if (wait) {
                while (true) {
                    synchronized (this) {
                        if (status == STATUS_SHUTDOWN) {
                            break;
                        }
                    }

                    try {
                        shutdownObj.wait() ;
                    } catch (InterruptedException exc) {
                        
                    }
                }
            } else {
                startingShutdown( getORBData().getORBId() ) ;
                
                
                shutdownServants(wait_for_completion);

                if (wait_for_completion) {
                    synchronized ( waitForCompletionObj ) {
                        while (numInvocations.get() > 0) {
                            try {
                                numWaiters.incrementAndGet() ;
                                waitForCompletionObj.wait();
                            } catch (InterruptedException ex) {
                                
                            } finally {
                                numWaiters.decrementAndGet() ;
                            }
                        }
                    }
                }

                synchronized ( runObj ) {
                    runObj.notifyAll();
                }

                status = STATUS_SHUTDOWN;

                shutdownObj.notifyAll() ;
            }
        }
    }

    
    
    
    @OrbLifeCycle
    protected void shutdownServants(boolean wait_for_completion) {
        Set<ObjectAdapterFactory> oaset ;
        synchronized(this) {
            oaset = new HashSet<ObjectAdapterFactory>( 
                requestDispatcherRegistry.getObjectAdapterFactories() ) ;
        }

        for (ObjectAdapterFactory oaf : oaset) {
            oaf.shutdown(wait_for_completion);
        }
    }

    
    private void checkShutdownState()
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.orbDestroyed() ;
        }

        if (status == STATUS_SHUTDOWN) {
            throw omgWrapper.badOperationAfterShutdown() ;
        }
    }

    public boolean isDuringDispatch() {
        return isProcessingInvocation.get() ;
    }

    public void startingDispatch() {
        isProcessingInvocation.set(true);
        numInvocations.incrementAndGet() ;
    }

    public void finishedDispatch() {
        isProcessingInvocation.set(false);
        int ni = numInvocations.decrementAndGet() ;
        if (ni < 0) {
            throw wrapper.numInvocationsAlreadyZero() ;
        }

        if (numWaiters.get() > 0 && ni == 0) {
            synchronized (waitForCompletionObj) {
                waitForCompletionObj.notifyAll();
            }
        }
    }

    
    @Override
    @OrbLifeCycle
    public void destroy() {
        boolean shutdownFirst = false ;
        synchronized (this) {
            shutdownFirst = (status == STATUS_OPERATING) ;
        }

        if (shutdownFirst) {
            shutdown(true);
        }

        synchronized (this) {
            if (status < STATUS_DESTROYED) {
                getCorbaTransportManager().close();
                getPIHandler().destroyInterceptors() ;
                
                
                status = STATUS_DESTROYED;
            } else {
                
                return ;
            }
        }

        startingDestruction( getORBData().getORBId() ) ;

        ThreadPoolManager tpToClose = null ;
        synchronized (threadPoolManagerAccessLock) {
            if (orbOwnsThreadPoolManager) {
                tpToClose = threadpoolMgr ;
                threadpoolMgr = null ;
            }
        }

        if (tpToClose != null) {
            try {
                tpToClose.close() ;
            } catch (IOException exc) {
                wrapper.ioExceptionOnClose( exc ) ;
            }
        }

        CachedCodeBase.cleanCache( this ) ;
        try {
            pihandler.close() ;
        } catch (IOException exc) {
            wrapper.ioExceptionOnClose( exc ) ;
        }

        super.destroy() ;

        synchronized (this) {
            corbaContactInfoListFactoryAccessLock = null ; 
            corbaContactInfoListFactoryReadLock = null ;
            corbaContactInfoListFactoryWriteLock = null ;

            transportManager = null ;
            legacyServerSocketManager = null ;
            OAInvocationInfoStack  = null ; 
            clientInvocationInfoStack  = null ; 
            codeBase = null ; 
            codeBaseIOR = null ;
            dynamicRequests.clear() ;
            isProcessingInvocation = null ;
            typeCodeForClassMap  = null ;
            valueFactoryCache = null ;
            orbVersionThreadLocal = null ; 
            requestDispatcherRegistry = null ;
            copierManager = null ;
            serviceContextFactoryRegistry = null ;
            serviceContextsCache= null ;
            toaFactory = null ;
            poaFactory = null ;
            pihandler = null ;
            configData = null ;
            badServerIdHandler = null ;
            clientDelegateFactory = null ;
            corbaContactInfoListFactory = null ;
            resolver = null ;
            localResolver = null ;
            insNamingDelegate = null ;
            urlOperation = null ;
            taggedComponentFactoryFinder = null ;
            taggedProfileFactoryFinder = null ;
            taggedProfileTemplateFactoryFinder = null ;
            objectKeyFactory = null ;
            invocationInterceptor = null ;
            objectKeyCache.clear() ;
        }

        try {
            mom.close() ;
        } catch (Exception exc) {
            
        }
    }

    
    @Override
    public synchronized ValueFactory register_value_factory(String repositoryID, 
        ValueFactory factory) 
    {
        checkShutdownState();

        if ((repositoryID == null) || (factory == null)) {
            throw omgWrapper.unableRegisterValueFactory();
        }

        return valueFactoryCache.put(repositoryID, factory);
    }

    
    @Override
    public synchronized void unregister_value_factory(String repositoryID) 
    {
        checkShutdownState();

        if (valueFactoryCache.remove(repositoryID) == null) {
            throw wrapper.nullParam();
        }
    }

    
    @Override
    public synchronized ValueFactory lookup_value_factory(String repositoryID) 
    {
        checkShutdownState();

        ValueFactory factory = valueFactoryCache.get(repositoryID);

        if (factory == null) {
            try {
                factory = Utility.getFactory(null, null, null, repositoryID);
            } catch(org.omg.CORBA.MARSHAL ex) {
                throw wrapper.unableFindValueFactory( ex ) ;
            }
        }

        return factory ;
    }

    public OAInvocationInfo peekInvocationInfo() {
        return OAInvocationInfoStack.get().peek() ;
    }

    public void pushInvocationInfo( OAInvocationInfo info ) {
        OAInvocationInfoStack.get().push( info ) ;
    }

    public OAInvocationInfo popInvocationInfo() {
        return OAInvocationInfoStack.get().pop() ;
    }

    

    private final Object badServerIdHandlerAccessLock = new Object();

    public void initBadServerIdHandler() 
    {
        synchronized (badServerIdHandlerAccessLock) {
            Class<?> cls = configData.getBadServerIdHandler() ;
            if (cls != null) {
                try {
                    Class<?>[] params = new Class<?>[] { org.omg.CORBA.ORB.class };
                    java.lang.Object[] args = new java.lang.Object[]{this};
                    Constructor<?> cons = cls.getConstructor(params);
                    badServerIdHandler = 
                        (BadServerIdHandler) cons.newInstance(args);
                } catch (Exception e) {
                    throw wrapper.errorInitBadserveridhandler( e ) ;
                }
            }
        }
    }

    public void setBadServerIdHandler( BadServerIdHandler handler ) 
    {
        synchronized (badServerIdHandlerAccessLock) {
            badServerIdHandler = handler;
        }
    }

    public void handleBadServerId( ObjectKey okey ) 
    {
        synchronized (badServerIdHandlerAccessLock) {
            if (badServerIdHandler == null) {
                throw wrapper.badServerId();
            } else {
                badServerIdHandler.handle(okey);
            }
        }
    }

    @Override
    public synchronized org.omg.CORBA.Policy create_policy( int type, 
        org.omg.CORBA.Any val ) throws org.omg.CORBA.PolicyError
    {
        checkShutdownState() ;

        return pihandler.create_policy( type, val ) ;
    }

    @Override
    public synchronized void connect(org.omg.CORBA.Object servant)
    {
        checkShutdownState();
        if (getTOAFactory() == null) {
            throw wrapper.noToa();
        }

        try {
            String codebase = Util.getInstance().getCodebase( servant.getClass() ) ;
            getTOAFactory().getTOA( codebase ).connect( servant ) ;
        } catch ( Exception ex ) {
            throw wrapper.orbConnectError( ex ) ;
        }
    }

    @Override
    public synchronized void disconnect(org.omg.CORBA.Object obj)
    {
        checkShutdownState();
        if (getTOAFactory() == null) {
            throw wrapper.noToa();
        }

        try {
            getTOAFactory().getTOA().disconnect( obj ) ;
        } catch ( Exception ex ) {
            throw wrapper.orbConnectError( ex ) ;
        }
    }

    public int getTransientServerId()
    {
        if( configData.getPersistentServerIdInitialized( ) ) {
            
            return configData.getPersistentServerId( );
        }
        return transientServerId;
    }

    public RequestDispatcherRegistry getRequestDispatcherRegistry()
    {
        return requestDispatcherRegistry;
    }

    public ServiceContextFactoryRegistry getServiceContextFactoryRegistry()
    {
        return serviceContextFactoryRegistry ;
    } 

    public ServiceContextsCache getServiceContextsCache() 
    {
        return serviceContextsCache;
    }

    
    
    
    
    
    
    
    
    
    
    public boolean isLocalHost( String hostName ) 
    {
        return hostName.equals( configData.getORBServerHost() ) ||
            hostName.equals( getLocalHostName() ) ;
    }

    @Subcontract
    public boolean isLocalServerId( int subcontractId, int serverId )
    {
        if (subcontractDebugFlag) {
            int psid = -1;
            if (configData.getPersistentServerIdInitialized()) {
                psid = configData.getPersistentServerId();
            }

            isLocalServerIdInfo( subcontractId, serverId, 
                 getTransientServerId(), 
                 ORBConstants.isTransient(subcontractId),
                 configData.getPersistentServerIdInitialized(), psid ) ;
        }

        if ((subcontractId < ORBConstants.FIRST_POA_SCID) || 
            (subcontractId > ORBConstants.MAX_POA_SCID)) {
            return serverId == getTransientServerId();
        }
                
        
        if (ORBConstants.isTransient( subcontractId )) {
            return serverId == getTransientServerId();
        } else if (configData.getPersistentServerIdInitialized()) {
            return serverId == configData.getPersistentServerId();
        } else {
            return false;
        }
    }

    

    private String getHostName(String host) 
        throws java.net.UnknownHostException 
    {
        return InetAddress.getByName( host ).getHostAddress();
    }

    

    private static String localHostString = null;

    private synchronized String getLocalHostName() 
    {
        if (localHostString == null) {
            try {
                localHostString = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception ex) {
                throw wrapper.getLocalHostFailed( ex ) ;
            }
        }
        return localHostString ;
    }

 

    
    @Override
    public synchronized boolean work_pending()
    {
        checkShutdownState();
        throw wrapper.genericNoImpl() ;
    }
  
    
    @Override
    public synchronized void perform_work()
    {
        checkShutdownState();
        throw wrapper.genericNoImpl() ;
    }

    @Override
    public synchronized void set_delegate(java.lang.Object servant){
        checkShutdownState();

        POAFactory pf = getPOAFactory() ;
        if (pf != null) {
            ((org.omg.PortableServer.Servant) servant)._set_delegate(pf.getDelegateImpl());
        } else {
            throw wrapper.noPoa();
        }
    }

    @InfoMethod
    private void invocationInfoChange( String msg ) { }

    @Subcontract
    public ClientInvocationInfo createOrIncrementInvocationInfo() {
        ClientInvocationInfo clientInvocationInfo = null;
        StackImpl<ClientInvocationInfo> invocationInfoStack =
            clientInvocationInfoStack.get();
        if (!invocationInfoStack.empty()) {
            clientInvocationInfo = invocationInfoStack.peek();
        }
        if ((clientInvocationInfo == null) ||
            (!clientInvocationInfo.isRetryInvocation()))
        {
            
            clientInvocationInfo = new InvocationInfo();
            invocationInfoStack.push(clientInvocationInfo);
            invocationInfoChange( "new call" ) ;
        } else {
            invocationInfoChange( "retry" ) ;
        }
        
        clientInvocationInfo.setIsRetryInvocation(false);
        clientInvocationInfo.incrementEntryCount();
        return clientInvocationInfo;
    }
    
    @Subcontract
    public void releaseOrDecrementInvocationInfo() {
        int entryCount = -1;
        ClientInvocationInfo clientInvocationInfo = null;
        StackImpl<ClientInvocationInfo> invocationInfoStack =
            clientInvocationInfoStack.get();
        if (!invocationInfoStack.empty()) {
            clientInvocationInfo = invocationInfoStack.peek();
        } else {
            throw wrapper.invocationInfoStackEmpty() ;
        }

        clientInvocationInfo.decrementEntryCount();
        entryCount = clientInvocationInfo.getEntryCount();
        if (clientInvocationInfo.getEntryCount() == 0
            
            && !clientInvocationInfo.isRetryInvocation()) {

            invocationInfoStack.pop();
            invocationInfoChange( "pop" ) ;
        }
    }
    
    public ClientInvocationInfo getInvocationInfo() {
        return clientInvocationInfoStack.get().peek() ;
    }

    
    
    
    

    private final Object clientDelegateFactoryAccessorLock = new Object();

    public void setClientDelegateFactory( ClientDelegateFactory factory ) 
    {
        synchronized (clientDelegateFactoryAccessorLock) {
            clientDelegateFactory = factory ;
        }
    }

    public ClientDelegateFactory getClientDelegateFactory() 
    {
        synchronized (clientDelegateFactoryAccessorLock) {
            return clientDelegateFactory ;
        }
    }

    private ReentrantReadWriteLock 
          corbaContactInfoListFactoryAccessLock = new ReentrantReadWriteLock();
    private Lock corbaContactInfoListFactoryReadLock =
                               corbaContactInfoListFactoryAccessLock.readLock();
    private Lock corbaContactInfoListFactoryWriteLock = 
                              corbaContactInfoListFactoryAccessLock.writeLock();
    
    public void setCorbaContactInfoListFactory( ContactInfoListFactory factory )
    {
        corbaContactInfoListFactoryWriteLock.lock() ;
        try {
            corbaContactInfoListFactory = factory ;
        } finally {
            corbaContactInfoListFactoryWriteLock.unlock() ;
        }
    }

    public ContactInfoListFactory getCorbaContactInfoListFactory()
    {
        corbaContactInfoListFactoryReadLock.lock() ;
        try {
            return corbaContactInfoListFactory ;
        } finally {
            corbaContactInfoListFactoryReadLock.unlock() ;
        }
    }

    public void setResolver( Resolver resolver ) {
        synchronized (resolverLock) {
            this.resolver = resolver ;
        }
    }

    public Resolver getResolver() {
        synchronized (resolverLock) {
            return resolver ;
        }
    }

    public void setLocalResolver( LocalResolver resolver ) {
        synchronized (resolverLock) {
            this.localResolver = resolver ;
        }
    }

    public LocalResolver getLocalResolver() {
        synchronized (resolverLock) {
            return localResolver ;
        }
    }

    public void setURLOperation( Operation stringToObject ) {
        synchronized (urlOperationLock) {
            urlOperation = stringToObject ;
        }
    }

    public Operation getURLOperation() {
        synchronized (urlOperationLock) {
            return urlOperation ;
        }
    }

    public void setINSDelegate( ServerRequestDispatcher sdel ) {
        synchronized (resolverLock) {
            insNamingDelegate = sdel ;
        }
    }

    public TaggedComponentFactoryFinder getTaggedComponentFactoryFinder() {
        return taggedComponentFactoryFinder ;
    }

    public IdentifiableFactoryFinder<TaggedProfile>
        getTaggedProfileFactoryFinder() {
        return taggedProfileFactoryFinder ;
    }

    public IdentifiableFactoryFinder<TaggedProfileTemplate>
        getTaggedProfileTemplateFactoryFinder() {
        return taggedProfileTemplateFactoryFinder ;
    }

    private final Object objectKeyFactoryAccessLock = new Object();

    public ObjectKeyFactory getObjectKeyFactory() 
    {
        synchronized (objectKeyFactoryAccessLock) {
            return objectKeyFactory ;
        }
    }

    public void setObjectKeyFactory( ObjectKeyFactory factory ) 
    {
        synchronized (objectKeyFactoryAccessLock) {
            objectKeyFactory = factory ;
        }
    }

    public TransportManager getTransportManager()
    {
        return transportManager;
    }

    public TransportManager getCorbaTransportManager()
    {
        return getTransportManager();
    }

    private final Object legacyServerSocketManagerAccessLock = new Object();

    public LegacyServerSocketManager getLegacyServerSocketManager()
    {
        synchronized (legacyServerSocketManagerAccessLock) {
            if (legacyServerSocketManager == null) {
                legacyServerSocketManager = new LegacyServerSocketManagerImpl(this);
            }
            return legacyServerSocketManager;
        }
    }

    private final Object threadPoolManagerAccessLock = new Object();

    public void setThreadPoolManager(ThreadPoolManager mgr) {
        synchronized (threadPoolManagerAccessLock) {
            threadpoolMgr = mgr;
        }
    }

    public ThreadPoolManager getThreadPoolManager() {
        synchronized (threadPoolManagerAccessLock) {
            if (threadpoolMgr == null) {
                threadpoolMgr = new ThreadPoolManagerImpl();
                orbOwnsThreadPoolManager = true ;
            }
            return threadpoolMgr;
        }
    }

    public CopierManager getCopierManager() {
        return copierManager ;
    }

    @Override
    public IOR getIOR( org.omg.CORBA.Object obj, boolean connectIfNecessary ) {
        IOR result ;

        if (connectIfNecessary) {
            try {
                result = getIOR( obj ) ;
            } catch (BAD_OPERATION bop) {
                if (StubAdapter.isStub(obj)) {
                    try {
                        StubAdapter.connect( obj, this ) ;
                    } catch (java.rmi.RemoteException exc) {
                        throw wrapper.connectingServant( exc ) ;
                    }
                } else {
                    connect( obj ) ;
                }

                result = getIOR( obj ) ;
            }
        } else {
            
            result = getIOR( obj ) ;
        }
    
        return result ;
    }
    
    @Override
    public ObjectKeyCacheEntry extractObjectKeyCacheEntry(byte[] objKey) {
        if (objKey == null) {
            throw wrapper.invalidObjectKey();
        }

        ByteArrayWrapper newObjKeyWrapper = new ByteArrayWrapper(objKey);

        return objectKeyCache.get( newObjKeyWrapper ) ;
    }

    @Override
    public synchronized boolean orbIsShutdown() {
        return ((status == STATUS_DESTROYED) || 
            (status == STATUS_SHUTDOWN)) ;
    }
} 





class SynchVariable {
    
    public boolean _flag;

    
    SynchVariable() {
        _flag = false;
    }

    
    public void set() {
        _flag = true;
    }

        
    public boolean value() {
        return _flag;
    }

    
    public void reset() {
        _flag = false;
    }
}


