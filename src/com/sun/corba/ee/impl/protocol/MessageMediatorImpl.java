


package com.sun.corba.ee.impl.protocol;






@Subcontract
@Transport
public class MessageMediatorImpl
    implements 
        MessageMediator,
        ProtocolHandler,
        MessageHandler,
        Work
{
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;
    protected static final InterceptorsSystemException interceptorWrapper =
        InterceptorsSystemException.self ;

    protected ORB orb;
    protected ContactInfo contactInfo;
    protected Connection connection;
    protected short addrDisposition;
    protected CDROutputObject outputObject;
    protected CDRInputObject inputObject;
    protected Message messageHeader;
    protected RequestMessage requestHeader;
    protected LocateReplyOrReplyMessage replyHeader;
    protected String replyExceptionDetailMessage;
    protected IOR replyIOR;
    protected Message dispatchHeader;
    protected ByteBuffer dispatchByteBuffer;
    protected byte streamFormatVersion;
    protected boolean streamFormatVersionSet = false;

    protected org.omg.CORBA.Request diiRequest;

    protected boolean cancelRequestAlreadySent = false;

    protected ProtocolHandler protocolHandler;
    protected boolean _executeReturnServantInResponseConstructor = false;
    protected boolean _executeRemoveThreadInfoInResponseConstructor = false;
    protected boolean _executePIInResponseConstructor = false;

    
    
    private static byte localMaxVersion =  ORBUtility.getMaxStreamFormatVersion();

    
    private long enqueueTime;

    
    
    
    public MessageMediatorImpl(ORB orb,
                                    ContactInfo contactInfo,
                                    Connection connection,
                                    GIOPVersion giopVersion,
                                    IOR ior,
                                    int requestId,
                                    short addrDisposition,
                                    String operationName,
                                    boolean isOneWay)
    {
        this( orb, connection ) ;
            
        this.contactInfo = contactInfo;
        this.addrDisposition = addrDisposition;

        streamFormatVersion = getStreamFormatVersionForThisRequest(
            this.contactInfo.getEffectiveTargetIOR(), giopVersion);

        

        streamFormatVersionSet = true;

        byte encodingVersion =
            ORBUtility.chooseEncodingVersion(orb, ior, giopVersion);
        ORBUtility.pushEncVersionToThreadLocalState(encodingVersion);
        requestHeader = MessageBase.createRequest(this.orb, giopVersion,
            encodingVersion, requestId, !isOneWay,
            this.contactInfo.getEffectiveTargetIOR(), this.addrDisposition,
            operationName,
            ServiceContextDefaults.makeServiceContexts(orb), null);
    }

    
    
    
    private MessageMediatorImpl(ORB orb,
                                    Connection connection)
    {
        this.orb = orb;
        this.connection = connection;
    }

    
    
    

    
    
    
    public MessageMediatorImpl(ORB orb,
                                    Connection connection,
                                    Message dispatchHeader,
                                    ByteBuffer byteBuffer)
    {
        this( orb, connection ) ;
        this.dispatchHeader = dispatchHeader;
        this.dispatchByteBuffer = byteBuffer;
    }

    public RequestId getRequestIdFromRawBytes() {
        return MessageBase.getRequestIdFromMessageBytes(getDispatchHeader(), dispatchByteBuffer);
    }

    
    
    
    

    public ORB getBroker() {
        return orb;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public Connection getConnection() {
        return connection;
    }

    public void initializeMessage() {
        getRequestHeader().write(outputObject);
    }

    public void finishSendingRequest() {
        
        outputObject.finishSendingMessage();
    }

    public CDRInputObject waitForResponse() {
        if (getRequestHeader().isResponseExpected()) {
            return connection.waitForResponse(this);
        }
        return null;
    }

    public void setOutputObject(CDROutputObject outputObject) {
        this.outputObject = outputObject;
    }

    public CDROutputObject getOutputObject() {
        return outputObject;
    }

    public void setInputObject(CDRInputObject inputObject) {
        this.inputObject = inputObject;
    }

    public CDRInputObject getInputObject() {
        return inputObject;
    }

    
    

    public void setReplyHeader(LocateReplyOrReplyMessage header) {
        this.replyHeader = header;
        this.replyIOR = header.getIOR(); 
    }

    public LocateReplyMessage getLocateReplyHeader() {
        return (LocateReplyMessage) replyHeader;
    }
    
    public ReplyMessage getReplyHeader() {
        return (ReplyMessage) replyHeader;
    }

    public void setReplyExceptionDetailMessage(String message) {
        replyExceptionDetailMessage = message;
    }
    
    public RequestMessage getRequestHeader() {
        return requestHeader;
    }
    
    public GIOPVersion getGIOPVersion() {
        if (messageHeader != null) {
            return messageHeader.getGIOPVersion() ;
        }

        if (getRequestHeader() == null) {
            return GIOPVersion.V1_2 ;
        }

        return getRequestHeader().getGIOPVersion();
    }

    public byte getEncodingVersion() {
        if (messageHeader != null) {
            return messageHeader.getEncodingVersion() ; 
        }

        if (getRequestHeader() == null) {
            return 0 ;
        }

        return getRequestHeader().getEncodingVersion();
    }

    public int getRequestId() {
        if (getRequestHeader() == null) {
            return -1 ;
        }

        return getRequestHeader().getRequestId();
    }

    public boolean isOneWay() {
        if (getRequestHeader() == null) {
            return false ;
        }

        return ! getRequestHeader().isResponseExpected();
    }

    public String getOperationName() {
        if (getRequestHeader() == null) {
            return "UNKNOWN" ;
        }

        return getRequestHeader().getOperation();
    }

    public ServiceContexts getRequestServiceContexts() {
        if (getRequestHeader() == null) {
            return null ;
        }

        return getRequestHeader().getServiceContexts();
    }

    public void setRequestServiceContexts(ServiceContexts sc) {
        getRequestHeader().setServiceContexts(sc);
    }

    public ServiceContexts getReplyServiceContexts() {
        return getReplyHeader().getServiceContexts();
    }

    @Subcontract
    public void sendCancelRequestIfFinalFragmentNotSent() {
        if ((!sentFullMessage()) && sentFragment() && 
            (!cancelRequestAlreadySent) && !connection.isClosed()) {

            try {
                connection.sendCancelRequestWithLock(getGIOPVersion(),
                                                     getRequestId());
                
                
                
                cancelRequestAlreadySent = true;
            } catch (SystemException se) {
                if (se.minor == ORBUtilSystemException.CONNECTION_REBIND) {
                    connection.purgeCalls(se, true, false);
                } else {
                    throw se;
                }
            } catch (IOException e) {
                throw interceptorWrapper.ioexceptionDuringCancelRequest( e );
            }
        }
    }

    @Subcontract
    public boolean sentFullMessage() {
        if (outputObject == null) {
            return false;
        } else {
            return outputObject.getBufferManager().sentFullMessage();
        }
    }

    @Subcontract
    public boolean sentFragment() {
        if (outputObject != null) {
            BufferManagerWrite buffMan = 
                outputObject.getBufferManager() ;

            if (buffMan != null) {
                return outputObject.getBufferManager().sentFragment();
            }
        }

        return false ;
    }

    public void setDIIInfo(org.omg.CORBA.Request diiRequest) {
        this.diiRequest = diiRequest;
    }

    public boolean isDIIRequest() {
        return diiRequest != null;
    }

    @Subcontract
    public Exception unmarshalDIIUserException(String repoId, InputStream is) {
        if (! isDIIRequest()) {
            return null;
        }

        ExceptionList _exceptions = diiRequest.exceptions();

        try {
            
            for (int i=0; i<_exceptions.count() ; i++) {
                TypeCode tc = _exceptions.item(i);
                if ( tc.id().equals(repoId) ) {
                    
                    
                    
                    
                    Any eany = orb.create_any();
                    eany.read_value(is, tc);

                    return new UnknownUserException(eany);
                }
            }
        } catch (Exception b) {
            throw wrapper.unexpectedDiiException(b);
        }

        
        return wrapper.unknownCorbaExc() ;
    }

    public void setDIIException(Exception exception) {
        diiRequest.env().exception(exception);
    }

    public void handleDIIReply(InputStream inputStream) {
        if (! isDIIRequest()) {
            return;
        }
        ((RequestImpl)diiRequest).unmarshalReply(inputStream);
    }

    public Message getDispatchHeader() {
        return dispatchHeader;
    }

    public int getThreadPoolToUse() {
        int poolToUse = 0;
        Message msg = dispatchHeader;
        
        
        if (msg != null) {
            poolToUse = msg.getThreadPoolToUse();
        }
        return poolToUse;
    }

    @InfoMethod
    private void reportException( String msg, Throwable thr ) { }

    @InfoMethod
    private void reportConnection( Connection conn ) { }

    
    @Transport
    public boolean dispatch() {
        reportConnection( connection ) ;

        try {
            boolean result = getProtocolHandler().handleRequest(this);
            return result;
        } catch (ThreadDeath td) {
            try {
                connection.purgeCalls(wrapper.connectionAbort(td), false, false);
            } catch (Throwable t) {
                reportException( "ThreadDeatch exception in dispatch", t );
            }
            throw td;
        } catch (Throwable ex) {
            reportException( "Exception in dispatch", ex ) ;

            try {
                if (ex instanceof INTERNAL) {
                    connection.sendMessageError(GIOPVersion.DEFAULT_VERSION);
                }
            } catch (IOException e) {
                reportException("Exception in sendMessageError", ex);
            }
            connection.purgeCalls(wrapper.connectionAbort(ex), false, false);
        }
        return true;
    }

    public byte getStreamFormatVersion()
    {
        
        
        
        
        
        if (streamFormatVersionSet) {
            return streamFormatVersion;
        }
        return getStreamFormatVersionForReply();
    }

    
    @Transport
    public byte getStreamFormatVersionForReply() {

        
        ServiceContexts svc = getRequestServiceContexts();

        MaxStreamFormatVersionServiceContext msfvsc
            = (MaxStreamFormatVersionServiceContext)svc.get(
                MaxStreamFormatVersionServiceContext.SERVICE_CONTEXT_ID);
            
        if (msfvsc != null) {
            byte remoteMaxVersion = msfvsc.getMaximumStreamFormatVersion();

            return (byte)Math.min(localMaxVersion, remoteMaxVersion);
        } else {
            
            
            if (getGIOPVersion().lessThan(GIOPVersion.V1_3)) {
                return ORBConstants.STREAM_FORMAT_VERSION_1;
            } else {
                return ORBConstants.STREAM_FORMAT_VERSION_2;
            }
        }
    }

    public boolean isSystemExceptionReply() {
        return replyHeader.getReplyStatus() == ReplyMessage.SYSTEM_EXCEPTION;
    }

    public boolean isUserExceptionReply() {
        return replyHeader.getReplyStatus() == ReplyMessage.USER_EXCEPTION;
    }

    public boolean isLocationForwardReply() {
        return ( (replyHeader.getReplyStatus() == ReplyMessage.LOCATION_FORWARD) ||
                 (replyHeader.getReplyStatus() == ReplyMessage.LOCATION_FORWARD_PERM) );
        
    }
    
    public boolean isDifferentAddrDispositionRequestedReply() {
        return replyHeader.getReplyStatus() == ReplyMessage.NEEDS_ADDRESSING_MODE;
    }
    
    public short getAddrDispositionReply() {
        return replyHeader.getAddrDisposition();
    }
    
    public IOR getForwardedIOR() {
        return replyHeader.getIOR();
    }

    public SystemException getSystemExceptionReply() {
        return replyHeader.getSystemException(replyExceptionDetailMessage);
    }

    
    
    
    

    public ObjectKeyCacheEntry getObjectKeyCacheEntry() {
        return getRequestHeader().getObjectKeyCacheEntry() ;
    }

    public ProtocolHandler getProtocolHandler() {
        
        return this;
    }

    
    
    
    

    public org.omg.CORBA.portable.OutputStream createReply() {
        
        
        getProtocolHandler().createResponse(this, null);
        return getOutputObject();
    }

    public org.omg.CORBA.portable.OutputStream createExceptionReply() {
        
        
        getProtocolHandler().createUserExceptionResponse(this, null);
        return getOutputObject();
    }

    public boolean executeReturnServantInResponseConstructor() {
        return _executeReturnServantInResponseConstructor;
    }

    public void setExecuteReturnServantInResponseConstructor(boolean b) {
        _executeReturnServantInResponseConstructor = b;
    }

    public boolean executeRemoveThreadInfoInResponseConstructor() {
        return _executeRemoveThreadInfoInResponseConstructor;
    }

    public void setExecuteRemoveThreadInfoInResponseConstructor(boolean b) {
        _executeRemoveThreadInfoInResponseConstructor = b;
    }

    public boolean executePIInResponseConstructor() {
        return _executePIInResponseConstructor;
    }

    public void setExecutePIInResponseConstructor( boolean b ) {
        _executePIInResponseConstructor = b;
    }

    @Transport
    private byte getStreamFormatVersionForThisRequest(IOR ior, GIOPVersion giopVersion) {

        IOR effectiveTargetIOR = 
            this.contactInfo.getEffectiveTargetIOR();
        IIOPProfileTemplate temp =
            (IIOPProfileTemplate)effectiveTargetIOR.getProfile().getTaggedProfileTemplate();
        Iterator iter = temp.iteratorById(TAG_RMI_CUSTOM_MAX_STREAM_FORMAT.value);
        if (!iter.hasNext()) {
            
            
            if (giopVersion.lessThan(GIOPVersion.V1_3)) {
                return ORBConstants.STREAM_FORMAT_VERSION_1;
            } else {
                return ORBConstants.STREAM_FORMAT_VERSION_2;
            }
        }

        byte remoteMaxVersion
            = ((MaxStreamFormatVersionComponent)iter.next()).getMaxStreamFormatVersion();

        return (byte)Math.min(localMaxVersion, remoteMaxVersion);
    }

    
    
    

    
    
    


    protected boolean isThreadDone = false;

    @Transport
    public boolean handleRequest(MessageMediator messageMediator) {
        try {
            byte encodingVersion = dispatchHeader.getEncodingVersion();
            ORBUtility.pushEncVersionToThreadLocalState(encodingVersion);
            dispatchHeader.callback(this);
        } catch (IOException e) {
            
        } finally {
            ORBUtility.popEncVersionFromThreadLocalState();
        }
        return isThreadDone;
    }

    @InfoMethod
    private void messageInfo( Message msg, RequestId rid ) { }

    @InfoMethod
    private void connectionInfo( Connection conn ) { }


    
    
    
    

    @Transport
    private void resumeOptimizedReadProcessing(Message message) {
        messageInfo( message, message.getCorbaRequestId() ) ;
        connectionInfo(connection);

        if (message.moreFragmentsToFollow()) {
            generalMessage("getting next fragment");

            MessageMediator messageMediator = null;
            RequestId requestId = message.getCorbaRequestId();
            Queue<MessageMediator> queue =
                connection.getFragmentList(requestId);

            
            
            
            
            
            
            
            
            
            synchronized (queue) {
                while (messageMediator == null) {
                    if (queue.size() > 0) {
                        messageMediator = queue.poll();
                    } else {
                        try {
                            queue.wait();
                        } catch (InterruptedException ex) {
                            wrapper.resumeOptimizedReadThreadInterrupted(ex);
                        }
                    }
                }
            }

            
            
            
            
            
            
            
            
            addMessageMediatorToWorkQueue(messageMediator);
        } else {
            if (message.getType() == Message.GIOPFragment || 
                message.getType() == Message.GIOPCancelRequest) {
                
                
                RequestId requestId = message.getCorbaRequestId();
                generalMessage(
                    "done processing fragments (removing fragment list)" );
                connection.removeFragmentList(requestId);
            }
        }
    }

    @InfoMethod
    private void poolToUseInfo( int id ) { }

    @Transport
    private void addMessageMediatorToWorkQueue(final MessageMediator messageMediator) {
        
        Throwable throwable = null;
        int poolToUse = -1 ;
        try {
            poolToUse = messageMediator.getThreadPoolToUse();
            poolToUseInfo( poolToUse ) ;
            orb.getThreadPoolManager().getThreadPool(poolToUse).getWorkQueue(0).
                             addWork((MessageMediatorImpl)messageMediator);
        } catch (NoSuchThreadPoolException e) {
            throwable = e;
        } catch (NoSuchWorkQueueException e) {
            throwable = e;
        }

        
        if (throwable != null) {
            reportException("exception from thread pool", throwable);
            throw wrapper.noSuchThreadpoolOrQueue(throwable, poolToUse );
        }
    }

    @Transport
    private void setWorkThenPoolOrResumeOptimizedRead(Message header) {
        if (getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
            resumeOptimizedReadProcessing(header);
        } else {
            
            
        
            isThreadDone = true;

            
            orb.getTransportManager().getSelector(0)
                .unregisterForEvent(getConnection().getEventHandler());
            
            orb.getTransportManager().getSelector(0)
                .registerForEvent(getConnection().getEventHandler());
        }
    }

    @Transport
    private void setWorkThenReadOrResumeOptimizedRead(Message header) {
        if (getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
            resumeOptimizedReadProcessing(header);
        } else {
            
            
            isThreadDone = false;
        }
    }

    private void setInputObject() {
        inputObject = new CDRInputObject(orb, getConnection(), dispatchByteBuffer, dispatchHeader);
        inputObject.setMessageMediator(this);
    }

    private void signalResponseReceived() {
        
        
        
        connection.getResponseWaitingRoom()
            .responseReceived(inputObject);
    }

    
    @Transport
    public void handleInput(Message header) throws IOException {
        messageHeader = header;
        setWorkThenReadOrResumeOptimizedRead(header);

        switch(header.getType()) {
            case Message.GIOPCloseConnection:
                generalMessage( "close connection" ) ;
                connection.purgeCalls(wrapper.connectionRebind(), true, false);
                break;
            case Message.GIOPMessageError:
                generalMessage( "message error" ) ;
                connection.purgeCalls(wrapper.recvMsgError(), true, false);
                break;
            default:
                generalMessage( "default" ) ;
                throw wrapper.badGiopRequestType() ;
        }
        releaseByteBufferToPool();
    }

    @Transport
    public void handleInput(RequestMessage_1_0 header) throws IOException {
        generalMessage( "GIOP Request 1.0") ;
        try {
            try {
                messageHeader = requestHeader = (RequestMessage) header;
                setInputObject();
            } finally {
                setWorkThenPoolOrResumeOptimizedRead(header);
            }
            getProtocolHandler().handleRequest(header, this);
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }
    }
    
    @Transport
    public void handleInput(RequestMessage_1_1 header) throws IOException {
        generalMessage( "GIOP Request 1.1") ;
        try {
            try {
                messageHeader = requestHeader = (RequestMessage) header;
                setInputObject();
                connection.serverRequest_1_1_Put(this);
            } finally {
                setWorkThenPoolOrResumeOptimizedRead(header);
            }
            getProtocolHandler().handleRequest(header, this);
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }
    }

    @InfoMethod
    private void requestIdInfo( int id ) { }

    
    @Transport
    public void handleInput(RequestMessage_1_2 header) throws IOException {
        generalMessage("GIOP Request 1.2") ;
        try {
            try {
                messageHeader = requestHeader = header;

                unmarshalRequestID(header);
                requestIdInfo(header.getRequestId());
                setInputObject();

                
                
                
                
                
                
                connection.serverRequestMapPut(header.getRequestId(), this);
            } finally {
                
                
                
                
                
                setWorkThenPoolOrResumeOptimizedRead(header);
            }
            
            getProtocolHandler().handleRequest(header, this);
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        } finally {
            connection.serverRequestMapRemove(header.getRequestId());
        }
    }

    private void unmarshalRequestID(Message_1_2 message) {
        message.unmarshalRequestID(dispatchByteBuffer);
    }

    @Transport
    public void handleInput(ReplyMessage_1_0 header) throws IOException {
        generalMessage( "GIOP ReplyMessage 1.0") ;
        try {
            try {
                messageHeader = replyHeader = (ReplyMessage) header;
                setInputObject();

                
                inputObject.unmarshalHeader();

                signalResponseReceived();
            } finally{
                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }
    }
    
    @Transport
    public void handleInput(ReplyMessage_1_1 header) throws IOException {
        generalMessage( "GIOP ReplyMessage 1.1" ) ;
        try {
            messageHeader = replyHeader = (ReplyMessage) header;
            setInputObject();

            if (header.moreFragmentsToFollow()) {
                
                
                connection.clientReply_1_1_Put(this);
            
                
                
                
                
                setWorkThenPoolOrResumeOptimizedRead(header);

                
                
                inputObject.unmarshalHeader();

                signalResponseReceived();
            } else {
                
                
                
                

                
                
                inputObject.unmarshalHeader();

                signalResponseReceived();

                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }
    }

    @InfoMethod
    private void moreFragmentsInfo( boolean moreFragments ) { }

    @Transport
    public void handleInput(ReplyMessage_1_2 header) throws IOException {
        generalMessage( "GIOP ReplyMessage 1.2" ) ;
        try {
            try {
                messageHeader = replyHeader = (ReplyMessage) header;

                
                unmarshalRequestID(header);
                requestIdInfo( header.getRequestId() ) ;
                moreFragmentsInfo(header.moreFragmentsToFollow());
                setInputObject();
                signalResponseReceived();
            } finally {
                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }
    }

    @Transport
    public void handleInput(LocateRequestMessage_1_0 header) throws IOException {
        generalMessage( "GIOP LocateRequestMessage 1.0" ) ;
        try {
            try {
                messageHeader = header;
                setInputObject();
            } finally {
                setWorkThenPoolOrResumeOptimizedRead(header);
            }
            getProtocolHandler().handleRequest(header, this);
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }

    }

    @Transport
    public void handleInput(LocateRequestMessage_1_1 header) throws IOException {
        generalMessage( "GIOP LocateRequestMessage 1.1" ) ;
        try {
            try {
                messageHeader = header;
                setInputObject();
            } finally {
                setWorkThenPoolOrResumeOptimizedRead(header);
            }
            getProtocolHandler().handleRequest(header, this);
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }
    }

    @Transport
    public void handleInput(LocateRequestMessage_1_2 header) throws IOException {
        generalMessage( "GIOP LocateRequestMessage 1.2" ) ;
        try {
            try {
                messageHeader = header;

                unmarshalRequestID(header);
                setInputObject();

                requestIdInfo(header.getRequestId());
                moreFragmentsInfo(header.moreFragmentsToFollow());

                if (header.moreFragmentsToFollow()) {
                    connection.serverRequestMapPut(header.getRequestId(),this);
                }
            } finally {
                setWorkThenPoolOrResumeOptimizedRead(header);
            }
            getProtocolHandler().handleRequest(header, this);
        } catch (Throwable t) {
            reportException( "", t ) ;
            
        }
    }

    @Transport
    public void handleInput(LocateReplyMessage_1_0 header) throws IOException {
        generalMessage("GIOP LocateReplyMessage 1.0");
        try {
            try {
                messageHeader = header;
                setInputObject();
                inputObject.unmarshalHeader(); 
                signalResponseReceived();
            } finally {
                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException("", t);
            
        }
    }

    @Transport
    public void handleInput(LocateReplyMessage_1_1 header) throws IOException {
        generalMessage("GIOP LocateReplyMessage 1.1");
        try {
            try {
                messageHeader = header;
                setInputObject();
                
                inputObject.unmarshalHeader();
                signalResponseReceived();
            } finally {
                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException("", t);
            
        }
    }

    @Transport
    public void handleInput(LocateReplyMessage_1_2 header) throws IOException {
        generalMessage("GIOP LocateReplyMessage 1.2");
        try {
            try {
                messageHeader = header;

                
                unmarshalRequestID(header);
                setInputObject();

                requestIdInfo(header.getRequestId());

                signalResponseReceived();
            } finally {
                setWorkThenPoolOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException("", t);
            
        }
    }

    @Transport
    public void handleInput(FragmentMessage_1_1 header) throws IOException {
        generalMessage("GIOP FragmentMessage 1.1");
        try {
            moreFragmentsInfo(header.moreFragmentsToFollow());

            try {
                messageHeader = header;
                MessageMediator mediator = null;
                CDRInputObject inObj = null;

                if (connection.isServer()) {
                    mediator = connection.serverRequest_1_1_Get();
                } else {
                    mediator = connection.clientReply_1_1_Get();
                }

                if (mediator != null) {
                    inObj = mediator.getInputObject();
                }

                
                
                
                
                
                
                
                
                
                if (inObj == null) {
                    generalMessage( "No input stream: discarding fragment") ;
                    
                    releaseByteBufferToPool();
                    return;
                }

                inObj.addFragment(header, dispatchByteBuffer);

                if (! header.moreFragmentsToFollow()) {
                    if (connection.isServer()) {
                        connection.serverRequest_1_1_Remove();
                    } else {
                        connection.clientReply_1_1_Remove();
                    }
                }
            } finally {
                
                
                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException("", t);
            
        }
    }

    @Transport
    public void handleInput(FragmentMessage_1_2 header) throws IOException {
        generalMessage("GIOP FragmentMessage 1.1");
        try {
            try {
                messageHeader = header;

                
                
                
                

                unmarshalRequestID(header);

                requestIdInfo(header.getRequestId());
                moreFragmentsInfo(header.moreFragmentsToFollow());

                MessageMediator mediator = null;
                CDRInputObject inObj = null;

                if (connection.isServer()) {
                    mediator =
                        connection.serverRequestMapGet(header.getRequestId());
                } else {
                    mediator = 
                        connection.clientRequestMapGet(header.getRequestId());
                }

                if (mediator != null) {
                    inObj = mediator.getInputObject();
                }

                
                if (inObj == null) {
                    generalMessage( "No input stream: discarding fragment") ;

                    
                    
                    releaseByteBufferToPool();
                    return;
                }
                inObj.addFragment(header, dispatchByteBuffer);

                
                
                if (! connection.isServer()) {
                    

                }
            } finally {
                
                
                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException("", t);
            
        }
    }

    @InfoMethod
    private void reportGIOPVersion( GIOPVersion vers ) { }

    @Transport
    public void handleInput(CancelRequestMessage header) throws IOException {
        generalMessage("GIOP CancelRequestMessage");
        try {
            try {
                messageHeader = header;
                setInputObject();

                
                inputObject.unmarshalHeader();

                requestIdInfo(header.getRequestId());
                reportGIOPVersion(header.getGIOPVersion());

                processCancelRequest(header.getRequestId());
                releaseByteBufferToPool();
            } finally {
                setWorkThenReadOrResumeOptimizedRead(header);
            }
        } catch (Throwable t) {
            reportException("", t);
            
        }
    }
    
    private void throwNotImplemented(String msg) {
        throw new RuntimeException(
            "CorbaMessageMediatorImpl: not implemented " + msg);
    }

    
    @Transport
    private void processCancelRequest(int cancelReqId) {
        
        
        

        

        if (!connection.isServer()) {
            return; 
        }

        
        
        

        
        MessageMediator mediator = connection.serverRequestMapGet(cancelReqId);
        int requestId ;
        if (mediator == null) { 
            
            mediator = connection.serverRequest_1_1_Get();
            if (mediator == null) {
                wrapper.badCancelRequest() ;
                
                
                
                
                return; 
            }

            requestId = (mediator).getRequestId();

            if (requestId != cancelReqId) {
                
                wrapper.bad1_1CancelRequestReceived() ;
                return; 
            }

            if (requestId == 0) { 
                wrapper.cancelRequestWithId0() ;
                
                
                
                
                
                
                
                
                
                return; 
            }
        } else {
            requestId = (mediator).getRequestId();
        }

        Message msg = (mediator).getRequestHeader();
        if (msg.getType() != Message.GIOPRequest) {
            
            
            wrapper.badMessageTypeForCancel() ; 
        }

        
        

        
        
        
        
        
        
        
        
        

        mediator.getInputObject().cancelProcessing(cancelReqId);
    }

    
    
    
    

    @Transport
    public void handleRequest(RequestMessage msg,
                              MessageMediator messageMediator) {
        try {
            beginRequest(messageMediator);
            try {
                handleRequestRequest(messageMediator);
                if (messageMediator.isOneWay()) {
                    return;
                }
            } catch (Throwable t) {
                if (messageMediator.isOneWay()) {
                    return;
                }
                handleThrowableDuringServerDispatch(
                    messageMediator, t, CompletionStatus.COMPLETED_MAYBE);
            }
            sendResponse(messageMediator);
        } catch (Throwable t) {
            wrapper.exceptionInHandleRequestForRequest( t ) ;
            dispatchError(messageMediator, "RequestMessage", t);
        } finally {
            endRequest(messageMediator);
        }
    }

    @Transport
    public void handleRequest(LocateRequestMessage msg,
                              MessageMediator messageMediator) {
        try {
            beginRequest(messageMediator);
            try {
                handleLocateRequest(messageMediator);
            } catch (Throwable t) {
                handleThrowableDuringServerDispatch(
                    messageMediator, t, CompletionStatus.COMPLETED_MAYBE);
            }
            sendResponse(messageMediator);
        } catch (Throwable t) {
            wrapper.exceptionInHandleRequestForLocateRequest( t ) ;
            dispatchError(messageMediator, "LocateRequestMessage", t);
        } finally {
            endRequest(messageMediator);
        }
    }

    @Subcontract
    private void beginRequest(MessageMediator messageMediator) {
        ORB myOrb = messageMediator.getBroker();
        connection.serverRequestProcessingBegins();
    }

    @Subcontract
    private void dispatchError(MessageMediator messageMediator,
                               String msg, Throwable t) {
        
        
    }

    @Subcontract
    private void sendResponse(MessageMediator messageMediator) {

        if (orb.orbIsShutdown()) {
            return;
        }

        
        CDROutputObject outObj = messageMediator.getOutputObject();
        if (outObj != null) {
            
            outObj.finishSendingMessage();
        }
    }

    @Subcontract
    private void endRequest(MessageMediator messageMediator) {
        ORB myOrb = messageMediator.getBroker();

        if (myOrb.orbIsShutdown()) {
            return;
        }

        

        try {
            CDROutputObject outputObj = messageMediator.getOutputObject();
            if (outputObj != null) {
                outputObj.close();
            }
            CDRInputObject inputObj = messageMediator.getInputObject();
            if (inputObj != null) {
                inputObj.close();
            }
        } catch (IOException ex) {
            
            
            
            reportException( "", ex ) ;
        } finally {
            messageMediator.getConnection().serverRequestProcessingEnds();
        }
    }

    @Subcontract
    protected void handleRequestRequest(MessageMediator messageMediator) {
        
        messageMediator.getInputObject().unmarshalHeader();

        ORB myOrb = messageMediator.getBroker();
        if (myOrb.orbIsShutdown()) {
            return;
        }

        ObjectKey okey = messageMediator.getObjectKeyCacheEntry().getObjectKey();

        ServerRequestDispatcher sc = okey.getServerRequestDispatcher();

        if (sc == null) {
            throw wrapper.noServerScInDispatch() ;
        }

        
        
        
        
        
        

        try {
            myOrb.startingDispatch();
            sc.dispatch(messageMediator);
        } finally {
            myOrb.finishedDispatch();
        }
    }

    @Subcontract
    protected void handleLocateRequest(MessageMediator messageMediator) {
        ORB myOrb = messageMediator.getBroker();
        LocateRequestMessage msg = (LocateRequestMessage) messageMediator.getDispatchHeader();
        IOR ior = null;
        LocateReplyMessage reply = null;
        short addrDisp = -1; 

        try {
            messageMediator.getInputObject().unmarshalHeader();
            ObjectKey okey = msg.getObjectKeyCacheEntry().getObjectKey() ;
            ServerRequestDispatcher sc = okey.getServerRequestDispatcher() ;
            if (sc == null) {
                return;
            }

            ior = sc.locate(okey);

            if ( ior == null ) {
                reply = MessageBase.createLocateReply(
                            myOrb, msg.getGIOPVersion(),
                            msg.getEncodingVersion(), 
                            msg.getRequestId(),
                            LocateReplyMessage.OBJECT_HERE, null);

            } else {
                reply = MessageBase.createLocateReply(
                            myOrb, msg.getGIOPVersion(),
                            msg.getEncodingVersion(),
                            msg.getRequestId(),
                            LocateReplyMessage.OBJECT_FORWARD, ior);
            }
            

        } catch (AddressingDispositionException ex) {

            
            
            
            reply = MessageBase.createLocateReply(
                        myOrb, msg.getGIOPVersion(),
                        msg.getEncodingVersion(),
                        msg.getRequestId(),
                        LocateReplyMessage.LOC_NEEDS_ADDRESSING_MODE, null);

            addrDisp = ex.expectedAddrDisp();

        } catch (RequestCanceledException ex) {

            return; 

        } catch ( Exception ex ) {

            
            

            
            

            reply = MessageBase.createLocateReply(
                        myOrb, msg.getGIOPVersion(),
                        msg.getEncodingVersion(),
                        msg.getRequestId(),
                        LocateReplyMessage.UNKNOWN_OBJECT, null);
        }

        CDROutputObject outObj = createAppropriateOutputObject(messageMediator, msg, reply);
        messageMediator.setOutputObject(outObj);
        outObj.setMessageMediator(messageMediator);

        reply.write(outObj);
        
        if (ior != null) {
            ior.write(outObj);
        }
        if (addrDisp != -1) {
            AddressingDispositionHelper.write(outObj, addrDisp);
        }
    }

    @Subcontract
    private CDROutputObject createAppropriateOutputObject(
        MessageMediator messageMediator,
        Message msg, LocateReplyMessage reply) {
        CDROutputObject outObj;

        if (msg.getGIOPVersion().lessThan(GIOPVersion.V1_2)) {
            
            
            outObj = OutputStreamFactory.newCDROutputObject( messageMediator.getBroker(), this,
                             GIOPVersion.V1_0,
                             messageMediator.getConnection(),
                             reply,
                             ORBConstants.STREAM_FORMAT_VERSION_1);
        } else {
            
            
            outObj = OutputStreamFactory.newCDROutputObject( messageMediator.getBroker(), messageMediator,
                             reply,
                             ORBConstants.STREAM_FORMAT_VERSION_1);
        }
        return outObj;
    }

    @Subcontract
    public void handleThrowableDuringServerDispatch(
        MessageMediator messageMediator,
        Throwable throwable,
        CompletionStatus completionStatus) {

        
        

        
        
        
        
        
        handleThrowableDuringServerDispatch(messageMediator, throwable, 
            completionStatus, 1);
    }


    

    @Subcontract
    protected void handleThrowableDuringServerDispatch(
        MessageMediator messageMediator,
        Throwable throwable,
        CompletionStatus completionStatus,
        int iteration) {

        if (iteration > 10) {
            throw new RuntimeException("handleThrowableDuringServerDispatch: " +
                "cannot create response.", throwable);
        }

        try {
            if (throwable instanceof ForwardException) {
                ForwardException fex = (ForwardException)throwable ;
                createLocationForward( messageMediator, fex.getIOR(), null ) ;
                return;
            }

            if (throwable instanceof AddressingDispositionException) {
                handleAddressingDisposition(
                    messageMediator,
                    (AddressingDispositionException)throwable);
                return;
            } 

            

            SystemException sex = 
                convertThrowableToSystemException(throwable, completionStatus);

            createSystemExceptionResponse(messageMediator, sex, null);
            return;

        } catch (Throwable throwable2) {

            
            
            

            handleThrowableDuringServerDispatch(messageMediator,
                                                throwable2,
                                                completionStatus,
                                                iteration + 1);
            return;
        }
    }

    @Subcontract
    protected SystemException convertThrowableToSystemException( 
        Throwable throwable, CompletionStatus completionStatus) {

        if (throwable instanceof SystemException) {
            return (SystemException)throwable;
        }

        if (throwable instanceof RequestCanceledException) {
            
            
            
            

            return wrapper.requestCanceled( throwable ) ;
        }

        
        
        
        
        
        
        

        
        
        
        

        return wrapper.runtimeexception( throwable, 
            throwable.getClass().getName(), throwable.getMessage());
    }

    @Subcontract
    protected void handleAddressingDisposition(
        MessageMediator messageMediator,
        AddressingDispositionException ex) {

        short addrDisp = -1;

        

        
                    
        switch (messageMediator.getRequestHeader().getType()) {
        case Message.GIOPRequest :
            ORB myOrb = messageMediator.getBroker() ;

            ReplyMessage repHdr = MessageBase.createReply( myOrb,
                messageMediator.getGIOPVersion(), 
                messageMediator.getEncodingVersion(), 
                messageMediator.getRequestId(), 
                ReplyMessage.NEEDS_ADDRESSING_MODE, 
                ServiceContextDefaults.makeServiceContexts(myOrb), null);
            
            
            CDROutputObject outObj = OutputStreamFactory.newCDROutputObject(
                messageMediator.getBroker(),
                this,
                messageMediator.getGIOPVersion(),
                messageMediator.getConnection(),
                repHdr,
                ORBConstants.STREAM_FORMAT_VERSION_1);
            messageMediator.setOutputObject(outObj);
            outObj.setMessageMediator(messageMediator);
            repHdr.write(outObj);
            AddressingDispositionHelper.write(outObj,
                                              ex.expectedAddrDisp());
            return;

        case Message.GIOPLocateRequest :
            LocateReplyMessage locateReplyHeader = MessageBase.createLocateReply(

                messageMediator.getBroker(),
                messageMediator.getGIOPVersion(),
                messageMediator.getEncodingVersion(), messageMediator.getRequestId(),
                LocateReplyMessage.LOC_NEEDS_ADDRESSING_MODE,
                null);                                   

            addrDisp = ex.expectedAddrDisp();

            
            outObj =
                createAppropriateOutputObject(messageMediator,
                                              messageMediator.getRequestHeader(),
                                              locateReplyHeader);
            messageMediator.setOutputObject(outObj);
            outObj.setMessageMediator(messageMediator);
            locateReplyHeader.write(outObj);
            IOR ior = null;
            if (ior != null) {
                ior.write(outObj);
            }
            if (addrDisp != -1) {
                AddressingDispositionHelper.write(outObj, addrDisp);
            }
            return;
        }
    }

    @Subcontract
    public MessageMediator createResponse(
        MessageMediator messageMediator, ServiceContexts svc) {
        
        
        
        
        return createResponseHelper(
            messageMediator,
            getServiceContextsForReply(messageMediator, null));
    }

    @Subcontract
    public MessageMediator createUserExceptionResponse(
        MessageMediator messageMediator, ServiceContexts svc) {
        
        return createResponseHelper(
            messageMediator,
            getServiceContextsForReply(messageMediator, null),
            true);
    }

    @Subcontract
    public MessageMediator createUnknownExceptionResponse(
        MessageMediator messageMediator, UnknownException ex) {
        
        
        ServiceContexts contexts = null;
        SystemException sys = new UNKNOWN( 0, 
            CompletionStatus.COMPLETED_MAYBE);
        contexts = ServiceContextDefaults.makeServiceContexts( 
            messageMediator.getBroker());
        UEInfoServiceContext uei = 
            ServiceContextDefaults.makeUEInfoServiceContext(sys);
        contexts.put( uei ) ;
        return createSystemExceptionResponse(messageMediator, sys, contexts);
    }

    @Subcontract
    public MessageMediator createSystemExceptionResponse(
        MessageMediator messageMediator,
        SystemException ex,
        ServiceContexts svc) {
        if (messageMediator.getConnection() != null) {
            
            
            
            
            
            
            
            
            
            MessageMediatorImpl mediator = (MessageMediatorImpl)
                messageMediator.getConnection()
                .serverRequestMapGet(messageMediator.getRequestId());

            CDROutputObject existingOutputObject = null;
            if (mediator != null) {
                existingOutputObject = mediator.getOutputObject();
            }

            
            
            if (existingOutputObject != null &&
                mediator.sentFragment() && 
                ! mediator.sentFullMessage())
            {
                return mediator;
            }
        }
    
        
        
        
        if (messageMediator.executePIInResponseConstructor()) {
            
            
            
            
            
            
            messageMediator.getBroker().getPIHandler().setServerPIInfo( ex );
        }

        if (ex != null) {
            reportException( "Creating system exception response for", ex ) ;
        }

        ServiceContexts serviceContexts = 
            getServiceContextsForReply(messageMediator, svc);

        
        
        

        addExceptionDetailMessage(messageMediator, ex, serviceContexts);

        MessageMediator response =
            createResponseHelper(messageMediator, serviceContexts, false);

        
        
        

        ORBUtility.writeSystemException(
            ex, (OutputStream)response.getOutputObject());

        return response;
    }

    @Subcontract
    private void addExceptionDetailMessage(MessageMediator mediator,
        SystemException ex, ServiceContexts serviceContexts) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        ex.printStackTrace(pw);
        pw.flush(); 
        EncapsOutputStream encapsOutputStream = 
            OutputStreamFactory.newEncapsOutputStream(mediator.getBroker());
        encapsOutputStream.putEndian();
        encapsOutputStream.write_wstring(baos.toString());
        UnknownServiceContext serviceContext =
            ServiceContextDefaults.makeUnknownServiceContext(
                ExceptionDetailMessage.value,
                encapsOutputStream.toByteArray());
        serviceContexts.put(serviceContext);
    }

    @Subcontract
    public MessageMediator createLocationForward(
        MessageMediator messageMediator, IOR ior, ServiceContexts svc) {

        ReplyMessage reply 
            = MessageBase.createReply(
                  messageMediator.getBroker(),
                  messageMediator.getGIOPVersion(),
                  messageMediator.getEncodingVersion(), messageMediator.getRequestId(),
                  ReplyMessage.LOCATION_FORWARD,
                  getServiceContextsForReply(messageMediator, svc), 
                  ior);

        return createResponseHelper(messageMediator, reply, ior);
    }

    @Subcontract
    protected MessageMediator createResponseHelper(
        MessageMediator messageMediator, ServiceContexts svc) {
        ReplyMessage message = 
            MessageBase.createReply(
                messageMediator.getBroker(),
                messageMediator.getGIOPVersion(),
                messageMediator.getEncodingVersion(), messageMediator.getRequestId(), ReplyMessage.NO_EXCEPTION,
                svc,
                null);
        return createResponseHelper(messageMediator, message, null);
    }

    @Subcontract
    protected MessageMediator createResponseHelper(
        MessageMediator messageMediator, ServiceContexts svc,
        boolean user) {

        ReplyMessage message =
            MessageBase.createReply(
                messageMediator.getBroker(),
                messageMediator.getGIOPVersion(),
                messageMediator.getEncodingVersion(), messageMediator.getRequestId(),
                user ? ReplyMessage.USER_EXCEPTION :
                       ReplyMessage.SYSTEM_EXCEPTION,
                svc,
                null);
        return createResponseHelper(messageMediator, message, null);
    }

    @InfoMethod
    private void createResponseHelperInfo( ReplyMessage reply ) { }

    
    @Subcontract
    protected MessageMediator createResponseHelper(
        MessageMediator messageMediator, ReplyMessage reply, IOR ior) {
        
        runServantPostInvoke(messageMediator);
        runInterceptors(messageMediator, reply);
        runRemoveThreadInfo(messageMediator);

        createResponseHelperInfo(reply);
                      
        messageMediator.setReplyHeader(reply);

        CDROutputObject replyOutputObject;
        
        
        if (messageMediator.getConnection() == null) {
            
            replyOutputObject = 
                OutputStreamFactory.newCDROutputObject(orb, messageMediator,
                                    messageMediator.getReplyHeader(),
                                    messageMediator.getStreamFormatVersion(),
                                    BufferManagerFactory.GROW);
        } else {
            replyOutputObject = messageMediator.getConnection().getAcceptor()
             .createOutputObject(messageMediator.getBroker(), messageMediator);
        }
        messageMediator.setOutputObject(replyOutputObject);
        messageMediator.getOutputObject().setMessageMediator(messageMediator);

        reply.write((OutputStream) messageMediator.getOutputObject());
        if (reply.getIOR() != null) {
            reply.getIOR().write((OutputStream) messageMediator.getOutputObject());
        }
        
        

        
        
        return messageMediator;
    }

    @Subcontract
    protected void runServantPostInvoke(MessageMediator messageMediator) {
        
        
        
        
        
        ORB myOrb = null;
        
        
        if (messageMediator.executeReturnServantInResponseConstructor()) {
            
            
            
            
            
            messageMediator.setExecuteReturnServantInResponseConstructor(false);
            messageMediator.setExecuteRemoveThreadInfoInResponseConstructor(true);

            try {
                myOrb = messageMediator.getBroker();
                OAInvocationInfo info = myOrb.peekInvocationInfo() ;
                ObjectAdapter oa = info.oa();
                try {
                    oa.returnServant() ;
                } catch (Throwable thr) {
                    wrapper.unexpectedException( thr ) ;

                    if (thr instanceof Error) {
                        throw (Error) thr;
                    } else if (thr instanceof RuntimeException) {
                        throw (RuntimeException) thr;
                    }
                } finally {
                    oa.exit();
                }
            } catch (EmptyStackException ese) {
                throw wrapper.emptyStackRunServantPostInvoke( ese ) ;
            }
        }
    }

    @Subcontract
    protected void runInterceptors(MessageMediator messageMediator,
        ReplyMessage reply) {

        if( messageMediator.executePIInResponseConstructor() ) {
            
            
            
            (messageMediator.getBroker()).getPIHandler().
                invokeServerPIEndingPoint( reply );

            
            
            
            (messageMediator.getBroker()).getPIHandler().
                cleanupServerPIRequest();

            
            messageMediator.setExecutePIInResponseConstructor(false);
        }
    }

    @Subcontract
    protected void runRemoveThreadInfo(MessageMediator messageMediator) {
        
        
        if (messageMediator.executeRemoveThreadInfoInResponseConstructor()) {
            messageMediator.setExecuteRemoveThreadInfoInResponseConstructor(false);
            messageMediator.getBroker().popInvocationInfo() ;
        }
    }

    @InfoMethod
    private void generalMessage( String msg ) { }

    @Subcontract
    protected ServiceContexts getServiceContextsForReply(
        MessageMediator messageMediator, ServiceContexts contexts) {
        Connection c = messageMediator.getConnection();

        
        
        if (contexts == null) {
            if (getGIOPVersion().equals(GIOPVersion.V1_2) && 
                c != null && 
                c.getBroker().getORBData().alwaysSendCodeSetServiceContext() &&
                (getEncodingVersion() == ORBConstants.CDR_ENC_VERSION)) {
                if (!c.isPostInitialContexts()) {
                    c.setPostInitialContexts();
                    contexts = messageMediator.getBroker().
                      getServiceContextsCache().get(
                          ServiceContextsCache.CASE.SERVER_INITIAL);
                } else {
                    contexts = messageMediator.getBroker().
                      getServiceContextsCache().get(
                          ServiceContextsCache.CASE.SERVER_SUBSEQUENT);
                }
                return contexts;
            } else {
                contexts = ServiceContextDefaults.makeServiceContexts(
                    messageMediator.getBroker());
            }
        } 

        if (c != null && !c.isPostInitialContexts() &&
                (getEncodingVersion() == ORBConstants.CDR_ENC_VERSION)) {
            c.setPostInitialContexts();
            SendingContextServiceContext scsc = 
                ServiceContextDefaults.makeSendingContextServiceContext( 
                    messageMediator.getBroker().getFVDCodeBaseIOR()) ; 

            if (contexts.get( scsc.getId() ) != null) {
                throw wrapper.duplicateSendingContextServiceContext();
            }

            contexts.put( scsc ) ;
            generalMessage( "Added SendingContextServiceContext") ;
        }

        

        ORBVersionServiceContext ovsc 
            = ServiceContextDefaults.makeORBVersionServiceContext();

        if (contexts.get( ovsc.getId() ) != null) {
            throw wrapper.duplicateOrbVersionServiceContext();
        }

        contexts.put( ovsc ) ;
        generalMessage( "Added ORB version service context" ) ;

        return contexts;
    }

    @Transport
    private void releaseByteBufferToPool() {
        if (dispatchByteBuffer != null) {
            orb.getByteBufferPool().releaseByteBuffer(dispatchByteBuffer);
        }
    }

    @Subcontract
    public void cancelRequest() {
        CDRInputObject inObj = getInputObject();
        if (inObj != null) {
            inObj.cancelProcessing(getRequestId());
        }
    }

    
    
    
    
    @InfoMethod
    private void ignoringThrowable( Throwable thr ) { }

    
    @Subcontract
    public void doWork() {
        try {
            dispatch();
        } catch (Throwable t) {
            ignoringThrowable(t);
        }
    }

    public void setEnqueueTime(long timeInMillis) {
        enqueueTime = timeInMillis;
    }

    public long getEnqueueTime() {
        return enqueueTime;
    }

    public String getName() {
        return toString();
    }
}




