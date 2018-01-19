


package com.sun.corba.ee.spi.transport;



public interface ResponseWaitingRoom{
    public void registerWaiter(MessageMediator messageMediator);

    
    public CDRInputObject waitForResponse(MessageMediator messageMediator);

    public void responseReceived(CDRInputObject inputObject);

    public void unregisterWaiter(MessageMediator messageMediator);

    public int numberRegistered();

    public void signalExceptionToAllWaiters(SystemException systemException);

    public MessageMediator getMessageMediator(int requestId);
}


