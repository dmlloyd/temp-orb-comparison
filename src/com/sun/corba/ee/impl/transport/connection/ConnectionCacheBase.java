


package com.sun.corba.ee.impl.transport.connection;


import com.sun.corba.ee.spi.transport.connection.Connection ;
import com.sun.corba.ee.spi.transport.connection.ConnectionCache ;

import com.sun.corba.ee.spi.transport.concurrent.ConcurrentQueue ;
import com.sun.corba.ee.spi.transport.concurrent.ConcurrentQueue.Handle;
import com.sun.corba.ee.spi.trace.Transport;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;

@Transport
public abstract class ConnectionCacheBase<C extends Connection> 
    implements ConnectionCache<C> {

    private boolean flag ;

    
    private final String cacheType ;

    
    
    private final int highWaterMark ;           
                                                
                                                
    private final int numberToReclaim ;         
                                                

    
    protected ConcurrentQueue<C> reclaimableConnections = null ;

    public final String getCacheType() {
        return cacheType ;
    }

    public final int numberToReclaim() {
        return numberToReclaim ;
    }

    public final int highWaterMark() {
        return highWaterMark ;
    }

    
    
    
    protected abstract String thisClassName() ;

    ConnectionCacheBase( final String cacheType, 
        final int highWaterMark, final int numberToReclaim ) {

        if (cacheType == null)
            throw new IllegalArgumentException( 
                "cacheType must not be null" ) ;

        if (highWaterMark < 0)
            throw new IllegalArgumentException( 
                "highWaterMark must be non-negative" ) ;

        if (numberToReclaim < 1)
            throw new IllegalArgumentException( 
                "numberToReclaim must be at least 1" ) ;

        this.cacheType = cacheType ;
        this.highWaterMark = highWaterMark ;
        this.numberToReclaim = numberToReclaim ;
    }
    
    @Override
    public String toString() {
        return thisClassName() + "[" 
            + getCacheType() + "]";
    }

    @InfoMethod
    private void display( String msg, Object value ) {}

    
    @Transport
    protected boolean reclaim() {
        int ctr = 0 ;
        while (ctr < numberToReclaim()) {
            Handle<C> candidate = reclaimableConnections.poll() ;
            if (candidate == null)
                
                
                break ;

            try {
                display("closing connection", candidate) ;
                close( candidate.value() ) ;
            } catch (RuntimeException exc) {
                display( "exception on close", exc ) ;
                throw exc ;
            }

            ctr++ ;
        }

        display( "number of connections reclaimed", ctr ) ;
        return ctr > 0 ;
    }
}
