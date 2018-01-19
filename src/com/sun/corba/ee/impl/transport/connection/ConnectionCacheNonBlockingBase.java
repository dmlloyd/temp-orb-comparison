


package com.sun.corba.ee.impl.transport.connection;





abstract class ConnectionCacheNonBlockingBase<C extends Connection> 
    extends ConnectionCacheBase<C> {

    protected final AtomicInteger totalBusy ;   
    protected final AtomicInteger totalIdle ;   

    ConnectionCacheNonBlockingBase( String cacheType, int highWaterMark,
        int numberToReclaim, long ttl ) {

        super( cacheType, highWaterMark, numberToReclaim) ;

        this.totalBusy = new AtomicInteger() ;
        this.totalIdle = new AtomicInteger() ;

        this.reclaimableConnections = 
            
            ConcurrentQueueFactory.<C>makeBlockingConcurrentQueue( ttl ) ;
    }

    public long numberOfConnections() {
        return totalIdle.get() + totalBusy.get() ;
    }

    public long numberOfIdleConnections() {
        return totalIdle.get() ;
    }

    public long numberOfBusyConnections() {
        return totalBusy.get() ;
    }

    public long numberOfReclaimableConnections() {
        return reclaimableConnections.size() ;
    }
}
 
