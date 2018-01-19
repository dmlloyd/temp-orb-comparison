


package com.sun.corba.ee.spi.transport;






@ManagedObject 
@Description( "An Acceptor represents an endpoint on which the ORB handles incoming connections" ) 
public abstract interface Acceptor
{
    @ManagedAttribute
    @Description( "The TCP port of this Acceptor" )  
    int getPort() ;

    @ManagedAttribute
    @Description( "The name of the IP interface for this Acceptor" ) 
    String getInterfaceName() ;

    @ManagedAttribute
    @Description( "The type of requests that this Acceptor handles" ) 
    String getType() ;

    @ManagedAttribute
    @Description( "True if this acceptor is used to lazily start the ORB" ) 
    boolean isLazy() ;

    void addToIORTemplate(IORTemplate iorTemplate, Policies policies,
                                 String codebase);
    String getMonitoringName();

    
    boolean initialize();

    
    boolean initialized();

    String getConnectionCacheType();

    void setConnectionCache(InboundConnectionCache connectionCache);

    InboundConnectionCache getConnectionCache();

    
    boolean shouldRegisterAcceptEvent();

    
    Socket getAcceptedSocket() ; 

    
    void processSocket( Socket channel ) ;

    
    void close();

    EventHandler getEventHandler();

    CDROutputObject createOutputObject(ORB broker, MessageMediator messageMediator);
    
    ServerSocket getServerSocket();
}


