

package com.sun.corba.ee.impl.transport;

import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.transport.ReaderThread;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.threadpool.Work;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.spi.trace.Transport;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;

@Transport
public class ReaderThreadImpl implements ReaderThread, Work {
    private ORB orb;
    private Connection connection;
    private boolean keepRunning;
    private long enqueueTime;
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public ReaderThreadImpl(ORB orb, Connection connection)
    {
        this.orb = orb;
        this.connection = connection;
        keepRunning = true;
    }

    
    
    
    

    public Connection getConnection() {
        return connection;
    }

    @Transport
    public synchronized void close() {
        keepRunning = false;

        
        
        
        
        
    }

    private synchronized boolean isRunning() {
        return keepRunning ;
    }

    
    
    
    

    @InfoMethod
    private void display( String msg ) { }

    @InfoMethod
    private void display( String msg, Object value ) { }


    
    @Transport
    public void doWork()
    {
        while (isRunning()) {
            try {
                display( "Start readerThread cycle", connection ) ;

                if (connection.read()) {
                    
                    return;
                }

                display( "End readerThread cycle" ) ;
            } catch (Throwable t) {
                wrapper.exceptionInReaderThread( t ) ;
                display( "Exception in read", t ) ;

                orb.getTransportManager().getSelector(0)
                    .unregisterForEvent(getConnection().getEventHandler());

                try {
                    if (isRunning()) {
                        getConnection().close();
                    }
                } catch (Exception exc) {
                    wrapper.ioExceptionOnClose( exc ) ;
                }
            }
        }
    }

    public void setEnqueueTime(long timeInMillis) {
        enqueueTime = timeInMillis;
    }

    public long getEnqueueTime() {
        return enqueueTime;
    }

    public String getName() { return "ReaderThread"; }
}


