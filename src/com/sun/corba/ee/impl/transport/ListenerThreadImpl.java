

package com.sun.corba.ee.impl.transport;


import com.sun.corba.ee.spi.transport.Acceptor;
import com.sun.corba.ee.spi.transport.ListenerThread;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.threadpool.Work;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.spi.trace.Transport;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;

@Transport
public class ListenerThreadImpl
    implements
        ListenerThread,
        Work
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private ORB orb;
    private Acceptor acceptor;
    private boolean keepRunning;
    private long enqueueTime;

    public ListenerThreadImpl(ORB orb, Acceptor acceptor)
    {
        this.orb = orb;
        this.acceptor = acceptor;
        keepRunning = true;
    }

    
    
    
    

    public Acceptor getAcceptor()
    {
        return acceptor;
    }

    @Transport
    public synchronized void close()
    {
        keepRunning = false;
        acceptor.close() ;
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
            display( "acceptor", acceptor ) ;
            try {
                display( "Before Accept cycle" ) ;
                acceptor.processSocket( acceptor.getAcceptedSocket() ) ;
                display( "After Accept cycle" ) ;
            } catch (Throwable t) {
                wrapper.exceptionInListenerThread( t ) ;
                display( "Exception in accept", t ) ;

                orb.getTransportManager().getSelector(0)
                    .unregisterForEvent(getAcceptor().getEventHandler());

                try {
                    if (isRunning()) {
                        getAcceptor().close();
                    }
                } catch (Exception exc) {
                    wrapper.ioExceptionOnClose( exc ) ;
                }
            }
        }
    }

    public void setEnqueueTime(long timeInMillis) 
    {
        enqueueTime = timeInMillis;
    }

    public long getEnqueueTime() 
    {
        return enqueueTime;
    }

    public String getName() { return "ListenerThread"; }

    
    
    
    
}


