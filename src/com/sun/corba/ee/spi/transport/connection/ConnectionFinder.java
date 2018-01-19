


package com.sun.corba.ee.spi.transport.connection;




public interface ConnectionFinder<C extends Connection> {
    
    C find( ContactInfo<C> cinfo, Collection<C> idleConnections, 
        Collection<C> busyConnections ) throws IOException ;
}

