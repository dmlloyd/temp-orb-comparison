

package com.sun.corba.se.pept.broker;



public interface Broker
{
    public ClientInvocationInfo createOrIncrementInvocationInfo();
    public ClientInvocationInfo getInvocationInfo();
    public void releaseOrDecrementInvocationInfo();

    public abstract TransportManager getTransportManager();
}


