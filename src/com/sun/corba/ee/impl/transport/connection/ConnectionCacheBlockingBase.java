


package com.sun.corba.ee.impl.transport.connection;


import com.sun.corba.ee.spi.transport.connection.Connection ;

import com.sun.corba.ee.spi.transport.concurrent.ConcurrentQueueFactory;

abstract class ConnectionCacheBlockingBase<C extends Connection> 
    extends ConnectionCacheBase<C> {

    protected int totalBusy ;   
    protected int totalIdle ;   

    ConnectionCacheBlockingBase( String cacheType, int highWaterMark,
        int numberToReclaim, long ttl ) {

        super( cacheType, highWaterMark, numberToReclaim) ;

        this.totalBusy = 0 ;
        this.totalIdle = 0 ;

        this.reclaimableConnections = 
            ConcurrentQueueFactory.<C>makeConcurrentQueue( ttl ) ;
    }

    public synchronized long numberOfConnections() {
        return totalIdle + totalBusy ;
    }

    public synchronized long numberOfIdleConnections() {
        return totalIdle ;
    }

    public synchronized long numberOfBusyConnections() {
        return totalBusy ;
    }

    public synchronized long numberOfReclaimableConnections() {
        return reclaimableConnections.size() ;
    }
}
 
