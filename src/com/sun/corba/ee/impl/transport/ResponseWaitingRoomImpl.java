


package com.sun.corba.ee.impl.transport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;


import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.transport.ResponseWaitingRoom;

import com.sun.corba.ee.impl.encoding.CDRInputObject;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.spi.misc.ORBConstants;
import com.sun.corba.ee.impl.misc.ORBUtility;
import com.sun.corba.ee.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.ee.spi.trace.Transport;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;


@Transport
public class ResponseWaitingRoomImpl
    implements
        ResponseWaitingRoom
{
    final private static ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    final static class OutCallDesc
    {
        MessageMediator messageMediator;
        SystemException exception;
        CDRInputObject inputObject;
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
    }

    
    final private Map<Integer, OutCallDesc> out_calls;
    final private ORB orb;
    final private Connection connection;


    public ResponseWaitingRoomImpl(ORB orb, Connection connection)
    {
        this.orb = orb;
        this.connection = connection;
        this.out_calls = 
               Collections.synchronizedMap(new HashMap<Integer, OutCallDesc>());
    }

    @Transport
    public void registerWaiter(MessageMediator messageMediator)
    {
        display( "messageMediator request ID",
            messageMediator.getRequestId() ) ;
        display( "messageMediator operation name",
            messageMediator.getOperationName() ) ;

        Integer requestId = messageMediator.getRequestId();
        
        OutCallDesc call = new OutCallDesc();
        call.messageMediator = messageMediator;
        OutCallDesc exists = out_calls.put(requestId, call);
        if (exists != null) {
            wrapper.duplicateRequestIdsInResponseWaitingRoom(
                       ORBUtility.operationNameAndRequestId(
                           (MessageMediator)exists.messageMediator),
                       ORBUtility.operationNameAndRequestId(messageMediator));
        }
    }

    @Transport
    public void unregisterWaiter(MessageMediator mediator)
    {
        MessageMediator messageMediator = mediator;
        display( "messageMediator request ID",
            messageMediator.getRequestId() ) ;
        display( "messageMediator operation name",
            messageMediator.getOperationName() ) ;

        Integer requestId = messageMediator.getRequestId();

        out_calls.remove(requestId);
    }

    @Transport
    public CDRInputObject waitForResponse(MessageMediator messageMediator) {
        CDRInputObject returnStream = null;
        
        display( "messageMediator request ID",
            messageMediator.getRequestId() ) ;
        display( "messageMediator operation name",
            messageMediator.getOperationName() ) ;
        
        Integer requestId = messageMediator.getRequestId();
        
        if (messageMediator.isOneWay()) {
            
            
            display( "Oneway request: not waiting") ;
            return null;
        }
        
        OutCallDesc call = out_calls.get(requestId);
        if (call == null) {
            throw wrapper.nullOutCall() ;
        }

        
        
        long waitForResponseTimeout =
                orb.getORBData().getWaitForResponseTimeout() * 1000 * 1000;
        
        try {
            call.lock.lock();
            while (call.inputObject == null && call.exception == null) {
                
                
                
                try {
                    display( "Waiting for response..." ) ;
                    
                    waitForResponseTimeout =
                            call.condition.awaitNanos(waitForResponseTimeout);
                    if (call.inputObject == null && call.exception == null) {
                        if (waitForResponseTimeout > 0) {
                            
                            
                            display( "Spurious wakeup, continuing to wait for ",
                                waitForResponseTimeout/1000000 );
                        } else {
                            
                            call.exception =
                                wrapper.communicationsTimeoutWaitingForResponse(
                                orb.getORBData().getWaitForResponseTimeout());
                            
                            
                            
                            
                            
                            ORBUtility.pushEncVersionToThreadLocalState(
                                    ORBConstants.JAVA_ENC_VERSION);
                        }
                    }
                } catch (InterruptedException ie) {};
            }
            if (call.exception != null) {
                display( "Exception from call", call.exception ) ;
                throw call.exception;
            }
            
            returnStream = call.inputObject;
        } finally {
            call.lock.unlock();
        }
        
        
        
        
        if (returnStream != null) {
            
            
            
            
            
            ((CDRInputObject)returnStream).unmarshalHeader();
        }
        
        return returnStream;
    }

    @InfoMethod
    private void display( String msg ) { }

    @InfoMethod
    private void display( String msg, int value ) { }

    @InfoMethod
    private void display( String msg, Object value ) { }

    @Transport
    public void responseReceived(CDRInputObject is)
    {
        CDRInputObject inputObject = (CDRInputObject) is;
        LocateReplyOrReplyMessage header = (LocateReplyOrReplyMessage)
            inputObject.getMessageHeader();
        display( "requestId", header.getRequestId()) ;
        display( "header", header ) ;

        OutCallDesc call = out_calls.get(header.getRequestId());

        
        
        
        
        
        
        
        
        if (call == null) {
            display( "No waiter" ) ;
            return;
        }

        
        
        
        
        
        
        try {
            call.lock.lock();
            MessageMediator messageMediator =
                           (MessageMediator)call.messageMediator;

            display( "Notifying waiters") ;
            display( "messageMediator request ID",
                messageMediator.getRequestId() ) ;
            display( "messageMediator operation name",
                messageMediator.getOperationName() ) ;

            messageMediator.setReplyHeader(header);
            messageMediator.setInputObject(is);
            inputObject.setMessageMediator(messageMediator);
            call.inputObject = is;
            call.condition.signal();
        } finally {
            call.lock.unlock();
        }
    }

    public int numberRegistered()
    {
        return out_calls.size();
    }

    
    
    
    

    @Transport
    public void signalExceptionToAllWaiters(SystemException systemException) {
        OutCallDesc call;
        synchronized (out_calls) {
            Iterator<OutCallDesc> itr = out_calls.values().iterator();
            while (itr.hasNext()) {
                call = itr.next();
                try {
                    call.lock.lock();
                    ((MessageMediator)call.messageMediator).cancelRequest();
                    call.inputObject = null;
                    call.exception = systemException;
                    call.condition.signal();
                } finally {
                    call.lock.unlock();
                }
            }
        }
    }

    public MessageMediator getMessageMediator(int requestId)
    {
        OutCallDesc call = out_calls.get(requestId);
        if (call == null) {
            
            
            return null;
        }
        return call.messageMediator;
    }
}


