






package com.sun.corba.ee.impl.folb;



















@Folb
public class ClientGroupManager
    extends
        org.omg.CORBA.LocalObject
    implements 
        ClientRequestInterceptor,
        GroupInfoService,
        IIOPPrimaryToContactInfo,
        IORToSocketInfo,
        ORBConfigurator,
        ORBInitializer
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private static final long serialVersionUID = 7849660203226017842L;
    public final String baseMsg = ClientGroupManager.class.getName();

    public static boolean sentMemberShipLabel = false; 
    public static boolean receivedIORUpdate   = false; 

    private ORB orb;
    private Codec codec;

    private boolean initialized = false;

    private IOR lastIOR;  
    private final Object lastIORLock = new Object();
    private CSIv2SSLTaggedComponentHandler csiv2SSLTaggedComponentHandler;
    private transient GIS gis = new GIS();

    public ClientGroupManager() {       
    }

    @InfoMethod
    private void reportException( Exception exc ) { }

    @InfoMethod
    private void notFound( String name ) { }

    @Folb
    private void initialize()
    {
        if (initialized) {
            return;
        }

        try {
            initialized = true;

            try {
                csiv2SSLTaggedComponentHandler =
                    (CSIv2SSLTaggedComponentHandler)
                    orb.resolve_initial_references(
                        ORBConstants.CSI_V2_SSL_TAGGED_COMPONENT_HANDLER);
            } catch (InvalidName e) {
                csiv2SSLTaggedComponentHandler = null;
                notFound( ORBConstants.CSI_V2_SSL_TAGGED_COMPONENT_HANDLER );
            }
            CodecFactory codecFactory =
                CodecFactoryHelper.narrow(
                  orb.resolve_initial_references(
                      ORBConstants.CODEC_FACTORY_NAME));

            codec = codecFactory.create_codec(
                new Encoding((short)0, (byte)1, (byte)2));
        } catch (InvalidName e) {
            reportException( e ) ;
        } catch (UnknownEncoding e) {
            reportException( e ) ;
        }
    }

    
    
    
    

    @InfoMethod
    private void nonSSLSocketInfo() { }

    @InfoMethod
    private void returningPreviousSocketInfo( List lst ) { }

    @Folb
    public List getSocketInfo(IOR ior, List previous) 
    {
        initialize();

        try {
            if (csiv2SSLTaggedComponentHandler != null) {
                List<SocketInfo> csiv2 =
                    csiv2SSLTaggedComponentHandler.extract(ior);
                if (csiv2 != null) {
                    
                    return csiv2;
                }
            }

            nonSSLSocketInfo();

            if (! previous.isEmpty()) {
                returningPreviousSocketInfo(previous);
                return previous;
            }

            List result = new ArrayList();

            
            
            

            IIOPProfileTemplate iiopProfileTemplate = (IIOPProfileTemplate)
                ior.getProfile().getTaggedProfileTemplate();
            IIOPAddress primary = iiopProfileTemplate.getPrimaryAddress() ;
            String host = primary.getHost().toLowerCase();
            int port = primary.getPort();
            
            SocketInfo primarySocketInfo = 
                createSocketInfo("primary", 
                                 SocketInfo.IIOP_CLEAR_TEXT, host, port);
            result.add(primarySocketInfo);

            
            
            

            final Iterator<ClusterInstanceInfoComponent> iterator =
                iiopProfileTemplate.iteratorById(
                    ORBConstants.FOLB_MEMBER_ADDRESSES_TAGGED_COMPONENT_ID,
                    ClusterInstanceInfoComponent.class );

            while (iterator.hasNext()) {
                ClusterInstanceInfo clusterInstanceInfo = 
                    iterator.next().getClusterInstanceInfo() ;
                List<com.sun.corba.ee.spi.folb.SocketInfo> endpoints =
                  clusterInstanceInfo.endpoints();
                for (com.sun.corba.ee.spi.folb.SocketInfo socketInfo : endpoints) {
                    result.add( createSocketInfo(
                        "ClusterInstanceInfo.endpoint",
                        socketInfo.type(), socketInfo.host(),
                        socketInfo.port()));
                }
            }

            
            
            

            final Iterator<AlternateIIOPAddressComponent> aiterator = 
                iiopProfileTemplate.iteratorById(
                    org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS.value,
                    AlternateIIOPAddressComponent.class );

            while (aiterator.hasNext()) {
                AlternateIIOPAddressComponent alternate = 
                    aiterator.next();
                
                host = alternate.getAddress().getHost().toLowerCase();
                port = alternate.getAddress().getPort();
                
                result.add(createSocketInfo(
                    "AlternateIIOPAddressComponent",
                    SocketInfo.IIOP_CLEAR_TEXT, host, port));
            }

            return result;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            RuntimeException rte = new RuntimeException(e.getMessage());
            rte.initCause(e);
            throw rte;
        }
    }

    @Folb
    private SocketInfo createSocketInfo(final String msg,
                                        final String type,
                                        final String host,
                                        final int port) 
    {
        return new SocketInfo() {
                public String getType() {
                    return type;
                }

                public String getHost() {
                    return host;
                }

                public int getPort() {
                    return port;
                }

                @Override
                public boolean equals(Object o) {
                    if (o == null) {
                        return false;
                    }
                    if (! (o instanceof SocketInfo)) {
                        return false;
                    }
                    SocketInfo other = (SocketInfo)o;
                    if (other.getPort() != port) {
                        return false;
                    }
                    if (! other.getHost().equals(host)) {
                        return false;
                    }
                    if (! other.getType().equals(type)) {
                        return false;
                    }
                    return true;
                }

                @Override
                public String toString() {
                    return "SocketInfo[" + type + " " + host + " " + port +"]";
                }

                @Override
                public int hashCode() {
                    return port ^ host.hashCode() ^ type.hashCode() ;
                }
            };
    }

    
    
    
    

    private Map map = new HashMap();

    @Folb
    public synchronized void reset(ContactInfo primary)
    {
        initialize();
        try {
            map.remove(getKey(primary));
        } catch (Throwable t) {
            throw wrapper.exceptionInReset( t ) ;
        }
    }

    @InfoMethod
    private void hasNextInfo( int previousIndex, int contactInfoSize ) { }

    @Folb
    public synchronized boolean hasNext(ContactInfo primary,
                                        ContactInfo previous,
                                        List contactInfos)
    {
        initialize();
        try {
            boolean result;
            if (previous == null) {
                result = true;
            } else {
                int previousIndex = contactInfos.indexOf(previous);
                int contactInfosSize = contactInfos.size();
                hasNextInfo(previousIndex, contactInfosSize);
                if (previousIndex < 0) {
                    
                    
                    
                    RuntimeException rte = new RuntimeException(


                        "Problem in " + baseMsg + ".hasNext: previousIndex: "
                        + previousIndex);
                    
                    throw rte;
                } else {
                    
                    
                    result = (contactInfosSize - 1) > previousIndex;
                }
            }
            return result;
        } catch (Throwable t) {
            
            RuntimeException rte =
                new RuntimeException(baseMsg + ".hasNext error");
            rte.initCause(t);
            throw rte;
        }
    }

    @InfoMethod
    private void initializeMap() { }

    @InfoMethod
    private void primaryMappedTo( Object obj ) { }

    @InfoMethod
    private void cannotFindMappedEntry() { }

    @InfoMethod
    private void iiopFailoverTo( Object obj )  { }

    @InfoMethod
    private void mappedResult( Object obj ) { }

    @InfoMethod
    private void mappedResultWithUpdate( Object obj, int prevIndex, int size ) { }

    @Folb
    public synchronized ContactInfo next(ContactInfo primary,
                                         ContactInfo previous,
                                         List contactInfos)
    {
        initialize();
        try {
            Object result = null;

            if (previous == null) {
                
                result = map.get(getKey(primary));
                if (result == null) {
                    initializeMap();
                    
                    
                    result = contactInfos.get(0);
                    map.put(getKey(primary), result);
                } else {
                    primaryMappedTo(result);
                    int position = contactInfos.indexOf(result);
                    if (position == -1) {
                        
                        
                        
                        
                        cannotFindMappedEntry();
                        reset(primary);
                        return next(primary, previous, contactInfos);
                    }
                    
                    
                    
                    
                    
                    result = contactInfos.get(position);
                    mappedResult( result ) ;
                }
            } else {
                
                
                
                result = contactInfos.get(contactInfos.indexOf(previous) + 1);
                map.put(getKey(primary), result);

                iiopFailoverTo(result);

                if (orb.folbDebugFlag) {
                    
                    mappedResultWithUpdate(result, contactInfos.indexOf(previous),
                        contactInfos.size() );
                }
            }
            return (ContactInfo) result;
        } catch (Throwable t) {
            throw wrapper.exceptionInNext( t ) ;
        }
    }

    @Folb
    private Object getKey(ContactInfo contactInfo)
    {
        if (((SocketInfo)contactInfo).getPort() == 0) {
            
            
            
            return contactInfo.getContactInfoList()
                .getEffectiveTargetIOR();
        } else {
            return contactInfo;
        }
    }

    
    
    
    

    @Folb
    public List<ClusterInstanceInfo> getInitialClusterInstanceInfo(ORB orb,
        List<String> endpoints ) {
        try {
          org.omg.CORBA.Object ref ;
          if (endpoints.isEmpty()) {
              ref = orb.resolve_initial_references( "NameService");
          } else {
              final StringBuilder sb = new StringBuilder() ;
              sb.append( "corbaloc:" ) ;
              boolean first = true ;
              for (String str : endpoints ) {
                  if (first) {
                      first = false ;
                  } else {
                      sb.append( ',' ) ;
                  }

                  sb.append( "iiop:1.2@" ).append( str ) ;
              }

              sb.append( "/NameService" ) ;
              ref = orb.string_to_object( sb.toString() ) ;
          }

          NamingContext nctx = NamingContextHelper.narrow(ref);
          NameComponent[] path =
              { new NameComponent(ORBConstants.INITIAL_GROUP_INFO_SERVICE, "") };
          InitialGroupInfoService.InitialGIS initGIS =
              (InitialGroupInfoService.InitialGIS)PortableRemoteObject.narrow(
                  nctx.resolve(path), InitialGroupInfoService.InitialGIS.class);
          return initGIS.getClusterInstanceInfo();
        } catch (Exception e) {
            reportException(e);
            return null;
        }
    }

    private class GIS extends GroupInfoServiceBase
    {
        public List<ClusterInstanceInfo> internalClusterInstanceInfo( List<String> endpoints )
        {
            if (lastIOR == null) {           
                return getInitialClusterInstanceInfo(orb, endpoints );
            }

            IIOPProfileTemplate iiopProfileTemplate;
            synchronized (lastIORLock) {
                iiopProfileTemplate = (IIOPProfileTemplate)
                    lastIOR.getProfile().getTaggedProfileTemplate();
            }
            Iterator<ClusterInstanceInfoComponent> iterator =
                iiopProfileTemplate.iteratorById(
                    ORBConstants.FOLB_MEMBER_ADDRESSES_TAGGED_COMPONENT_ID,
                    ClusterInstanceInfoComponent.class );

            LinkedList<ClusterInstanceInfo> results = 
                new LinkedList<ClusterInstanceInfo>();

            while (iterator.hasNext()) {
                ClusterInstanceInfo clusterInstanceInfo = 
                    iterator.next().getClusterInstanceInfo() ;
                results.add(clusterInstanceInfo);
            }

            return results;
        }

        @Override
        public boolean shouldAddAddressesToNonReferenceFactory(String[] x)
        {
            throw new RuntimeException("Should not be called in this context");
        }

        @Override
        public boolean shouldAddMembershipLabel (String[] adapterName)
        {
            throw new RuntimeException("Should not be called in this context");
        }
    }

    public boolean addObserver(GroupInfoServiceObserver x)
    {
        return gis.addObserver(x);
    }
    public void notifyObservers()
    {
        gis.notifyObservers();
    }
    public List<ClusterInstanceInfo> getClusterInstanceInfo(
        String[] adapterName)
    {
        return gis.getClusterInstanceInfo(adapterName);
    }
    public List<ClusterInstanceInfo> getClusterInstanceInfo(
        String[] adapterName, List<String> endpoints )
    {
        return gis.getClusterInstanceInfo(adapterName,endpoints);
    }
    public boolean shouldAddAddressesToNonReferenceFactory(String[] x)
    {
        return gis.shouldAddAddressesToNonReferenceFactory(x);
    }
    public boolean shouldAddMembershipLabel (String[] adapterName)
    {
        return gis.shouldAddMembershipLabel(adapterName);
    }

    
    
    
    

    public String name() 
    {
        return baseMsg; 
    }

    public void destroy() 
    {
    }

    
    
    
    

    @InfoMethod
    private void sendRequestMembershipLabel( String label ) { }

    @InfoMethod
    private void sendRequestNoMembershipLabel( ) { }

    @Folb
    public void send_request(ClientRequestInfo ri)
    {
        try {
            operation( ri.operation() ) ;
            initialize(); 

            org.omg.CORBA.Object ref = ri.effective_target();
            IOR ior = orb.getIOR(ref,false);
            IIOPProfileTemplate iiopProfileTemplate = (IIOPProfileTemplate)
                ior.getProfile().getTaggedProfileTemplate();
            Iterator iterator = iiopProfileTemplate.iteratorById(
                ORBConstants.FOLB_MEMBERSHIP_LABEL_TAGGED_COMPONENT_ID);
            if (iterator.hasNext()) {
                org.omg.IOP.TaggedComponent membershipLabelTaggedComponent = 
                    ((com.sun.corba.ee.spi.ior.TaggedComponent)iterator.next())
                        .getIOPComponent(orb);
                byte[] data = membershipLabelTaggedComponent.component_data;
                sentMemberShipLabel = true; 
                sendRequestMembershipLabel( new String(data) );
                ServiceContext sc = new ServiceContext(
                    ORBConstants.FOLB_MEMBERSHIP_LABEL_SERVICE_CONTEXT_ID,
                    data);
                ri.add_request_service_context(sc, false);
            } else {
                sentMemberShipLabel = false; 
                sendRequestNoMembershipLabel() ;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public void send_poll(ClientRequestInfo ri)
    {
    }

    public void receive_reply(ClientRequestInfo ri)
    {
        receive_star(".receive_reply", ri);
    }

    public void receive_exception(ClientRequestInfo ri)
    {
        receive_star(".receive_exception", ri);
    }

    public void receive_other(ClientRequestInfo ri)
    {
        receive_star(".receive_other", ri);
    }

    @InfoMethod
    private void operation( String op ) { }

    @InfoMethod
    private void noIORUpdate() { }

    @InfoMethod
    private void receivedIORUpdateInfo() { }

    @Folb
    private void receive_star(String point, ClientRequestInfo ri)
    {
        operation( ri.operation() ) ;
        ServiceContext iorServiceContext = null;
        try {
            iorServiceContext =
                ri.get_reply_service_context(
                    ORBConstants.FOLB_IOR_UPDATE_SERVICE_CONTEXT_ID);
        } catch (BAD_PARAM e) {
            wrapper.noIORUpdateServicateContext( e ) ;
        }

        if (iorServiceContext == null) {
            noIORUpdate();
            receivedIORUpdate = false; 
            return;
        }

        receivedIORUpdateInfo() ;
        receivedIORUpdate = true ;

        IOR ior = extractIOR(iorServiceContext.context_data);
        synchronized (lastIORLock) {
            lastIOR = ior; 
            gis.notifyObservers();
        }
        reportLocatedIOR(ri, ior);

    }

    protected void reportLocatedIOR(ClientRequestInfo ri, IOR ior) {
        
        ((ClientRequestInfoImpl)ri).setLocatedIOR(ior);
    }

    protected IOR extractIOR(byte[] data) {
        Any any = null;
        try {
            any = codec.decode_value(data, ForwardRequestHelper.type());
        } catch (FormatMismatch e) {
            reportException( e ) ;
        } catch (TypeMismatch e) {
            reportException( e ) ;
        }

        
        
        ForwardRequest fr = ForwardRequestHelper.extract(any);
        org.omg.CORBA.Object ref = fr.forward;
        return orb.getIOR(ref,false);
    }

    
    
    
    

    public void pre_init(ORBInitInfo info) 
    {
    }

    @Folb
    public void post_init(ORBInitInfo info) {
        try {
            info.add_client_request_interceptor(this);
        } catch (Exception e) {
            reportException(e);
        }
    }

    
    
    
    

    @Folb
    public void configure(DataCollector collector, ORB orb) 
    {
        this.orb = orb;
        orb.getORBData().addORBInitializer(this);
        orb.getORBData().setIIOPPrimaryToContactInfo(this);
        orb.getORBData().setIORToSocketInfo(this);
        
        try {
            orb.register_initial_reference(
                ORBConstants.FOLB_CLIENT_GROUP_INFO_SERVICE,
                this);
        } catch (InvalidName e) {
            reportException(e);
        }
    }
}


