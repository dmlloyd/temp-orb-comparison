


package xxxx;






public abstract interface MessageMediator
    extends
        ResponseHandler
{
    RequestId getRequestIdFromRawBytes();
    public void setReplyHeader(LocateReplyOrReplyMessage header);
    public LocateReplyMessage getLocateReplyHeader();
    public ReplyMessage getReplyHeader();
    public void setReplyExceptionDetailMessage(String message);
    public RequestMessage getRequestHeader();
    public GIOPVersion getGIOPVersion();
    public byte getEncodingVersion();
    public int getRequestId();
    public boolean isOneWay();
    public String getOperationName();
    public ServiceContexts getRequestServiceContexts();
    public void setRequestServiceContexts(ServiceContexts sc);
    public ServiceContexts getReplyServiceContexts();
    public Message getDispatchHeader();
    public int getThreadPoolToUse();
    public boolean dispatch();
    public byte getStreamFormatVersion(); 
    public byte getStreamFormatVersionForReply();

    
    

    public void sendCancelRequestIfFinalFragmentNotSent();

    public void setDIIInfo(org.omg.CORBA.Request request);
    public boolean isDIIRequest();
    public Exception unmarshalDIIUserException(String repoId,
                                               InputStream inputStream);
    public void setDIIException(Exception exception);
    public void handleDIIReply(InputStream inputStream);

    public boolean isSystemExceptionReply();
    public boolean isUserExceptionReply();
    public boolean isLocationForwardReply();
    public boolean isDifferentAddrDispositionRequestedReply();
    public short getAddrDispositionReply();
    public IOR getForwardedIOR();
    public SystemException getSystemExceptionReply();
    public void cancelRequest();

    
    
    
    

    public ObjectKeyCacheEntry getObjectKeyCacheEntry();
    public ProtocolHandler getProtocolHandler();

    
    
    
    

    public org.omg.CORBA.portable.OutputStream createReply();
    public org.omg.CORBA.portable.OutputStream createExceptionReply();

    
    
    
    

    public boolean executeReturnServantInResponseConstructor();

    public void setExecuteReturnServantInResponseConstructor(boolean b);

    public boolean executeRemoveThreadInfoInResponseConstructor();

    public void setExecuteRemoveThreadInfoInResponseConstructor(boolean b);

    public boolean executePIInResponseConstructor();

    public void setExecutePIInResponseConstructor( boolean b );

    public ORB getBroker();

    public ContactInfo getContactInfo();

    public Connection getConnection();

    
    public void initializeMessage();

    
    public void finishSendingRequest();

    public CDRInputObject waitForResponse();

    public void setOutputObject(CDROutputObject outputObject);

    public CDROutputObject getOutputObject();

    public void setInputObject(CDRInputObject inputObject);

    public CDRInputObject getInputObject();
}



