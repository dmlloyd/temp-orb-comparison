


package com.sun.corba.ee.spi.transport.concurrent ;



public final class ConcurrentQueueFactory {
    private ConcurrentQueueFactory() {} 

    
    public static <V> ConcurrentQueue makeNonBlockingConcurrentQueue(final long ttl ) {
        return new ConcurrentQueueNonBlockingImpl<V>( ttl ) ;
    }

    
    public static <V> ConcurrentQueue makeBlockingConcurrentQueue(final long ttl ) {
        return new ConcurrentQueueBlockingImpl<V>( ttl ) ;
    }

    
    public static <V> ConcurrentQueue makeConcurrentQueue(final long ttl ) {
        return new ConcurrentQueueImpl<V>( ttl ) ;
    }
}
