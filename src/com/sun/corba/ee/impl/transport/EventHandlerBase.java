

package com.sun.corba.ee.impl.transport;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import java.nio.channels.SelectionKey;

import com.sun.corba.ee.spi.transport.EventHandler;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.threadpool.NoSuchThreadPoolException;
import com.sun.corba.ee.spi.threadpool.NoSuchWorkQueueException;
import com.sun.corba.ee.spi.threadpool.Work;

import com.sun.corba.ee.spi.trace.Transport;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;

@Transport
public abstract class EventHandlerBase
    implements
        EventHandler
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    protected ORB orb;
    protected Work work;
    protected boolean useWorkerThreadForEvent;
    protected boolean useSelectThreadToWait;
    protected SelectionKey selectionKey;

    
    
    
    

    public void setUseSelectThreadToWait(boolean x)
    {
        useSelectThreadToWait = x;
    }

    public boolean shouldUseSelectThreadToWait()
    {
        return useSelectThreadToWait;
    }

    public void setSelectionKey(SelectionKey selectionKey)
    {
        this.selectionKey = selectionKey;
    }

    public SelectionKey getSelectionKey()
    {
        return selectionKey;
    }

    @InfoMethod
    private void display( String msg ) { }

    @InfoMethod
    private void display( String msg, Object value ) { }

    
    @Transport
    public void handleEvent()
    {
        getSelectionKey().interestOps(getSelectionKey().interestOps() &
                                      (~ getInterestOps()));
        if (shouldUseWorkerThreadForEvent()) {
            Throwable throwable = null;
            try {
                display( "add work to pool 0") ;
                orb.getThreadPoolManager().getThreadPool(0)
                    .getWorkQueue(0).addWork(getWork());
            } catch (NoSuchThreadPoolException e) {
                throwable = e;
            } catch (NoSuchWorkQueueException e) {
                throwable = e;
            }
            
            if (throwable != null) {
                display( "unexpected exception", throwable ) ;
                throw wrapper.noSuchThreadpoolOrQueue(throwable, 0);
            }
        } else {
            display( "doWork" ) ;
            getWork().doWork();
        }
    }

    public boolean shouldUseWorkerThreadForEvent()
    {
        return useWorkerThreadForEvent;
    }

    public void setUseWorkerThreadForEvent(boolean x)
    {
        useWorkerThreadForEvent = x;
    }

    public void setWork(Work work)
    {
        this.work = work;
    }

    public Work getWork()
    {
        return work;
    }
}


