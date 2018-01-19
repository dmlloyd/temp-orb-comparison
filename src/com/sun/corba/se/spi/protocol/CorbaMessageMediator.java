

package xxxx;







public interface CorbaMessageMediator
    extends
        MessageMediator,
        ResponseHandler
{
    public void setReplyHeader(LocateReplyOrReplyMessage header);
    public LocateReplyMessage getLocateReplyHeader();
    public ReplyMessage getReplyHeader();
    public void setReplyExceptionDetailMessage(String message);
    public RequestMessage getRequestHeader();
    public GIOPVersion getGIOPVersion();
    public byte getEncodingVersion();
    public int getRequestId();
    public Integer getRequestIdInteger();
    public boolean isOneWay();
    public short getAddrDisposition();
    public String getOperationName();
    public ServiceContexts getRequestServiceContexts();
    public ServiceContexts getReplyServiceContexts();
    public Message getDispatchHeader();
    public void setDispatchHeader(Message msg);
    public ByteBuffer getDispatchBuffer();
    public void setDispatchBuffer(ByteBuffer byteBuffer);
    public int getThreadPoolToUse();
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

    
    
    
    

    public ObjectKey getObjectKey();
    public void setProtocolHandler(CorbaProtocolHandler protocolHandler);
    public CorbaProtocolHandler getProtocolHandler();

    
    
    
    

    public org.omg.CORBA.portable.OutputStream createReply();
    public org.omg.CORBA.portable.OutputStream createExceptionReply();

    
    
    
    

    public boolean executeReturnServantInResponseConstructor();

    public void setExecuteReturnServantInResponseConstructor(boolean b);

    public boolean executeRemoveThreadInfoInResponseConstructor();

    public void setExecuteRemoveThreadInfoInResponseConstructor(boolean b);

    public boolean executePIInResponseConstructor();

    public void setExecutePIInResponseConstructor( boolean b );
}


