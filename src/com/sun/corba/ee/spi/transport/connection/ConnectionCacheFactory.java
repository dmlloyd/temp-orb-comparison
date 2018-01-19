


package com.sun.corba.ee.spi.transport.connection;




public final class ConnectionCacheFactory {
    private ConnectionCacheFactory() {}

    public static <C extends Connection> OutboundConnectionCache<C>
    makeBlockingOutboundConnectionCache( String cacheType, int highWaterMark,
        int numberToReclaim, int maxParallelConnections, int ttl ) {

        return new OutboundConnectionCacheBlockingImpl<C>( cacheType, highWaterMark,
            numberToReclaim, maxParallelConnections, ttl ) ;
    }

    public static <C extends Connection> OutboundConnectionCache<C>
    makeNonBlockingOutboundConnectionCache( String cacheType, int highWaterMark,
        int numberToReclaim, int maxParallelConnections, int ttl ) {

        return new OutboundConnectionCacheImpl<C>( cacheType, highWaterMark,
            numberToReclaim, maxParallelConnections, ttl ) ;
    }

    public static <C extends Connection> InboundConnectionCache<C>
    makeBlockingInboundConnectionCache( String cacheType, int highWaterMark,
        int numberToReclaim, int ttl ) {
        return new InboundConnectionCacheBlockingImpl<C>( cacheType,
            highWaterMark, numberToReclaim, ttl ) ;
    }

    public static <C extends Connection> InboundConnectionCache<C> 
    makeNonBlockingInboundConnectionCache( String cacheType, int highWaterMark,
        int numberToReclaim, int ttl ) {
        return new InboundConnectionCacheImpl<C>( cacheType,
            highWaterMark, numberToReclaim, ttl ) ;
    }
}
