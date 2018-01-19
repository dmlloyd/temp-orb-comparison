

package com.sun.corba.se.pept.transport;



public interface ContactInfo
{
    
    public Broker getBroker();

    
    public ContactInfoList getContactInfoList();

    
    public ClientRequestDispatcher getClientRequestDispatcher();

    
    public boolean isConnectionBased();

    
    public boolean shouldCacheConnection();

    
    public String getConnectionCacheType();

    
    public void setConnectionCache(OutboundConnectionCache connectionCache);

    
    public OutboundConnectionCache getConnectionCache();

    
    public Connection createConnection();

    
    public MessageMediator createMessageMediator(Broker broker,
                                                 ContactInfo contactInfo,
                                                 Connection connection,
                                                 String methodName,
                                                 boolean isOneWay);

    
    public MessageMediator createMessageMediator(Broker broker,
                                                 Connection connection);

    
    public MessageMediator finishCreatingMessageMediator(Broker broker,
                                                         Connection connection,
                                                         MessageMediator messageMediator);

    
    public InputObject createInputObject(Broker broker,
                                         MessageMediator messageMediator);

    
    public OutputObject createOutputObject(MessageMediator messageMediator);

    
    public int hashCode();
}


