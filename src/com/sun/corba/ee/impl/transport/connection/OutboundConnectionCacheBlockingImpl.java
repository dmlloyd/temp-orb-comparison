


package xxxx;







@Transport
@ManagedObject
@Description( "Outbound connection cache for connections opened by the client" ) 
public final class OutboundConnectionCacheBlockingImpl<C extends Connection> 
    extends ConnectionCacheBlockingBase<C> 
    implements OutboundConnectionCache<C> {
   
    private ReentrantLock lock = new ReentrantLock() ;

    
    
    private final int maxParallelConnections ;  
                                                
                                                

    @ManagedAttribute
    public int maxParallelConnections() { return maxParallelConnections ; }
    
    private Map<ContactInfo<C>,OutboundCacheEntry<C>> entryMap ;

    @ManagedAttribute( id="cacheEntries" ) 
    private Map<ContactInfo<C>,OutboundCacheEntry<C>> entryMap() {
        return new HashMap<ContactInfo<C>,OutboundCacheEntry<C>>( entryMap ) ;
    }
    
    private Map<C,OutboundConnectionState<C>> connectionMap ;

    @ManagedAttribute( id="connections" ) 
    private Map<C,OutboundConnectionState<C>> connectionMap() {
        return new HashMap<C,OutboundConnectionState<C>>( connectionMap ) ;
    }

    protected String thisClassName() {
        return "OutboundConnectionCacheBlockingImpl" ;
    }

    public OutboundConnectionCacheBlockingImpl( final String cacheType, 
        final int highWaterMark, final int numberToReclaim, 
        final int maxParallelConnections, final long ttl ) {

        super( cacheType, highWaterMark, numberToReclaim, ttl ) ;

        if (maxParallelConnections < 1) 
            throw new IllegalArgumentException( 
                "maxParallelConnections must be > 0" ) ;

        this.maxParallelConnections = maxParallelConnections ;

        this.entryMap = 
            new HashMap<ContactInfo<C>,OutboundCacheEntry<C>>() ;
        this.connectionMap = new HashMap<C,OutboundConnectionState<C>>() ;
        this.reclaimableConnections = 
            ConcurrentQueueFactory.<C>makeConcurrentQueue( ttl ) ;
    }

    public boolean canCreateNewConnection( ContactInfo<C> cinfo ) {
        lock.lock() ;
        try {
            OutboundCacheEntry<C> entry = entryMap.get( cinfo ) ;
            if (entry == null)
                return true ;

            return internalCanCreateNewConnection( entry ) ;
        } finally {
            lock.unlock() ;
        }
    }

    private boolean internalCanCreateNewConnection( 
        final OutboundCacheEntry<C> entry ) {
        lock.lock() ;
        try {
            final boolean createNewConnection = (entry.totalConnections() == 0) ||
                ((numberOfConnections() < highWaterMark()) &&
                (entry.totalConnections() < maxParallelConnections)) ;

            return createNewConnection ;
        } finally {
            lock.unlock() ;
        }
    }

    public C get( final ContactInfo<C> cinfo) throws IOException {
        return get( cinfo, null ) ;
    }

    @InfoMethod
    private void msg( String m ) {}

    @InfoMethod
    private void display( String m, Object value ) {}

    @Transport
    public C get( final ContactInfo<C> cinfo,
        final ConnectionFinder<C> finder ) throws IOException {
        lock.lock() ;
        C result = null ;

        try {
            while (true) {
                final OutboundCacheEntry<C> entry = getEntry( cinfo ) ;

                if (finder != null) {
                    msg( "calling finder to get a connection" ) ;
                        
                    entry.startConnect() ; 
                    
                    
                    
                    lock.unlock() ;
                    try {
                        result = finder.find( cinfo, 
                            entry.idleConnectionsView,
                            entry.busyConnectionsView ) ;
                    } finally {
                        lock.lock() ;
                        entry.finishConnect() ;
                    }

                    if (result != null) {
                        display( "finder got connection", result ) ;
                    }
                }

                if (result == null) {
                    result = entry.idleConnections.poll() ;
                }
                if (result == null) {
                    result = tryNewConnection( entry, cinfo ) ;
                }
                if (result == null) {
                    result = entry.busyConnections.poll() ;
                }

                if (result == null)  {
                    msg( "No connection available: "
                        + "awaiting a pending connection" ) ;
                    entry.waitForConnection() ;
                    continue ;
                } else {
                    OutboundConnectionState<C> cs = getConnectionState( 
                        cinfo, entry, result ) ;

                    if (cs.isBusy()) {
                        
                    } else if (cs.isIdle()) {
                        totalBusy++ ;
                        decrementTotalIdle() ;
                    } else { 
                        totalBusy++ ;
                    }

                    cs.acquire() ;
                    break ;
                }
            }
        } finally {
            display( "totalIdle", totalIdle ) ;
            display( "totalBusy", totalBusy ) ;
            lock.unlock() ;
        }

        return result ;
    }

    @Transport
    private OutboundCacheEntry<C> getEntry( final ContactInfo<C> cinfo 
        ) throws IOException {

        OutboundCacheEntry<C> result = null ;
        
        result = entryMap.get( cinfo ) ;
        if (result == null) {
            result = new OutboundCacheEntry<C>( lock ) ;
            display( "creating new OutboundCacheEntry", result ) ;
            entryMap.put( cinfo, result ) ;
        } else {
            display( "re-using existing OutboundCacheEntry", result ) ;
        }

        return result ;
    }

    
    
    @Transport
    private C tryNewConnection( final OutboundCacheEntry<C> entry, 
        final ContactInfo<C> cinfo ) throws IOException {
        
        C conn = null ;
        if (internalCanCreateNewConnection(entry)) {
            
            
            
            entry.startConnect() ;
            lock.unlock() ;
            try {
                conn = cinfo.createConnection() ;
            } finally {
                lock.lock() ;
                entry.finishConnect() ;
            }
        }

        return conn ;
    }

    @Transport
    private OutboundConnectionState<C> getConnectionState( 
        ContactInfo<C> cinfo, OutboundCacheEntry<C> entry, C conn ) {
        lock.lock() ;
        
        try {
            OutboundConnectionState<C> cs = connectionMap.get( conn ) ;
            if (cs == null) {
                cs = new OutboundConnectionState<C>( cinfo, entry, conn ) ;
                display( "creating new OutboundConnectionState ", cs ) ;
                connectionMap.put( conn, cs ) ;
            } else {
                display( "found OutboundConnectionState ", cs ) ;
            }

            return cs ;
        } finally {
            lock.unlock() ;
        }
    }

    @Transport
    public void release( final C conn, 
        final int numResponsesExpected ) {
        lock.lock() ;
        OutboundConnectionState<C> cs = null ;

        try {
            cs = connectionMap.get( conn ) ;
            if (cs == null) {
                msg( "connection was already closed" ) ;
                return ; 
            } else {
                int numResp = cs.release( numResponsesExpected ) ;
                display( "numResponsesExpected", numResponsesExpected ) ;

                if (!cs.isBusy()) {
                    boolean connectionClosed = false ;
                    if (numResp == 0) {
                        connectionClosed = reclaimOrClose( cs, conn ) ;
                    }

                    decrementTotalBusy() ;

                    if (!connectionClosed) {
                        msg( "idle connection queued" ) ;
                        totalIdle++ ;
                    }
                }
            }
        } finally {
            display( "cs", cs ) ;
            display( "totalIdle", totalIdle ) ;
            display( "totalBusy", totalBusy ) ;
            lock.unlock() ;
        }
    }

    
    @Transport
    public void responseReceived( final C conn ) {
        lock.lock() ;
        try {
            final OutboundConnectionState<C> cs = connectionMap.get( conn ) ;
            if (cs == null) {
                msg( "response received on closed connection" ) ;
                return ;
            }

            if (cs.responseReceived()) {
                reclaimOrClose( cs, conn ) ;
            }
        } finally {
            lock.unlock() ;
        }
    }
    
    
    
    @Transport
    private boolean reclaimOrClose( OutboundConnectionState<C> cs, 
        final C conn ) {

        final boolean isOverflow = numberOfConnections() >
            highWaterMark() ;

        if (isOverflow) {
            msg( "closing overflow connection" ) ;
            close( conn ) ;
        } else {
            msg( "queuing reclaimable connection" ) ;
            cs.setReclaimableHandle(
                reclaimableConnections.offer( conn ) ) ;
        }

        return isOverflow ;
    }


    
    @Transport
    public void close( final C conn ) {
        lock.lock() ;
        try {
            final OutboundConnectionState<C> cs = connectionMap.remove( conn ) ;
            if (cs == null) {
                msg( "connection was already closed" ) ;
                return ;
            }
            display( "cs", cs ) ;

            if (cs.isBusy()) {
                msg( "connection removed from busy connections" ) ;
                decrementTotalBusy() ;
            } else if (cs.isIdle()) {
                msg( "connection removed from idle connections" ) ;
                decrementTotalIdle() ;
            }
            try {
                cs.close();
            } catch (IOException ex) {
                
            }
        } finally {
            lock.unlock() ;
        }
    }

    @Transport
    private void decrementTotalIdle() {
        if (totalIdle > 0) {
            totalIdle-- ;
        } else {
            msg( "ERROR: was already 0!" ) ;
        }
    }

    @Transport
    private void decrementTotalBusy() {
        if (totalBusy > 0) {
            totalBusy-- ;
        } else {
            msg( "ERROR: count was already 0!" ) ;
        }
    }
}


