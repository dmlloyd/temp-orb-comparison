


package com.sun.corba.ee.spi.transport;

import com.sun.corba.ee.impl.encoding.CDRInputObject;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import org.omg.CORBA.SystemException;


public interface ResponseWaitingRoom{
    public void registerWaiter(MessageMediator messageMediator);

    
    public CDRInputObject waitForResponse(MessageMediator messageMediator);

    public void responseReceived(CDRInputObject inputObject);

    public void unregisterWaiter(MessageMediator messageMediator);

    public int numberRegistered();

    public void signalExceptionToAllWaiters(SystemException systemException);

    public MessageMediator getMessageMediator(int requestId);
}


