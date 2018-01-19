


package xxxx;


public class ORBConstants {
    private ORBConstants() {}

    public static final String STRINGIFY_PREFIX = "IOR:" ;

    






    
    
    
    public static final int NEO_FIRST_SERVICE_CONTEXT = 0x4e454f00 ;
    public static final int NUM_NEO_SERVICE_CONTEXTS = 15 ;
    public static final int TAG_ORB_VERSION = NEO_FIRST_SERVICE_CONTEXT ;

    public static final int SUN_TAGGED_COMPONENT_ID_BASE = 0x53554e00;
    public static final int SUN_SERVICE_CONTEXT_ID_BASE  = 0x53554e00;

    
    
    
    

    
    public static final int TAG_CONTAINER_ID =
        SUN_TAGGED_COMPONENT_ID_BASE + 0;
    
    public static final int TAG_REQUEST_PARTITIONING_ID = 
        SUN_TAGGED_COMPONENT_ID_BASE + 1;
    
    public static final int TAG_JAVA_SERIALIZATION_ID =
        SUN_TAGGED_COMPONENT_ID_BASE + 2;

    
    public static final int FOLB_MEMBER_ADDRESSES_TAGGED_COMPONENT_ID = 
        SUN_TAGGED_COMPONENT_ID_BASE + 3;
    
    public static final int FOLB_MEMBERSHIP_LABEL_TAGGED_COMPONENT_ID =
        SUN_TAGGED_COMPONENT_ID_BASE + 4;
    
    public static final int TAG_LOAD_BALANCING_ID = 
        SUN_TAGGED_COMPONENT_ID_BASE + 5;

    
    
    
    

    
    public static final int CONTAINER_ID_SERVICE_CONTEXT =
        SUN_SERVICE_CONTEXT_ID_BASE + 0;        

    
    public static final int FOLB_MEMBERSHIP_LABEL_SERVICE_CONTEXT_ID =
        SUN_SERVICE_CONTEXT_ID_BASE + 1;
    
    public static final int FOLB_IOR_UPDATE_SERVICE_CONTEXT_ID =
        SUN_SERVICE_CONTEXT_ID_BASE + 2;


    
    
    
    
    
    public static final int SERVANT_CACHING_POLICY      = SUNVMCID.value + 0 ;
    public static final int ZERO_PORT_POLICY            = SUNVMCID.value + 1 ;
    public static final int COPY_OBJECT_POLICY          = SUNVMCID.value + 2 ;
    public static final int REQUEST_PARTITIONING_POLICY = SUNVMCID.value + 3 ;
    public static final int REFERENCE_MANAGER_POLICY    = SUNVMCID.value + 4 ;
    public static final int LOAD_BALANCING_POLICY       = SUNVMCID.value + 5 ;

    
    
    
    
    public static final int TOA_SCID = 2 ;

    public static final int DEFAULT_SCID = TOA_SCID ;

    public static final int FIRST_POA_SCID = 32;
    public static final int MAX_POA_SCID = 63;
    public static final int TRANSIENT_SCID          = FIRST_POA_SCID ;
    public static final int PERSISTENT_SCID         = makePersistent( TRANSIENT_SCID ) ;
    public static final int SC_TRANSIENT_SCID       = FIRST_POA_SCID + 4 ;
    public static final int SC_PERSISTENT_SCID      = makePersistent( SC_TRANSIENT_SCID ) ;
    public static final int IISC_TRANSIENT_SCID     = FIRST_POA_SCID + 8 ;
    public static final int IISC_PERSISTENT_SCID    = makePersistent( IISC_TRANSIENT_SCID ) ;
    public static final int MINSC_TRANSIENT_SCID    = FIRST_POA_SCID + 12 ;
    public static final int MINSC_PERSISTENT_SCID   = makePersistent( MINSC_TRANSIENT_SCID ) ;

    public static boolean isTransient( int scid )
    {
        return (scid & 2) == 0 ;
    }

    public static int makePersistent( int scid ) 
    {
        return scid | 2 ;
    }

    

    
    
    
    
    
    
    
    

    
    
    
    

    public static final String CORBA_PREFIX = "org.omg.CORBA." ;

    public static final String INITIAL_HOST_PROPERTY =
        CORBA_PREFIX + "ORBInitialHost" ;
    public static final String INITIAL_PORT_PROPERTY =
        CORBA_PREFIX + "ORBInitialPort" ;
    public static final String INITIAL_SERVICES_PROPERTY =
        CORBA_PREFIX + "ORBInitialServices" ;
    public static final String DEFAULT_INIT_REF_PROPERTY =
        CORBA_PREFIX + "ORBDefaultInitRef" ;
    public static final String ORB_INIT_REF_PROPERTY =
        CORBA_PREFIX + "ORBInitRef" ;

    
    public static final String SUN_PREFIX = "com.sun.corba.ee." ;

    
    public static final String DEBUG_OSGI_LISTENER = SUN_PREFIX + "ORBDebugOSGIListener" ;

    
    public static final String ALLOW_LOCAL_OPTIMIZATION         = SUN_PREFIX + "ORBAllowLocalOptimization" ;
    public static final String SERVER_PORT_PROPERTY             = SUN_PREFIX + "ORBServerPort" ;
    public static final String SERVER_HOST_PROPERTY             = SUN_PREFIX + "ORBServerHost" ;
    public static final String ORB_ID_PROPERTY                  = CORBA_PREFIX + "ORBId" ;
    
    public static final String OLD_ORB_ID_PROPERTY              = SUN_PREFIX + "ORBid" ;
    public static final String ORB_SERVER_ID_PROPERTY           = CORBA_PREFIX + "ORBServerId" ;
    public static final String DEBUG_PROPERTY                   = SUN_PREFIX + "ORBDebug" ;
    public static final String INIT_DEBUG_PROPERTY              = SUN_PREFIX + "ORBInitDebug" ;
    
    public static final String USE_REP_ID = SUN_PREFIX + "ORBUseRepId";

    
    public static final String GIOP_VERSION                     = SUN_PREFIX + "giop.ORBGIOPVersion" ;
    public static final String GIOP_FRAGMENT_SIZE               = SUN_PREFIX + "giop.ORBFragmentSize" ;
    public static final String GIOP_BUFFER_SIZE                 = SUN_PREFIX + "giop.ORBBufferSize" ;
    public static final String GIOP_11_BUFFMGR                  = SUN_PREFIX + "giop.ORBGIOP11BuffMgr";
    public static final String GIOP_12_BUFFMGR                  = SUN_PREFIX + "giop.ORBGIOP12BuffMgr";
    public static final String GIOP_TARGET_ADDRESSING           = SUN_PREFIX + "giop.ORBTargetAddressing";    
    public static final int GIOP_DEFAULT_FRAGMENT_SIZE = 4096;
    public static final int GIOP_DEFAULT_BUFFER_SIZE = 4096;
    public static final int DEFAULT_GIOP_11_BUFFMGR = 0; 
    public static final int DEFAULT_GIOP_12_BUFFMGR = 2; 
    public static final short ADDR_DISP_OBJKEY = 0; 
    public static final short ADDR_DISP_PROFILE = 1; 
    public static final short ADDR_DISP_IOR = 2; 
    public static final short ADDR_DISP_HANDLE_ALL = 3; 

    
    
    
    public static final int GIOP_12_MSG_BODY_ALIGNMENT = 8;

    
    
    public static final int GIOP_FRAGMENT_DIVISOR = 8;
    public static final int GIOP_FRAGMENT_MINIMUM_SIZE = 32;

    public static final String ENV_IS_SERVER_PROPERTY = 
        SUN_PREFIX + "ORBEnvironmentIsGlassFishServer" ;

    
    public static final String HIGH_WATER_MARK_PROPERTY =
        SUN_PREFIX + "connection.ORBHighWaterMark" ;
    public static final String LOW_WATER_MARK_PROPERTY =
        SUN_PREFIX + "connection.ORBLowWaterMark" ;
    public static final String NUMBER_TO_RECLAIM_PROPERTY =
        SUN_PREFIX + "connection.ORBNumberToReclaim" ;

    public static final String ACCEPTOR_CLASS_PREFIX_PROPERTY =
        SUN_PREFIX + "transport.ORBAcceptor";

    public static final String CONTACT_INFO_LIST_FACTORY_CLASS_PROPERTY =
        SUN_PREFIX + "transport.ORBContactInfoList";

    
    public static final String LEGACY_SOCKET_FACTORY_CLASS_PROPERTY =
        SUN_PREFIX + "legacy.connection.ORBSocketFactoryClass" ;


    public static final String SOCKET_FACTORY_CLASS_PROPERTY =
        SUN_PREFIX + "transport.ORBSocketFactoryClass" ;
    public static final String LISTEN_SOCKET_PROPERTY =
        SUN_PREFIX + "transport.ORBListenSocket";
    public static final String IOR_TO_SOCKET_INFO_CLASS_PROPERTY =
        SUN_PREFIX + "transport.ORBIORToSocketInfoClass";
    public static final String IIOP_PRIMARY_TO_CONTACT_INFO_CLASS_PROPERTY =
        SUN_PREFIX + "transport.ORBIIOPPrimaryToContactInfoClass";

    
    public static final int REQUEST_PARTITIONING_MIN_THREAD_POOL_ID =  0;
    public static final int REQUEST_PARTITIONING_MAX_THREAD_POOL_ID = 63;

    
    public static final int FIRST_LOAD_BALANCING_VALUE      = 0 ;
    public static final int NO_LOAD_BALANCING               = FIRST_LOAD_BALANCING_VALUE + 0 ;
    public static final int PER_REQUEST_LOAD_BALANCING      = FIRST_LOAD_BALANCING_VALUE + 1 ;
    public static final int LAST_LOAD_BALANCING_VALUE       = PER_REQUEST_LOAD_BALANCING ;

    
    
    
    
    public static final String TRANSPORT_TCP_TIMEOUTS_PROPERTY =
        SUN_PREFIX + "transport.ORBTCPTimeouts";

    
    public static final String TRANSPORT_TCP_CONNECT_TIMEOUTS_PROPERTY = 
        SUN_PREFIX + "transport.ORBTCPConnectTimeouts" ;

    
    
    
    public static final int TRANSPORT_TCP_INITIAL_TIME_TO_WAIT = 2000;

    
    
    public static final int TRANSPORT_TCP_MAX_TIME_TO_WAIT = 6000;

    
    
    
    public static final int TRANSPORT_TCP_BACKOFF_FACTOR = 20;

    
    
    public static final int TRANSPORT_TCP_CONNECT_INITIAL_TIME_TO_WAIT = 250 ;

    
    
    public static final int TRANSPORT_TCP_CONNECT_MAX_TIME_TO_WAIT = 60000;

    
    
    
    public static final int TRANSPORT_TCP_CONNECT_BACKOFF_FACTOR = 100;

    
    public static final int TRANSPORT_TCP_CONNECT_MAX_SINGLE_WAIT = 5000 ;

    public static final String USE_NIO_SELECT_TO_WAIT_PROPERTY =
        SUN_PREFIX + "transport.ORBUseNIOSelectToWait";

    
    
    public static final String ACCEPTOR_SOCKET_TYPE_PROPERTY =
        SUN_PREFIX + "transport.ORBAcceptorSocketType";

    
    public static final String ACCEPTOR_SOCKET_USE_WORKER_THREAD_FOR_EVENT_PROPERTY =
        SUN_PREFIX + "transport.ORBAcceptorSocketUseWorkerThreadForEvent";

    
    public static final String CONNECTION_SOCKET_TYPE_PROPERTY = 
        SUN_PREFIX + "transport.ORBConnectionSocketType";

    
    public static final String CONNECTION_SOCKET_USE_WORKER_THREAD_FOR_EVENT_PROPERTY =
        SUN_PREFIX + "transport.ORBConnectionSocketUseWorkerThreadForEvent";

    
    
    
    public static final String DISABLE_DIRECT_BYTE_BUFFER_USE_PROPERTY =
        SUN_PREFIX + "transport.ORBDisableDirectByteBufferUse" ;

    
    
    
    public static final int DEFAULT_READ_BYTE_BUFFER_SIZE = 64000;
    
    
    public static final String READ_BYTE_BUFFER_SIZE_PROPERTY =
        SUN_PREFIX + "transport.ORBReadByteBufferSize";

    
    public static final int DEFAULT_POOLED_DIRECT_BYTE_BUFFER_SLAB_SIZE = 
                                                         4000000; 

    
    
    public static final String POOLED_DIRECT_BYTE_BUFFER_SLAB_SIZE_PROPERTY =
            SUN_PREFIX + "transport.ORBPooledDirectByteBufferSlabSize";

    
    
    public static final int MAX_READ_BYTE_BUFFER_SIZE_THRESHOLD = 256000; 

    
    public static final String MAX_READ_BYTE_BUFFER_SIZE_THRESHOLD_PROPERTY =
            SUN_PREFIX + "transport.ORBMaximumReadByteBufferSize";

    
    
    public static final String ALWAYS_ENTER_BLOCKING_READ_PROPERTY =
            SUN_PREFIX + "transport.ORBOptimizedReadAlwaysEnterBlockingRead";

    
    
    public static final String NON_BLOCKING_READ_CHECK_MESSAGE_PARSER_PROPERTY =
            SUN_PREFIX + "transport.ORBNonBlockingReadCheckMessageParser";

    
    
    public static final String BLOCKING_READ_CHECK_MESSAGE_PARSER_PROPERTY =
            SUN_PREFIX + "transport.ORBBlockingReadCheckMessageParser";

    public static final String SOCKET        = "Socket";
    public static final String SOCKETCHANNEL = "SocketChannel";

    public static final String WAIT_FOR_RESPONSE_TIMEOUT =
        SUN_PREFIX + "transport.ORBWaitForResponseTimeout";

    public static final String NO_DEFAULT_ACCEPTORS = 
        SUN_PREFIX + "transport.ORBNoDefaultAcceptors" ;

    public static final String REGISTER_MBEANS = 
        SUN_PREFIX + "ORBRegisterMBeans" ;

    public static final int DEFAULT_FRAGMENT_READ_TIMEOUT = 18000 ;

    public static final String FRAGMENT_READ_TIMEOUT = 
        SUN_PREFIX + "ORBFragmentReadTimeout" ;

    
    public static final String PERSISTENT_SERVER_PORT_PROPERTY  = 
        SUN_PREFIX + "POA.ORBPersistentServerPort" ;
    public static final String BAD_SERVER_ID_HANDLER_CLASS_PROPERTY = 
        SUN_PREFIX + "POA.ORBBadServerIdHandlerClass" ;
    public static final String ACTIVATED_PROPERTY               = 
        SUN_PREFIX + "POA.ORBActivated" ;
    public static final String SERVER_NAME_PROPERTY             = 
        SUN_PREFIX + "POA.ORBServerName" ;

    
    

    public static final String SERVER_DEF_VERIFY_PROPERTY       = 
        SUN_PREFIX + "activation.ORBServerVerify" ;

    public static final String JTS_CLASS_PROPERTY               = 
        SUN_PREFIX + "CosTransactions.ORBJTSClass" ;

    
    public static final String ENABLE_JAVA_SERIALIZATION_PROPERTY = 
        SUN_PREFIX + "encoding.ORBEnableJavaSerialization";

    

    public static final String PI_ORB_INITIALIZER_CLASS_PREFIX   =
        "org.omg.PortableInterceptor.ORBInitializerClass.";

    public static final String USER_CONFIGURATOR_PREFIX = 
        SUN_PREFIX + "ORBUserConfigurators." ;

    public static final String RFM_PROPERTY = USER_CONFIGURATOR_PREFIX 
        + "com.sun.corba.ee.impl.oa.rfm.ReferenceManagerConfigurator" ;

    public static final String USE_DYNAMIC_STUB_PROPERTY = 
        SUN_PREFIX + "ORBUseDynamicStub" ;

    public static final String DEBUG_DYNAMIC_STUB =
        SUN_PREFIX + "ORBDebugStubGeneration" ;
    
    
    
    public static final String USE_CODEGEN_REFLECTIVE_COPYOBJECT = 
        SUN_PREFIX + "ORBUseCodegenReflectiveCopyobject" ;

    public static final String DYNAMIC_STUB_FACTORY_FACTORY_CLASS =
        SUN_PREFIX + "ORBDynamicStubFactoryFactoryClass" ;
    
    

    public static final int DEFAULT_INITIAL_PORT                 = 900;

    public static final String DEFAULT_INS_HOST = "localhost";

    public static final int DEFAULT_INS_PORT                     = 2809;

    public static final int DEFAULT_INS_GIOP_MAJOR_VERSION       = 1;

    
    
    public static final int DEFAULT_INS_GIOP_MINOR_VERSION       = 0;


    

    
    public static final int MAJORNUMBER_SUPPORTED                 = 1;
    public static final int MINORNUMBERMAX                        = 2;

    
    
    public static final int TRANSIENT                             = 1;
    public static final int PERSISTENT                            = 2;

    

    
    public static final String DISABLE_ORBD_INIT_PROPERTY       = SUN_PREFIX + "activation.ORBDisableORBD" ;

    

    public static final String DB_DIR_PROPERTY                  = SUN_PREFIX + "activation.DbDir" ;
    public static final String DB_PROPERTY                      = SUN_PREFIX + "activation.db" ;
    public static final String ORBD_PORT_PROPERTY               = SUN_PREFIX + "activation.Port" ;
    public static final String SERVER_POLLING_TIME              = SUN_PREFIX + "activation.ServerPollingTime";
    public static final String SERVER_STARTUP_DELAY             = SUN_PREFIX + "activation.ServerStartupDelay";

    public static final int DEFAULT_ACTIVATION_PORT             = 1049 ;

    
    
    
    public static final int RI_NAMESERVICE_PORT                 = 1050;

    public static final int DEFAULT_SERVER_POLLING_TIME         = 1000;

    public static final int DEFAULT_SERVER_STARTUP_DELAY        = 1000;


    

    public static final String LOG_LEVEL_PROPERTY               = SUN_PREFIX + "ORBLogLevel";

    public static final String LOG_RESOURCE_FILE                = 
        "com.sun.corba.ee.impl.logging.LogStrings";

    public static final String SHOW_INFO_MESSAGES
        = SUN_PREFIX + "ORBShowInfoMessages";

    public static final String GET_SERVICE_CONTEXT_RETURNS_NULL
        = SUN_PREFIX + "ORBGetServiceContextReturnsNull";

    

    public static final String TRANSIENT_NAME_SERVICE_NAME = "TNameService" ;
    public static final String PERSISTENT_NAME_SERVICE_NAME = "NameService" ;

    
    
    public static final String NAME_SERVICE_SERVER_ID   = "1000000" ;

    public static final String ROOT_POA_NAME            = "RootPOA" ;
    public static final String POA_CURRENT_NAME         = "POACurrent" ;
    public static final String SERVER_ACTIVATOR_NAME    = "ServerActivator" ;
    public static final String SERVER_LOCATOR_NAME      = "ServerLocator" ;
    public static final String SERVER_REPOSITORY_NAME   = "ServerRepository" ;
    public static final String INITIAL_NAME_SERVICE_NAME= "InitialNameService" ;
    public static final String TRANSACTION_CURRENT_NAME = "TransactionCurrent" ;
    public static final String DYN_ANY_FACTORY_NAME     = "DynAnyFactory" ;
    public static final String REFERENCE_FACTORY_MANAGER= "ReferenceFactoryManager" ; 

    
    public static final String PI_CURRENT_NAME          = "PICurrent" ;
    public static final String CODEC_FACTORY_NAME       = "CodecFactory" ;

    public static final String FOLB_CLIENT_GROUP_INFO_SERVICE = 
        "FolbClientGroupInfoService";

    public static final String FOLB_SERVER_GROUP_INFO_SERVICE = 
        "FolbServerGroupInfoService";

    public static final String CSI_V2_SSL_TAGGED_COMPONENT_HANDLER =
        "CSIv2SSLTaggedComponentHandler";


    

    public static final String DEFAULT_DB_DIR       = "orb.db" ;
    public static final String DEFAULT_DB_NAME      = "db" ;
    public static final String INITIAL_ORB_DB       = "initial.db" ;
    public static final String SERVER_LOG_DIR       = "logs" ;
    public static final String ORBID_DIR_BASE       = "orbids" ;
    public static final String ORBID_DB_FILE_NAME   = "orbids.db" ;

    
    
    
    
    public static final int LEGACY_SUN_NOT_SERIALIZABLE = SUNVMCID.value + 1 ;

    

    
    
    
    
    public static final boolean DEFAULT_ALWAYS_SEND_CODESET_CTX = true;
    public static final String ALWAYS_SEND_CODESET_CTX_PROPERTY
        = SUN_PREFIX + "codeset.AlwaysSendCodeSetCtx";

    
    
    public static final boolean DEFAULT_USE_BYTE_ORDER_MARKERS = true;
    public static final String USE_BOMS = SUN_PREFIX + "codeset.UseByteOrderMarkers";

    
    public static final boolean DEFAULT_USE_BYTE_ORDER_MARKERS_IN_ENCAPS = false;
    public static final String USE_BOMS_IN_ENCAPS = SUN_PREFIX + "codeset.UseByteOrderMarkersInEncaps";

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static final String CHAR_CODESETS = SUN_PREFIX + "codeset.charsets";
    public static final String WCHAR_CODESETS = SUN_PREFIX + "codeset.wcharsets";

    
    public static final byte STREAM_FORMAT_VERSION_1 = (byte)1;
    public static final byte STREAM_FORMAT_VERSION_2 = (byte)2;

    
    public static final byte CDR_ENC_VERSION = (byte) 0;
    public static final byte JAVA_ENC_VERSION = (byte) 1; 

    
    public static final String APPSERVER_MODE = SUN_PREFIX + "ORBAppServerMode";

    
    public static final String INITIAL_GROUP_INFO_SERVICE = "INITIAL_GIS" ;

    
    
    public static final String TIMING_POINTS_ENABLED = SUN_PREFIX 
        + "ORBEnableTimingPoints" ;

    
    
    public static final String USE_ENUM_DESC = SUN_PREFIX 
        + "ORBUseEnumDesc" ;

    
    
    public static final String GMBAL_ROOT_PARENT_NAME = SUN_PREFIX 
        + "ORBGmbalRootParentName" ;
}


