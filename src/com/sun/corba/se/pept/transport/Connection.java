

package xxxx;





public interface Connection
{
    
    public boolean shouldRegisterReadEvent();

    
    public boolean shouldRegisterServerReadEvent(); 

    
    public boolean read();

    
    public void close();

    
    

    
    public Acceptor getAcceptor();

    
    public ContactInfo getContactInfo();

    
    public EventHandler getEventHandler();

    
    public boolean isServer();

    
    public boolean isBusy();

    
    public long getTimeStamp();

    
    public void setTimeStamp(long time);

    
    public void setState(String state);

    
    public void writeLock();

    
    public void writeUnlock();

    
    public void sendWithoutLock(OutputObject outputObject);

    
    public void registerWaiter(MessageMediator messageMediator);

    
    public InputObject waitForResponse(MessageMediator messageMediator);

    
    public void unregisterWaiter(MessageMediator messageMediator);

    public void setConnectionCache(ConnectionCache connectionCache);

    public ConnectionCache getConnectionCache();
}


