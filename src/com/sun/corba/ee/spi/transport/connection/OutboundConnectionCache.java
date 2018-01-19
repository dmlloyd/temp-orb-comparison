


package com.sun.corba.ee.spi.transport.connection;



public interface OutboundConnectionCache<C extends Connection> 
    extends ConnectionCache<C> {
    
    int maxParallelConnections() ;

    
    boolean canCreateNewConnection( ContactInfo<C> cinfo ) ;

    
    C get( ContactInfo<C> cinfo, ConnectionFinder<C> finder 
        ) throws IOException ;

    
    C get( ContactInfo<C> cinfo ) throws IOException ;

    
    void release( C conn, int numResponseExpected ) ;

    
    void responseReceived( C conn ) ;
}
