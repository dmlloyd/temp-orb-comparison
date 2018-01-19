


package com.sun.corba.ee.spi.protocol;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.UnknownException;

import com.sun.corba.ee.spi.ior.IOR ;

import com.sun.corba.ee.spi.servicecontext.ServiceContexts;
import com.sun.corba.ee.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.ee.impl.protocol.giopmsgheaders.RequestMessage;


public abstract interface ProtocolHandler
{
    public void handleRequest(RequestMessage header, 
                              MessageMediator messageMediator);

    public void handleRequest(LocateRequestMessage header, 
                              MessageMediator messageMediator);

    public MessageMediator createResponse(
        MessageMediator messageMediator,
        ServiceContexts svc);
    public MessageMediator createUserExceptionResponse(
        MessageMediator messageMediator,
        ServiceContexts svc);
    public MessageMediator createUnknownExceptionResponse(
        MessageMediator messageMediator,
        UnknownException ex);
    public MessageMediator createSystemExceptionResponse(
        MessageMediator messageMediator,
        SystemException ex,
        ServiceContexts svc);
    public MessageMediator createLocationForward(
        MessageMediator messageMediator,
        IOR ior, 
        ServiceContexts svc);

    public void handleThrowableDuringServerDispatch( 
        MessageMediator request,
        Throwable exception,
        CompletionStatus completionStatus);

    public boolean handleRequest(MessageMediator messageMediator);

}


