


package com.sun.corba.ee.spi.transport;








public interface Connection
    extends
        com.sun.corba.ee.spi.legacy.connection.Connection
{
    
    public boolean shouldRegisterReadEvent();

    
    public boolean shouldRegisterServerReadEvent(); 

    
    public boolean read();

    public void close();

    
    

    public Acceptor getAcceptor();

    public ContactInfo getContactInfo();

    public EventHandler getEventHandler();

    
    public boolean isServer();

    
    public boolean isClosed();

    
    public boolean isBusy();

    
    public long getTimeStamp();

    
    public void setTimeStamp(long time);

    
    public void setState(String state);

    
    public void writeLock();

    
    public void writeUnlock();

    
    public void sendWithoutLock(CDROutputObject outputObject);

    
    public void registerWaiter(MessageMediator messageMediator);

    
    public CDRInputObject waitForResponse(MessageMediator messageMediator);

    
    public void unregisterWaiter(MessageMediator messageMediator);

    public void setConnectionCache(ConnectionCache connectionCache);

    public ConnectionCache getConnectionCache();
    public boolean hasSocketChannel();

    public void write(ByteBuffer byteBuffer)
        throws IOException;

    public int getNextRequestId();
    public ORB getBroker();
    public CodeSetComponentInfo.CodeSetContext getCodeSetContext();
    public void setCodeSetContext(CodeSetComponentInfo.CodeSetContext csc);

    
    public MessageMediator clientRequestMapGet(int requestId);

    public void clientReply_1_1_Put(MessageMediator x);
    public MessageMediator clientReply_1_1_Get();
    public void clientReply_1_1_Remove();

    public void serverRequest_1_1_Put(MessageMediator x);
    public MessageMediator serverRequest_1_1_Get();
    public void serverRequest_1_1_Remove();

    public boolean isPostInitialContexts();

    
    public void setPostInitialContexts();

    public void purgeCalls(SystemException systemException,
                           boolean die, boolean lockHeld);

    
    
    
    public static final int OPENING = 1;
    public static final int ESTABLISHED = 2;
    public static final int CLOSE_SENT = 3;
    public static final int CLOSE_RECVD = 4;
    public static final int ABORT = 5;

    
    
    
    
    
    
    
    
    

    
    
    

    void setCodeBaseIOR(IOR ior);

    IOR getCodeBaseIOR();

    
    
    CodeBase getCodeBase();

    

    public void sendCloseConnection(GIOPVersion giopVersion)
        throws IOException;

    public void sendMessageError(GIOPVersion giopVersion)
        throws IOException;

    public void sendCancelRequest(GIOPVersion giopVersion, int requestId)
        throws
            IOException;

    
    public void sendCancelRequestWithLock(GIOPVersion giopVersion,
                                          int requestId)
        throws 
            IOException;

    public ResponseWaitingRoom getResponseWaitingRoom();

    public void serverRequestMapPut(int requestId,
                                    MessageMediator messageMediator);
    public MessageMediator serverRequestMapGet(int requestId);
    public void serverRequestMapRemove(int requestId);

    public Queue<MessageMediator> getFragmentList(RequestId corbaRequestId);
    public void removeFragmentList(RequestId corbaRequestId);

    
    public SocketChannel getSocketChannel();

    
    public void serverRequestProcessingBegins();
    public void serverRequestProcessingEnds();

    
    public void closeConnectionResources() ;
}



