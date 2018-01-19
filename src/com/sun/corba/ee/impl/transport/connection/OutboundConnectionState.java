


package xxxx;





@Transport
@ManagedData
public class OutboundConnectionState<C extends Connection> {

    private enum ConnectionStateValue { NEW, BUSY, IDLE }

    private ConnectionStateValue csv ;  

    final ContactInfo<C> cinfo ;        
                                        
    final C connection ;                
                                        
    final OutboundCacheEntry<C> entry ; 

    private int busyCount ;             
              
    int expectedResponseCount ;         
                                        

    
    
    
    
    ConcurrentQueue.Handle<C> reclaimableHandle ;   
                                                    
                                                    
                                                    

    public String toString() {
        return "OutboundConnectionState[csv=" + csv
            + ",cinfo=" + cinfo
            + ",connection=" + connection
            + ",busyCount=" + busyCount
            + ",expectedResponceCount=" + expectedResponseCount + "]" ;
    }


    @ManagedAttribute
    @Description( "The current state of this connection")
    private synchronized ConnectionStateValue state() { return csv ; }

    @ManagedAttribute
    @Description( "The contactInfo used to create this connection")
    private synchronized ContactInfo<C> contactInfo() { return cinfo ; }

    @ManagedAttribute
    @Description( "The underlying connection for this ConnectionState")
    private synchronized C connection() { return connection ; }

    @ManagedAttribute
    private synchronized OutboundCacheEntry<C> cacheEntry() { return entry ; }
    
    @ManagedAttribute
    private synchronized int busyCount() { return busyCount ; }

    @ManagedAttribute
    private synchronized int expectedResponseCount() {
        return expectedResponseCount ;
    }

    @ManagedAttribute
    public synchronized boolean isReclaimable() {
        return reclaimableHandle != null ;
    }

    public OutboundConnectionState( final ContactInfo<C> cinfo, 
        final OutboundCacheEntry<C> entry, final C conn ) {

        this.csv = ConnectionStateValue.NEW ;
        this.cinfo = cinfo ;
        this.connection = conn ;
        this.entry = entry ;

        busyCount = 0 ;
        expectedResponseCount = 0 ;
        reclaimableHandle = null ;
    }



    public synchronized boolean isBusy() { 
        return csv == ConnectionStateValue.BUSY ; 
    } 

    public synchronized boolean isIdle() { 
        return csv == ConnectionStateValue.IDLE ; 
    } 

    
    
    @Transport
    public synchronized void acquire() { 
        if (busyCount == 0) {
            entry.idleConnections.remove( connection ) ;
            removeFromReclaim() ;
            csv = ConnectionStateValue.BUSY ;
        } else {
            
            
            entry.busyConnections.remove( connection ) ;
        }

        busyCount++ ;
        entry.busyConnections.offer( connection ) ;
    }

    public synchronized void setReclaimableHandle( 
        ConcurrentQueue.Handle<C> handle ) {
        reclaimableHandle = handle ;
    }

    @InfoMethod
    private void msg( String m ) {}

    @InfoMethod
    private void display( String m, Object value ) {}

    
    
    @Transport
    public synchronized int release( int numResponsesExpected ) {
        expectedResponseCount += numResponsesExpected ;
        busyCount-- ;
        if (busyCount < 0) {
            msg( "ERROR: numBusy is <0!" ) ;
        }

        if (busyCount == 0) {
            csv = ConnectionStateValue.IDLE ;
            boolean wasOnBusy = entry.busyConnections.remove( connection ) ;
            if (!wasOnBusy) {
               msg( "connection not on busy queue, should have been" ) ;
            }
            entry.idleConnections.offer( connection ) ;
        }

        display( "expectedResponseCount", expectedResponseCount ) ;
        display( "busyCount", busyCount ) ;

        return expectedResponseCount ;
    }

    
    @Transport
    public synchronized boolean responseReceived() {
        boolean result = false ;
        --expectedResponseCount ;
        display( "expectedResponseCount", expectedResponseCount ) ;

        if (expectedResponseCount < 0) {
            msg( "ERROR: expectedResponseCount<0!" ) ;
            expectedResponseCount = 0 ;
        }

        result = (expectedResponseCount == 0) && (busyCount == 0) ;

        return result ;
    }

    @Transport
    public synchronized void close() throws IOException {
        removeFromReclaim() ;

        if (csv == ConnectionStateValue.IDLE) {
            entry.idleConnections.remove( connection ) ;
        } else if (csv == ConnectionStateValue.BUSY) {
            entry.busyConnections.remove( connection ) ;
        }

        csv = ConnectionStateValue.NEW ;
        busyCount = 0 ;
        expectedResponseCount = 0  ;

        connection.close() ;
    }

    @Transport
    private void removeFromReclaim() {
        if (reclaimableHandle != null) {
            if (!reclaimableHandle.remove()) {
                display( "result was not on reclaimable Q", cinfo ) ;
            }
            reclaimableHandle = null ;
        }
    }
}

