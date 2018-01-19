


package com.sun.corba.ee.impl.transport.connection;







@Transport
public final class InboundConnectionCacheImpl<C extends Connection> 
    extends ConnectionCacheNonBlockingBase<C> 
    implements InboundConnectionCache<C> {

    private final ConcurrentMap<C,ConnectionState<C>> connectionMap ;

    protected String thisClassName() {
        return "InboundConnectionCacheImpl" ;
    }

    private static final class ConnectionState<C extends Connection> {
        final C connection ;                            
                                                        
        final AtomicInteger busyCount ;                 
                                                        
        final AtomicInteger expectedResponseCount ;     
                                                        
                                                        

        
        
        
        
        
        ConcurrentQueue.Handle reclaimableHandle ;  
                                                    
                                                    

        ConnectionState( final C conn ) {
            this.connection = conn ;

            busyCount = new AtomicInteger() ;
            expectedResponseCount = new AtomicInteger() ;
            reclaimableHandle = null ;
        }
    }

    public InboundConnectionCacheImpl( final String cacheType, 
        final int highWaterMark, final int numberToReclaim, long ttl ) {

        super( cacheType, highWaterMark, numberToReclaim, ttl ) ;

        this.connectionMap = 
            new ConcurrentHashMap<C,ConnectionState<C>>() ;
    }

    

    public void requestReceived( final C conn ) {
        ConnectionState<C> cs = getConnectionState( conn ) ;

        final int totalConnections = totalBusy.get() + totalIdle.get() ;
        if (totalConnections > highWaterMark())
            reclaim() ;

        ConcurrentQueue.Handle<C> reclaimHandle = cs.reclaimableHandle ;
        if (reclaimHandle != null) 
            reclaimHandle.remove() ;

        int count = cs.busyCount.getAndIncrement() ;
        if (count == 0) {
            totalIdle.decrementAndGet() ;
            totalBusy.incrementAndGet() ;
        }
    }

    @InfoMethod
    private void msg( String m ) {}

    @InfoMethod
    private void display( String m, Object value ) {}

    @Transport
    public void requestProcessed( final C conn, 
        final int numResponsesExpected ) {

        final ConnectionState<C> cs = connectionMap.get( conn ) ;

        if (cs == null) {
            msg( "connection was closed");
            return ;
        } else {
            int numResp = cs.expectedResponseCount.addAndGet(
                numResponsesExpected ) ;
            int numBusy = cs.busyCount.decrementAndGet() ;

            display( "numResp", numResp ) ;
            display( "numBusy", numBusy ) ;

            if (numBusy == 0) {
                totalBusy.decrementAndGet() ;
                totalIdle.incrementAndGet() ;

                if (numResp == 0) {
                    display( "queing reclaimalbe connection", conn ) ;
                    cs.reclaimableHandle =
                        reclaimableConnections.offer( conn ) ;
                }
            }
        }
    }

    
    @Transport
    public void responseSent( final C conn ) {
        final ConnectionState<C> cs = connectionMap.get( conn ) ;
        final int waitCount = cs.expectedResponseCount.decrementAndGet() ;
        if (waitCount == 0) {
            cs.reclaimableHandle = reclaimableConnections.offer( conn ) ;
        }
    }

    
    public void close( final C conn ) {
        final ConnectionState<C> cs = connectionMap.remove( conn ) ;
        int count = cs.busyCount.get() ;
        if (count == 0)
            totalIdle.decrementAndGet() ;
        else
            totalBusy.decrementAndGet() ;

        final ConcurrentQueue.Handle rh = cs.reclaimableHandle ;
        if (rh != null)
            rh.remove() ;

        try {
            conn.close() ;
        } catch (IOException exc) {
            
        }
    }

    
    
    private ConnectionState<C> getConnectionState( C conn ) {
        
        ConnectionState<C> cs = new ConnectionState( conn ) ;
        ConnectionState<C> result = connectionMap.putIfAbsent( conn, cs ) ;
        if (result != null) {
            totalIdle.incrementAndGet() ;
            return result ;
        } else {
            return cs ;
        }
    }
}


