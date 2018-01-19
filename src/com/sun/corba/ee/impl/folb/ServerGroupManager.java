






package com.sun.corba.ee.impl.folb;








@Folb
public class ServerGroupManager
    extends
        org.omg.CORBA.LocalObject
    implements 
        GroupInfoServiceObserver,
        IORInterceptor,
        ORBConfigurator,
        ORBInitializer,
        ServerRequestInterceptor
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private static final String baseMsg = ServerGroupManager.class.getName();
    private static final long serialVersionUID = -3197578705750630503L;

    private transient ORB orb;
    private transient GroupInfoService gis;
    private transient CSIv2SSLTaggedComponentHandler
        csiv2SSLTaggedComponentHandler;
    private String membershipLabel;

    private enum MembershipChangeState { IDLE, DOING_WORK, RETRY_REQUIRED };
    private MembershipChangeState membershipChangeState =
        MembershipChangeState.IDLE;

    private ReferenceFactoryManager referenceFactoryManager;
    private Codec codec;
    private boolean initialized = false;

    
    
    private static final String SSL = com.sun.corba.ee.spi.transport.SocketInfo.SSL_PREFIX ;
    private static final String CLEAR = com.sun.corba.ee.spi.transport.SocketInfo.IIOP_CLEAR_TEXT ;

    @InfoMethod
    private void alreadyInitialized() { }

    @Folb
    private void initialize() {

        if (initialized) {
            alreadyInitialized();
            return;
        }

        try {
            initialized = true;

            updateMembershipLabel();

            CodecFactory codecFactory =
                CodecFactoryHelper.narrow(
                  orb.resolve_initial_references(
                      ORBConstants.CODEC_FACTORY_NAME));

            codec = codecFactory.create_codec(
                new Encoding((short)0, (byte)1, (byte)2));

            referenceFactoryManager = (ReferenceFactoryManager)
                orb.resolve_initial_references(
                    ORBConstants.REFERENCE_FACTORY_MANAGER);

            gis = (GroupInfoService) PortableRemoteObject.narrow(
                orb.resolve_initial_references(
                    ORBConstants.FOLB_SERVER_GROUP_INFO_SERVICE),
                GroupInfoService.class);

            gis.addObserver(this);

            try {
                csiv2SSLTaggedComponentHandler =
                    (CSIv2SSLTaggedComponentHandler)
                    orb.resolve_initial_references(
                        ORBConstants.CSI_V2_SSL_TAGGED_COMPONENT_HANDLER);
            } catch (InvalidName e) {
                csiv2SSLTaggedComponentHandler = null;
                wrapper.noCSIV2Handler( e ) ;
            }
        } catch (InvalidName e) {
            wrapper.serverGroupManagerException( e ) ;
        } catch (UnknownEncoding e) {
            wrapper.serverGroupManagerException( e ) ;
        }
    }

    
    
    
    

    public String name() {
        return baseMsg; 
    }

    public void destroy() {
    }

    
    
    
    

    @InfoMethod
    private void adapterName( String[] arr ) { }

    @InfoMethod
    private void addingAddresses() { }

    @InfoMethod
    private void notAddingAddress() { }

    @InfoMethod
    private void addingMembershipLabel( String ml ) { }

    @InfoMethod
    private void notAddingMembershipLabel( ) { }

    @InfoMethod
    private void skippingEndpoint( SocketInfo si ) {}

    @InfoMethod
    private void includingEndpoint( SocketInfo si ) {}

    @InfoMethod
    private void addingInstanceInfoFor( String name, int weight ) {}

    @Folb
    public void establish_components(IORInfo iorInfo) {
        try {
            initialize();

            
            String[] adapterName = 
                ((com.sun.corba.ee.impl.interceptors.IORInfoImpl)iorInfo)
                    .getObjectAdapter().getAdapterTemplate().adapter_name();

            adapterName( adapterName ) ;

            ReferenceFactory rf = referenceFactoryManager.find(adapterName);
            if (rf == null) {
                if (gis.shouldAddAddressesToNonReferenceFactory(adapterName)) {
                    addingAddresses() ;
                } else {
                    notAddingAddress();
                    return;
                }
            }

            

            
            List<ClusterInstanceInfo> info = 
                gis.getClusterInstanceInfo(adapterName);

            
            if (csiv2SSLTaggedComponentHandler != null) {
                TaggedComponent csiv2 = 
                    csiv2SSLTaggedComponentHandler.insert(iorInfo, info);
                if (csiv2 != null) {
                    iorInfo.add_ior_component(csiv2);
                }
            }

            
            for (ClusterInstanceInfo clusterInstanceInfo : info) {
                addingInstanceInfoFor( clusterInstanceInfo.name(),
                    clusterInstanceInfo.weight() ) ;

                List<SocketInfo> listOfSocketInfo = 
                    new LinkedList<SocketInfo>();

                for (SocketInfo sinfo : clusterInstanceInfo.endpoints()) {
                    if (sinfo.type().startsWith( SSL )) {
                        skippingEndpoint(sinfo);
                    } else {
                        includingEndpoint(sinfo);
                        
                        final SocketInfo si = new SocketInfo( CLEAR, sinfo.host(), sinfo.port() ) ;
                        listOfSocketInfo.add( si ) ;
                    }
                }

                final ClusterInstanceInfo ninfo = new ClusterInstanceInfo(
                    clusterInstanceInfo.name(),
                    clusterInstanceInfo.weight(),
                    listOfSocketInfo ) ;

                ClusterInstanceInfoComponent comp = 
                    IIOPFactories.makeClusterInstanceInfoComponent( 
                        ninfo ) ;

                iorInfo.add_ior_component( comp.getIOPComponent(orb) ) ;
            }

            
            if (gis.shouldAddMembershipLabel(adapterName)) {
                TaggedComponent tc = new TaggedComponent(
                    ORBConstants.FOLB_MEMBERSHIP_LABEL_TAGGED_COMPONENT_ID,
                    membershipLabel.getBytes());

                addingMembershipLabel( membershipLabel );
                iorInfo.add_ior_component(tc);
            } else {
                notAddingMembershipLabel();
            }
        } catch (RuntimeException e) {
            wrapper.serverGroupManagerException(e);
        }
    }

    public void components_established( IORInfo iorInfo ) {
    }

    public void adapter_manager_state_changed( int managerId, short state ) {
    }

    public void adapter_state_changed( ObjectReferenceTemplate[] templates,
        short state ) {
    }

    
    
    
    

    @InfoMethod
    private void alreadyChangingMembership() { }

    @InfoMethod
    private void loopingForMembershipChange() { }

    @InfoMethod
    private void unexpectedStateForMembershipChange() { }

    @Folb
    public void membershipChange() {
        try {
            synchronized (this) {
                if (membershipChangeState == MembershipChangeState.IDLE) {
                    membershipChangeState = MembershipChangeState.DOING_WORK;
                } else {
                    
                    membershipChangeState = MembershipChangeState.RETRY_REQUIRED;
                    alreadyChangingMembership();
                    return;
                }
            }

            boolean loop;

            do {
                loop = false;

                restartFactories();

                synchronized (this) {
                    if (membershipChangeState == MembershipChangeState.RETRY_REQUIRED) {
                        membershipChangeState = MembershipChangeState.DOING_WORK;
                        
                        
                        loop = true;
                        loopingForMembershipChange();
                    } else if (membershipChangeState == MembershipChangeState.DOING_WORK) {
                        membershipChangeState = MembershipChangeState.IDLE;
                    } else if (membershipChangeState == MembershipChangeState.IDLE) {
                        unexpectedStateForMembershipChange();
                    }
                }
            } while (loop);
            
        } catch (RuntimeException e) {
            wrapper.serverGroupManagerException(e);

            
            
            synchronized (this) {
                membershipChangeState = MembershipChangeState.IDLE;
            }
        }
    }


    @Folb
    public class WorkerThread extends Thread {
        @InfoMethod
        private void suspendRFM() { }

        @InfoMethod
        private void updateMembershipLabelInfo() { }

        @InfoMethod
        private void restartFactories() { }

        @InfoMethod
        private void resumeRFM() { }

        @Folb
        @Override
        public void run() {
            try {
                suspendRFM() ;
                referenceFactoryManager.suspend();

                
                
                
                
                updateMembershipLabelInfo();
                updateMembershipLabel();

                restartFactories();
                referenceFactoryManager.restartFactories();
            } finally {
                resumeRFM();
                referenceFactoryManager.resume();
            }
        }
    }

    @InfoMethod
    private void waitingForWorkerTermination() { }

    @Folb
    private void restartFactories() {
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        final ReferenceFactoryManager rfm = referenceFactoryManager;

        Thread worker = new WorkerThread() ;
        
        worker.start();
        
        
        waitingForWorkerTermination();
        boolean tryAgain;
        do {
            tryAgain = false;

            try { 
                worker.join(); 
            } catch (InterruptedException e) { 
                Thread.interrupted() ; 
                tryAgain = true; 
            }
        } while (tryAgain);
    }

    @InfoMethod
    private void newMembershipLabel( String ml ) { }

    @Folb
    private void updateMembershipLabel() {
        UID uid = new UID();
        String hostAddress = null;
        try {
            
            
            
            hostAddress = InetAddress.getLocalHost().getHostAddress();
            membershipLabel = hostAddress + ":::" + uid;
            newMembershipLabel( membershipLabel );
        } catch (UnknownHostException e) {
            wrapper.serverGroupManagerException(e);
        }
    }

    
    
    
    

    @Folb
    public void receive_request_service_contexts(ServerRequestInfo ri)
    {
        initialize();
    }

    @Folb
    public void receive_request(ServerRequestInfo ri)
    {
        initialize();
    }

    public void send_reply(ServerRequestInfo ri)
    {
        send_star(".send_reply", ri);
    }

    public void send_exception(ServerRequestInfo ri)
    {
        send_star(".send_exception", ri);
    }

    public void send_other(ServerRequestInfo ri)
    {
        send_star(".send_other", ri);
    }

    @InfoMethod
    private void rfmIsHolding() { }

    @InfoMethod
    private void notManagedByReferenceFactory( String[] adapterName ) { }

    @InfoMethod 
    private void membershipLabelsEqual() { }
    
    @InfoMethod 
    private void membershipLabelsNotEqual() { }
    
    @InfoMethod 
    private void membershipLabelsNotPresent() { }
   
    @InfoMethod
    private void sendingUpdatedIOR( String[] adapterName ) { }

    
    @Folb
    private void send_star(String point, ServerRequestInfo ri)
    {
        String[] adapterName = null;
        try {
            adapterName = ri.adapter_name();

            if (referenceFactoryManager.getState() ==
                ReferenceFactoryManager.RFMState.SUSPENDED) {

                rfmIsHolding();
                return;
            }

            ReferenceFactory referenceFactory = 
                referenceFactoryManager.find(adapterName);

            
            if (referenceFactory == null && 
                    !((ServerRequestInfoExt)ri).isNameService()) {
                notManagedByReferenceFactory( adapterName ) ;
                return;
            }

            
            String requestMembershipLabel = null;
            try {
                ServiceContext sc = ri.get_request_service_context(
                    ORBConstants.FOLB_MEMBERSHIP_LABEL_SERVICE_CONTEXT_ID);
                
                if (sc != null) {
                    byte[] data = sc.context_data;
                    requestMembershipLabel = new String(data);

                    if (membershipLabel.equals(requestMembershipLabel)) {
                        membershipLabelsEqual();
                        return;
                    }
                    membershipLabelsNotEqual();
                }
            } catch (BAD_PARAM e) {
                membershipLabelsNotPresent();
                
            }

            
            
            
            
            
            sendingUpdatedIOR( adapterName ) ;
            
            byte[] objectId = ri.object_id();
            org.omg.CORBA.Object ref = 
                referenceFactory.createReference(objectId);
            Any any = orb.create_any();
            
            
            ForwardRequest fr = new ForwardRequest(ref);
            ForwardRequestHelper.insert(any, fr);
            byte[] data = null;
            try {
                data = codec.encode_value(any);
            } catch (InvalidTypeForEncoding e) {
                wrapper.serverGroupManagerException(e);
            }
            ServiceContext sc = new ServiceContext(
                ORBConstants.FOLB_IOR_UPDATE_SERVICE_CONTEXT_ID, data);
            ri.add_reply_service_context(sc, false);
        } catch (RuntimeException e) {
            wrapper.serverGroupManagerException(e);
        }
    }

    
    
    
    

    public void pre_init(ORBInitInfo info) 
    {
    }

    @Folb
    public void post_init(ORBInitInfo info) {
        try {
            info.add_ior_interceptor(this);
            info.add_server_request_interceptor(this);
        } catch (Exception e) {
            wrapper.serverGroupManagerException(e);
        }
    }

    
    
    
    

    @Folb
    public void configure(DataCollector collector, ORB orb) 
    {
        this.orb = orb;

        
        orb.getORBData().addORBInitializer(this);
    }
}


