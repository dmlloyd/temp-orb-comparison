


package xxxx;







@Transport
public final class InboundConnectionCacheBlockingImpl<C extends Connection> 
    extends ConnectionCacheBlockingBase<C> 
    implements InboundConnectionCache<C> {

    private final Map<C,ConnectionState<C>> connectionMap ;

    protected String thisClassName() {
        return "InboundConnectionCacheBlockingImpl" ;
    }

    private static final class ConnectionState<C extends Connection> {
        final C connection ;            
                                        
        int busyCount ;                 
                                        
        int expectedResponseCount ;     
                                        
                                        

        ConcurrentQueue.Handle reclaimableHandle ;  
                                                    
                                                    

        ConnectionState( final C conn ) {
            this.connection = conn ;

            busyCount = 0 ;
            expectedResponseCount = 0 ;
            reclaimableHandle = null ;
        }
    }

    public InboundConnectionCacheBlockingImpl( final String cacheType, 
        final int highWaterMark, final int numberToReclaim, final long ttl ) {

        super( cacheType, highWaterMark, numberToReclaim, ttl ) ;

        this.connectionMap = new HashMap<C,ConnectionState<C>>() ;
    }

    

    @InfoMethod
    private void display( String msg, Object value ) {}

    @InfoMethod
    private void msg( String msg ) {}

    @Transport
    public synchronized void requestReceived( final C conn ) {
        ConnectionState<C> cs = getConnectionState( conn ) ;

        final int totalConnections = totalBusy + totalIdle ;
        if (totalConnections > highWaterMark())
            reclaim() ;

        ConcurrentQueue.Handle<C> reclaimHandle = cs.reclaimableHandle ;
        if (reclaimHandle != null) {
            reclaimHandle.remove() ;
            display( "removed from reclaimableQueue", conn ) ;
        }

        int count = cs.busyCount++ ;
        if (count == 0) {
            display( "moved from idle to busy", conn ) ;

            totalIdle-- ;
            totalBusy++ ;
        }
    }

    @Transport
    public synchronized void requestProcessed( final C conn, 
        final int numResponsesExpected ) {
        final ConnectionState<C> cs = connectionMap.get( conn ) ;

        if (cs == null) {
            msg( "connection was closed") ;
            return ;
        } else {
            cs.expectedResponseCount += numResponsesExpected ;
            int numResp = cs.expectedResponseCount ;
            int numBusy = --cs.busyCount ;

            display( "responses expected", numResp ) ;
            display( "connection busy count", numBusy ) ;

            if (numBusy == 0) {
                totalBusy-- ;
                totalIdle++ ;

                if (numResp == 0) {
                    display( "queuing reclaimable connection", conn ) ;

                    if ((totalBusy+totalIdle) > highWaterMark()) {
                        close( conn ) ;
                    } else {
                        cs.reclaimableHandle =
                            reclaimableConnections.offer( conn ) ;
                    }
                }
            }
        }
    }

    
    @Transport
    public synchronized void responseSent( final C conn ) {
        final ConnectionState<C> cs = connectionMap.get( conn ) ;
        final int waitCount = --cs.expectedResponseCount ;
        if (waitCount == 0) {
            display( "reclaimable connection", conn ) ;

            if ((totalBusy+totalIdle) > highWaterMark()) {
                close( conn ) ;
            } else {
                cs.reclaimableHandle =
                    reclaimableConnections.offer( conn ) ;
            }
        } else {
            display( "wait count", waitCount ) ;
        }
    }

    
    @Transport
    public synchronized void close( final C conn ) {
        final ConnectionState<C> cs = connectionMap.remove( conn ) ;
        display( "connection state", cs ) ;

        int count = cs.busyCount ;

        if (count == 0)
            totalIdle-- ;
        else
            totalBusy-- ;

        final ConcurrentQueue.Handle rh = cs.reclaimableHandle ;
        if (rh != null) {
            msg( "connection was reclaimable") ;
            rh.remove() ;
        }

        try {
            conn.close() ;
        } catch (IOException exc) {
            display( "close threw", exc ) ;
        }
    }

    
    
    private ConnectionState<C> getConnectionState( C conn ) {
        
        ConnectionState<C> result = connectionMap.get( conn ) ;
        if (result == null) {
            result = new ConnectionState( conn ) ;
            connectionMap.put( conn, result ) ;
            totalIdle++ ;
        }

        return result ;
    }
}


