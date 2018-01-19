

package com.sun.corba.se.pept.protocol;




public interface MessageMediator
{
    
    public Broker getBroker();

    
    public ContactInfo getContactInfo();

    
    public Connection getConnection();

    
    public void initializeMessage();

    
    public void finishSendingRequest();

    
    @Deprecated
    public InputObject waitForResponse();

    
    public void setOutputObject(OutputObject outputObject);

    
    public OutputObject getOutputObject();

    
    public void setInputObject(InputObject inputObject);

    
    public InputObject getInputObject();
}


