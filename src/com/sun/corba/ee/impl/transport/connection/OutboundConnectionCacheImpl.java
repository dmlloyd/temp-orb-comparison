


package com.sun.corba.ee.impl.transport.connection;







public final class OutboundConnectionCacheImpl<C extends Connection> 
    extends ConnectionCacheNonBlockingBase<C> 
    implements OutboundConnectionCache<C> {

    private final int maxParallelConnections ;  
                                                
                                                

    private final ConcurrentMap<ContactInfo<C>,CacheEntry<C>> entryMap ;
    private final ConcurrentMap<C,ConnectionState<C>> connectionMap ;

    public int maxParallelConnections() {
        return maxParallelConnections ;
    }

    protected String thisClassName() {
        return "OutboundConnectionCacheImpl" ;
    }

    private static final class ConnectionState<C extends Connection> {
        final ContactInfo<C> cinfo ;                    
                                                        
        final C connection ;                            
                                                        
        final CacheEntry<C> entry ;                     
                                                        

        final AtomicInteger busyCount ;                 
                                                        
        final AtomicInteger expectedResponseCount ;     
                                                        
                                                        

        
        
        
        
        
        volatile ConcurrentQueue.Handle reclaimableHandle ;  
                                                             
                                                             
        volatile ConcurrentQueue.Handle idleHandle ;         
                                                             
        volatile ConcurrentQueue.Handle busyHandle ;         
                                                             

        ConnectionState( final ContactInfo<C> cinfo, final CacheEntry<C> entry, 
            final C conn ) {

            this.cinfo = cinfo ;
            this.connection = conn ;
            this.entry = entry ;

            busyCount = new AtomicInteger() ;
            expectedResponseCount = new AtomicInteger() ;
            reclaimableHandle = null ;
            idleHandle = null ;
            busyHandle = null ;
        }
    }

    
    
    
    private static final class CacheEntry<C extends Connection> {
        final ConcurrentQueue<C> idleConnections =
            ConcurrentQueueFactory.<C>makeBlockingConcurrentQueue(0) ;

        final ConcurrentQueue<C> busyConnections =
            ConcurrentQueueFactory.<C>makeBlockingConcurrentQueue(0) ;

        public int totalConnections() {
            return idleConnections.size() + busyConnections.size() ;
        }
    }

    public OutboundConnectionCacheImpl( final String cacheType, 
        final int highWaterMark, final int numberToReclaim, 
        final int maxParallelConnections, final long ttl ) {

        super( cacheType, highWaterMark, numberToReclaim, ttl ) ;
        this.maxParallelConnections = maxParallelConnections ;

        this.entryMap = 
            new ConcurrentHashMap<ContactInfo<C>,CacheEntry<C>>() ;
        this.connectionMap = 
            new ConcurrentHashMap<C,ConnectionState<C>>() ;
        this.reclaimableConnections = 
            ConcurrentQueueFactory.<C>makeBlockingConcurrentQueue( ttl ) ;
    }

    

    public C get( final ContactInfo<C> cinfo,
        ConnectionFinder<C> finder ) throws IOException {

        return get( cinfo ) ;
    }

    public C get( final ContactInfo<C> cinfo ) throws IOException {
        final CacheEntry<C> entry = getEntry( cinfo ) ;
        C result = null ;

        final int totalConnections = totalBusy.get() + totalIdle.get() ;
        if (totalConnections >= highWaterMark())
            reclaim() ;

        do {
            result = entry.idleConnections.poll().value() ;
            if (result == null) {
                if (canCreateNewConnection( entry )) { 
                    
                    
                    
                    result = cinfo.createConnection() ; 

                    final ConnectionState<C> cs = new ConnectionState<C>( cinfo, 
                        entry, result ) ;
                    connectionMap.put( result, cs ) ;

                    
                    
                    
                    
                    
                    cs.busyCount.incrementAndGet() ;
                    entry.busyConnections.offer( result ) ;
                    totalBusy.incrementAndGet() ;
                } else { 
                    
                    

                    result = entry.busyConnections.poll().value() ;
                    if (result != null) {
                        entry.busyConnections.offer( result ) ;
                    }
                }
            } else { 
                final ConnectionState<C> cs = connectionMap.get( result ) ;
                if (cs == null) {
                    
                    result = null ;
                } else {
                    final ConcurrentQueue.Handle<C> handle = cs.reclaimableHandle ;
                    if (handle != null) {
                        if (handle.remove()) {
                            totalIdle.decrementAndGet() ;
                            totalBusy.incrementAndGet() ;
                            entry.busyConnections.offer( result ) ;
                        } else {
                            
                            result = null ;
                        }       
                    }
                }
            }
        } while (result == null) ;

        return result ;
    }

    public void release( final C conn, final int numResponsesExpected ) {
        try {
            final ConnectionState<C> cs = connectionMap.get( conn ) ;

            if (cs == null) {
                return ; 
            } else {
                int numResp = cs.expectedResponseCount.addAndGet( 
                    numResponsesExpected ) ;
                int numBusy = cs.busyCount.decrementAndGet() ;

                if (numBusy == 0) {
                    final ConcurrentQueue.Handle busyHandle = cs.busyHandle ;
                    final CacheEntry<C> entry = cs.entry ;
                    boolean wasOnBusy = false ;
                    if (busyHandle != null)
                        wasOnBusy = busyHandle.remove() ;

                    if (wasOnBusy) {
                        
                        
                        
                        
                        
                        
                        
                        
                    
                        if (cs.busyCount.get() > 0) {
                            cs.busyHandle = entry.busyConnections.offer( conn ) ;
                        } else {
                            
                            
                            
                            
                            
                            
                            if (cs.expectedResponseCount.get() == 0) {
                                cs.reclaimableHandle = 
                                    reclaimableConnections.offer( conn ) ;
                                totalBusy.decrementAndGet() ;
                            }

                            cs.idleHandle = entry.idleConnections.offer( conn ) ;
                        }
                    }
                }
            }
        } finally {
        }
    }

    
    public void responseReceived( final C conn ) {
        final ConnectionState<C> cs = connectionMap.get( conn ) ;
        if (cs == null) {
            return ;
        }

        final ConcurrentQueue.Handle<C> idleHandle = cs.idleHandle ;
        final CacheEntry<C> entry = cs.entry ;
        final int waitCount = cs.expectedResponseCount.decrementAndGet() ;
        if (waitCount == 0) {
            boolean wasOnIdle = false ;
            if (cs != null)
                wasOnIdle = cs.idleHandle.remove() ;

            if (wasOnIdle)
                cs.reclaimableHandle = reclaimableConnections.offer( conn ) ;
        }
    }

    
    public void close( final C conn ) {
        final ConnectionState<C> cs = connectionMap.remove( conn ) ;
        if (cs == null) {
            return ;
        }

        final CacheEntry<C> entry = entryMap.remove( cs.cinfo ) ;

        final ConcurrentQueue.Handle rh = cs.reclaimableHandle ;
        if (rh != null)
            rh.remove() ;

        final ConcurrentQueue.Handle bh = cs.busyHandle ;
        if (bh != null)
            bh.remove() ;

        final ConcurrentQueue.Handle ih = cs.idleHandle ;
        if (ih != null)
            ih.remove() ;

        try { 
            conn.close() ;
        } catch (IOException exc) {
            
        }
    }

    
    
    private CacheEntry<C> getEntry( ContactInfo<C> cinfo ) {
        
        CacheEntry<C> entry = new CacheEntry() ;
        CacheEntry<C> result = entryMap.putIfAbsent( cinfo, entry ) ;
        if (result != null)
            return result ;
        else
            return entry ;
    }

    
    
    
    private boolean canCreateNewConnection( final CacheEntry<C> entry ) {
        final int totalConnections = totalBusy.get() + totalIdle.get() ;
        final int totalConnectionsInEntry = entry.totalConnections() ;
        return (totalConnectionsInEntry == 0) || 
            ((totalConnections < highWaterMark()) && 
            (totalConnectionsInEntry < maxParallelConnections)) ;
    }

    public boolean canCreateNewConnection( final ContactInfo<C> cinfo ) {
        final CacheEntry<C> entry = entryMap.get( cinfo ) ;
        if (entry == null)
            return true ;

        return canCreateNewConnection( entry ) ;
    }
}


