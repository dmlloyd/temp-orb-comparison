


package xxxx;











@ManagedObject
@Description( "ORB Configuration data" ) 
@AMXMetadata( isSingleton=true ) 
public interface ORBData {
    @ManagedAttribute
    @Description( "Value of ORBInitialHost, the host name of the remote name service" ) 
    public String getORBInitialHost() ;
    

    @ManagedAttribute
    @Description( "Value of ORBInitialPort, the port number of the remote name service" ) 
    public int getORBInitialPort() ;
    

    @ManagedAttribute
    @Description( "DESC" ) 
    public String getORBServerHost() ;
    

    @ManagedAttribute
    @Description( "DESC" ) 
    public int getORBServerPort() ;
    

    @ManagedAttribute
    @Description( "If true, the ORB listens at its ports on all IP interfaces on the host" ) 
    public boolean getListenOnAllInterfaces();
    

    @ManagedAttribute
    @Description( "The implementation of the legacy ORBSocketFactory interface in use (if any)" ) 
    public com.sun.corba.ee.spi.legacy.connection.ORBSocketFactory getLegacySocketFactory () ;

    @ManagedAttribute
    @Description( "The implementation of the ORBSocketFactory interface in use (if any)" ) 
    public com.sun.corba.ee.spi.transport.ORBSocketFactory getSocketFactory();

    @ManagedAttribute
    @Description( "Return the user-specified listen ports, on which the ORB listens for incoming requests" ) 
    public USLPort[] getUserSpecifiedListenPorts () ;
    

    @ManagedAttribute
    @Description( "Return the instance of the IORToSocketInfo interface, "
        + "which is used to get SocketInfo from IORs" ) 
    public IORToSocketInfo getIORToSocketInfo();

    
    public void            setIORToSocketInfo(IORToSocketInfo x);

    @ManagedAttribute
    @Description( "Return the instance of the IIOPPrimaryToContactInfo interface" ) 
    public IIOPPrimaryToContactInfo getIIOPPrimaryToContactInfo();

    
    public void                     setIIOPPrimaryToContactInfo(
                                                  IIOPPrimaryToContactInfo x);

    @ManagedAttribute
    @Description( "Return the configured ORB ID" ) 
    public String getORBId() ;

    @ManagedAttribute
    @Description( "Returns true if the RMI-IIOP local optimization "
        + "(caching servant in local subcontract) is allowed." ) 
    public boolean isLocalOptimizationAllowed() ;

    @ManagedAttribute
    @Description( "Return the GIOP version that will be prefered for sending requests" ) 
    public GIOPVersion getGIOPVersion() ;

    @ManagedAttribute
    @Description( "Return the high water mark for the connection cache" ) 
    public int getHighWaterMark() ;
    

    @ManagedAttribute
    @Description( "Return the number of connections to attempt to reclaim "
        + "when the total number of connections exceeds the high water mark" ) 
    public int getNumberToReclaim() ;
    

    @ManagedAttribute
    @Description( "Return the " ) 
    public int getGIOPFragmentSize() ;
    

    
    
    
    public int getGIOPBufferSize() ;
    

    
    
    
        
    public int getGIOPBuffMgrStrategy(GIOPVersion gv) ;

    
    @ManagedAttribute
    @Description( "The ORB required target addressing mode: "
        + "0:ObjectKey, 1:TaggedProfile, 2:EntireIOR, 3:Accept All (default)" ) 
    public short getGIOPTargetAddressPreference() ;

    @ManagedAttribute
    @Description( "The ORB required target addressing mode: "
        + "0:ObjectKey, 1:TaggedProfile, 2:EntireIOR, 3:Accept All (default)" ) 
    public short getGIOPAddressDisposition() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean useByteOrderMarkers() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean useByteOrderMarkersInEncapsulations() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean alwaysSendCodeSetServiceContext() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean getPersistentPortInitialized() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public int getPersistentServerPort();

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean getPersistentServerIdInitialized() ;

    
    @ManagedAttribute
    @Description( "DESC" ) 
    public int getPersistentServerId();

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean getServerIsORBActivated() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public Class getBadServerIdHandler();

    
    @ManagedAttribute
    @Description( "DESC" ) 
    public CodeSetComponentInfo getCodeSetComponentInfo() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public ORBInitializer[] getORBInitializers();

    
    
    
    public void addORBInitializer( ORBInitializer init ) ;

    @ManagedAttribute
    @Description( "Pair of (name, CORBA URL) used to initialize resolve_initial_references" ) 
    public Pair<String,String>[] getORBInitialReferences();

    public String getORBDefaultInitialReference() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public String[] getORBDebugFlags();
    

    @ManagedAttribute
    @Description( "DESC" ) 
    public Acceptor[] getAcceptors();

    @ManagedAttribute
    @Description( "DESC" ) 
    public ContactInfoListFactory getCorbaContactInfoListFactory();

    @ManagedAttribute
    @Description( "DESC" ) 
    public String acceptorSocketType();
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean acceptorSocketUseSelectThreadToWait();
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean acceptorSocketUseWorkerThreadForEvent();
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public String connectionSocketType();
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean connectionSocketUseSelectThreadToWait();

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean connectionSocketUseWorkerThreadForEvent();

    @ManagedAttribute
    @Description( "DESC" ) 
    public long getCommunicationsRetryTimeout();
    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public long getWaitForResponseTimeout();
    

    @ManagedAttribute
    @Description( "DESC" ) 
    public TcpTimeouts getTransportTcpTimeouts();
    

    @ManagedAttribute
    @Description( "DESC" ) 
    public TcpTimeouts getTransportTcpConnectTimeouts();
    

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean disableDirectByteBufferUse() ;

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean isJavaSerializationEnabled();

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean useRepId();

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean showInfoMessages();

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean getServiceContextReturnsNull() ;

    
    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean isAppServerMode() ;
    
    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public int getReadByteBufferSize();
    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public int getMaxReadByteBufferSizeThreshold();

    
    @ManagedAttribute
    @Description( "DESC" ) 
    public int getPooledDirectByteBufferSlabSize();
    
    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean alwaysEnterBlockingRead();
    
    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public void alwaysEnterBlockingRead(boolean b);

    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean nonBlockingReadCheckMessageParser();

    
    
    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean blockingReadCheckMessageParser();

    @ManagedAttribute
    @Description( "DESC" ) 
    public boolean timingPointsEnabled() ;
    

    @ManagedAttribute
    @Description( "DESC" ) 
    
    
    
    
    public boolean useEnumDesc() ;

    @ManagedAttribute
    @Description( "Returns true if ORB is running inside the GFv3 application server" ) 
    boolean environmentIsGFServer() ;

    @ManagedAttribute
    @Description( "If true, do not start any acceptors in the transport by default" )
    public boolean noDefaultAcceptors() ;

    
    public boolean registerMBeans() ;

    @ManagedAttribute
    @Description( "The time that a CDRInputStream will wait for more data before throwing an exception" ) 
    public int fragmentReadTimeout() ;

    public void setOrbInitArgs( String[] args ) ;

    @ManagedAttribute
    @Description( "The String[] args that were passed to the ORB init call (used for interceptor initialization)" ) 
    public String[] getOrbInitArgs() ;

    @ManagedAttribute
    @Description( "True if ORBD should not be used in this ORB instance")
    public boolean disableORBD() ;
}


