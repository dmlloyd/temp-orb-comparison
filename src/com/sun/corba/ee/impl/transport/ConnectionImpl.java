


package xxxx;





@Transport
public class ConnectionImpl extends EventHandlerBase implements Connection, Work {

    protected static final ORBUtilSystemException wrapper =
            ORBUtilSystemException.self;

    
    
    

    protected SocketChannel socketChannel;
    private MessageParser messageParser;
    private SocketChannelReader socketChannelReader;
    private Throwable discardedThrowable;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    protected ByteBuffer byteBuffer = null;
    protected long enqueueTime;

    
    
    protected ContactInfo contactInfo;
    protected Acceptor acceptor;
    protected ConnectionCache connectionCache;

    
    
    

    protected Socket socket;    
    protected long timeStamp = 0;
    protected boolean isServer = false;

    
    
    protected AtomicInteger requestId = new AtomicInteger(5);
    protected ResponseWaitingRoom responseWaitingRoom;
    private int state;
    protected final java.lang.Object stateEvent = new java.lang.Object();
    protected final java.lang.Object writeEvent = new java.lang.Object();
    protected boolean writeLocked;
    protected int serverRequestCount = 0;

    
    
    Map<Integer, MessageMediator> serverRequestMap = null;

    
    
    
    protected boolean postInitialContexts = false;

    
    
    protected IOR codeBaseServerIOR;

    
    
    
    protected CachedCodeBase cachedCodeBase = new CachedCodeBase(this);


    
    protected TcpTimeouts tcpTimeouts;

    
    
    protected TemporarySelector tmpReadSelector;
    
    protected final java.lang.Object tmpReadSelectorLock = new java.lang.Object();

    private NioBufferWriter bufferWriter;
    protected Dispatcher dispatcher = DISPATCHER;

    
    Throwable getDiscardedThrowable() {
        return discardedThrowable;
    }

    
    void clearDiscardedThrowable() {
        discardedThrowable = null;
    }

    interface Dispatcher {
        boolean dispatch(MessageMediator messageMediator);
    }

    final static Dispatcher DISPATCHER = new Dispatcher() {
        @Override
        public boolean dispatch(MessageMediator messageMediator) {
            return messageMediator.dispatch();
        }
    };


    
    
    
    
    
    
    
    
    
    
    
    protected ConcurrentHashMap<RequestId, Queue<MessageMediator>> fragmentMap;

    
    public ConnectionImpl(ORB orb) {
        this.orb = orb;
        messageParser = new MessageParserImpl(orb, this);
        socketChannelReader = new SocketChannelReader(orb);
        setWork(this);
        responseWaitingRoom = new ResponseWaitingRoomImpl(orb, this);
        setTcpTimeouts(orb.getORBData().getTransportTcpTimeouts());
    }

    
    protected ConnectionImpl(ORB orb,
                             boolean useSelectThreadToWait,
                             boolean useWorkerThread) {
        this(orb);
        setUseSelectThreadToWait(useSelectThreadToWait);
        setUseWorkerThreadForEvent(useWorkerThread);

        if (useSelectThreadToWait) {
            
            fragmentMap = new ConcurrentHashMap<RequestId, Queue<MessageMediator>>();
        }
    }

    
    private ConnectionImpl(ORB orb,
                           ContactInfo contactInfo,
                           boolean useSelectThreadToWait,
                           boolean useWorkerThread,
                           String socketType,
                           String hostname,
                           int port) {
        this(orb, useSelectThreadToWait, useWorkerThread);

        this.contactInfo = contactInfo;

        try {
            defineSocket(useSelectThreadToWait,
                    orb.getORBData().getSocketFactory().createSocket(socketType, new InetSocketAddress(hostname, port)));
        } catch (Throwable t) {
            throw wrapper.connectFailure(t, socketType, hostname,
                    Integer.toString(port));
        }
        state = OPENING;
    }

    protected final void defineSocket(boolean useSelectThreadToWait, Socket socket) throws IOException {
        this.socket = socket;
        socketChannel = socket.getChannel();

        if (socketChannel == null) {
            setUseSelectThreadToWait(false);  
        } else {
            socketChannel.configureBlocking(!useSelectThreadToWait);
        }
    }

    
    public ConnectionImpl(ORB orb,
                          ContactInfo contactInfo,
                          String socketType,
                          String hostname,
                          int port) {
        this(orb, contactInfo,
                orb.getORBData().connectionSocketUseSelectThreadToWait(),
                orb.getORBData().connectionSocketUseWorkerThreadForEvent(),
                socketType, hostname, port);
    }

    
    private ConnectionImpl(ORB orb, Acceptor acceptor, Socket socket,
                           boolean useSelectThreadToWait, boolean useWorkerThread) {
        this(orb, useSelectThreadToWait, useWorkerThread);

        try {
            defineSocket(useSelectThreadToWait, socket);
        } catch (IOException e) {
            RuntimeException rte = new RuntimeException();
            rte.initCause(e);
            throw rte;
        }

        this.acceptor = acceptor;
        serverRequestMap = Collections.synchronizedMap(new HashMap<Integer, MessageMediator>());
        isServer = true;

        state = ESTABLISHED;
    }

    
    public ConnectionImpl(ORB orb,
                          Acceptor acceptor,
                          Socket socket) {
        this(orb, acceptor, socket,
                (socket.getChannel() != null && orb.getORBData().connectionSocketUseSelectThreadToWait()),
                (socket.getChannel() != null && orb.getORBData().connectionSocketUseWorkerThreadForEvent()));
    }

    
    
    
    

    public boolean shouldRegisterReadEvent() {
        return true;
    }

    public boolean shouldRegisterServerReadEvent() {
        return true;
    }

    public boolean read() {
        MessageMediator messageMediator = readBits();

        
        return messageMediator == null || dispatcher.dispatch(messageMediator);
    }

    private MessageMediator readBits() {
        try {
            return createMessageMediator();
        } catch (ThreadDeath td) {
            try {
                purgeCalls(wrapper.connectionAbort(td), false, false);
            } catch (Throwable t) {
                exceptionInfo("purgeCalls", t);
            }
            throw td;
        } catch (Throwable ex) {
            exceptionInfo("readBits", ex);

            if (ex instanceof SystemException) {
                SystemException se = (SystemException) ex;
                if (se.minor == ORBUtilSystemException.CONNECTION_REBIND) {
                    unregisterForEventAndPurgeCalls(se);
                    throw se;
                } else {
                    try {
                        if (se instanceof INTERNAL) {
                            sendMessageError(GIOPVersion.DEFAULT_VERSION);
                        }
                    } catch (IOException e) {
                        exceptionInfo("sendMessageError", e);
                    }
                }
            }
            unregisterForEventAndPurgeCalls(wrapper.connectionAbort(ex));

            
            
            
            
            
            
            
            throw wrapper.throwableInReadBits(ex);
        }
    }

    private void unregisterForEventAndPurgeCalls(SystemException ex) {
        
        orb.getTransportManager().getSelector(0).unregisterForEvent(this);
        
        purgeCalls(ex, true, false);
    }

    
    
    private MessageMediator createMessageMediator() {
        try {
            ByteBuffer headerBuffer = read(0, Message.GIOPMessageHeaderLength);
            Message header = MessageBase.parseGiopHeader(orb, this, headerBuffer, 0);

            headerBuffer.position(Message.GIOPMessageHeaderLength);
            int msgSizeMinusHeader = header.getSize() - Message.GIOPMessageHeaderLength;
            ByteBuffer buffer = read(Message.GIOPMessageHeaderLength, msgSizeMinusHeader);

            traceMessageBodyReceived(orb, buffer);

            return new MessageMediatorImpl(orb, this, header, buffer);
        } catch (IOException e) {
            throw wrapper.ioexceptionWhenReadingConnection(e, this);
        }
    }


    private void traceMessageBodyReceived(ORB orb, ByteBuffer buf) {
        TransportManager ctm = orb.getTransportManager();
        MessageTraceManagerImpl mtm = (MessageTraceManagerImpl) ctm.getMessageTraceManager();
        if (mtm.isEnabled()) {
            mtm.recordBodyReceived(buf);
        }
    }

    public boolean hasSocketChannel() {
        return getSocketChannel() != null;
    }

    
    
    
    private ByteBuffer read(int offset, int length) throws IOException {
        try {
            int size = offset + length;
            byte[] buf = new byte[size];
            
            
            
            
            readFully(getSocket().getInputStream(), buf, offset, length);
            return ByteBuffer.wrap(buf);
        } catch (IOException ioe) {
            if (getState() == CLOSE_RECVD) {
                throw wrapper.connectionRebind(ioe);
            } else {
                throw ioe;
            }
        }
    }

    
    
    



    
    
    
    
    
    
    
    
    
    private void readFully(java.io.InputStream is, byte[] buf, int offset, int length)
            throws IOException {
        int n = 0;
        int bytecount;

        do {
            bytecount = is.read(buf, offset + n, length - n);

            if (bytecount < 0) {
                throw new IOException("End-of-stream");
            } else {
                n += bytecount;
            }
        } while (n < length);
    }

    
    @Transport
    public void write(ByteBuffer byteBuffer) throws IOException {
        try {
            if (hasSocketChannel()) {
                if (getSocketChannel().isBlocking()) {
                    throw wrapper.temporaryWriteSelectorWithBlockingConnection(this);
                }
                writeUsingNio(byteBuffer);
            } else {
                if (!byteBuffer.hasArray()) {
                    throw wrapper.unexpectedDirectByteBufferWithNonChannelSocket();
                }

                byte[] tmpBuf = new byte[byteBuffer.limit()];
                System.arraycopy(byteBuffer.array(), byteBuffer.arrayOffset(), tmpBuf, 0, tmpBuf.length);
                getSocket().getOutputStream().write(tmpBuf, 0, tmpBuf.length);
                getSocket().getOutputStream().flush();
            }

            
            
            
            getConnectionCache().stampTime(this);
        } catch (IOException ioe) {
            if (getState() == CLOSE_RECVD) {
                throw wrapper.connectionRebind(ioe);
            } else {
                throw ioe;
            }
        }
    }

    private void writeUsingNio(ByteBuffer byteBuffer) throws IOException {
        if (bufferWriter == null)
            bufferWriter = new NioBufferWriter(getSocketChannel(), tcpTimeouts);
        bufferWriter.write(byteBuffer);
    }

    
    @Transport
    public synchronized void close() {
        writeLock();

        
        
        
        
        

        if (isBusy()) { 
            writeUnlock();
            doNotCloseBusyConnection();
            return;
        }

        try {
            try {
                sendCloseConnection(GIOPVersion.V1_0);
            } catch (Throwable t) {
                wrapper.exceptionWhenSendingCloseConnection(t);
            }

            synchronized (stateEvent) {
                state = CLOSE_SENT;
                stateEvent.notifyAll();
            }

            
            
            

            
            
            purgeCalls(wrapper.connectionRebind(), false, true);

        } catch (Exception ex) {
            wrapper.exceptionInPurgeCalls(ex);
        }

        closeConnectionResources();
    }

    @Transport
    public void closeConnectionResources() {
        Selector selector = orb.getTransportManager().getSelector(0);
        selector.unregisterForEvent(this);
        closeSocketAndTemporarySelectors();
    }

    @InfoMethod
    private void closingSocketChannel() {
    }

    @InfoMethod
    private void IOExceptionOnClose(Exception e) {
    }

    @Transport
    protected void closeSocketAndTemporarySelectors() {
        try {
            if (socketChannel != null) {
                closeTemporarySelectors();
                closingSocketChannel();
                socketChannel.socket().close();
            }
        } catch (IOException e) {
            IOExceptionOnClose(e);
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception e) {
                IOExceptionOnClose(e);
            }
        }
    }

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public EventHandler getEventHandler() {
        return this;
    }

    
    
    
    
    public boolean isServer() {
        return isServer;
    }

    public boolean isClosed() {
        boolean result = true;
        if (socketChannel != null) {
            result = !socketChannel.isOpen();
        } else if (socket != null) {
            result = socket.isClosed();
        }
        return result;
    }

    public boolean isBusy() {
        if (serverRequestCount > 0 ||
                getResponseWaitingRoom().numberRegistered() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long time) {
        timeStamp = time;
    }

    protected int getState() {
        return state;
    }

    protected void setState(int state) {
        this.state = state;
    }

    public void setState(String stateString) {
        synchronized (stateEvent) {
            if (stateString.equals("ESTABLISHED")) {
                state = ESTABLISHED;
                stateEvent.notifyAll();
            } else {
                
            }
        }
    }

    
    @Transport
    public void writeLock() {
        
        while (true) {
            int localState;
            synchronized (stateEvent) {
                localState = getState();
            }

            localStateInfo(localState);

            switch (localState) {

                case OPENING:
                    synchronized (stateEvent) {
                        if (getState() != OPENING) {
                            
                            break;
                        }
                        try {
                            stateEvent.wait();
                        } catch (InterruptedException ie) {
                            wrapper.openingWaitInterrupted(ie);
                        }
                    }
                    
                    break;

                case ESTABLISHED:
                    synchronized (writeEvent) {
                        if (!writeLocked) {
                            writeLocked = true;
                            return;
                        }

                        try {
                            
                            
                            while (getState() == ESTABLISHED && writeLocked) {
                                writeEvent.wait(100);
                            }
                        } catch (InterruptedException ie) {
                            wrapper.establishedWaitInterrupted(ie);
                        }
                    }
                    
                    break;

                case ABORT:
                    synchronized (stateEvent) {
                        if (getState() != ABORT) {
                            break;
                        }
                        throw wrapper.writeErrorSend();
                    }

                case CLOSE_RECVD:
                    
                    
                    synchronized (stateEvent) {
                        if (getState() != CLOSE_RECVD) {
                            break;
                        }
                        throw wrapper.connectionRebind();
                    }

                default:
                    
                    throw new RuntimeException(".writeLock: bad state");
            }
        }
    }

    @Transport
    public void writeUnlock() {
        synchronized (writeEvent) {
            writeLocked = false;
            writeEvent.notify(); 
        }
    }

    
    
    public void sendWithoutLock(CDROutputObject outputObject) {
        
        

        
        

        try {
            
            CDROutputObject cdrOutputObject = outputObject;
            cdrOutputObject.writeTo(this);

            
            

        } catch (IOException exc) {
            
            
            
            
            

            
            
            final SystemException sysexc = (getState() == CLOSE_RECVD) ?
                    wrapper.connectionRebindMaybe(exc) :
                    wrapper.writeErrorSend(exc);

            purgeCalls(sysexc, false, true);

            throw sysexc;
        }
    }

    public void registerWaiter(MessageMediator messageMediator) {
        responseWaitingRoom.registerWaiter(messageMediator);
    }

    public void unregisterWaiter(MessageMediator messageMediator) {
        responseWaitingRoom.unregisterWaiter(messageMediator);
    }

    public CDRInputObject waitForResponse(MessageMediator messageMediator) {
        return responseWaitingRoom.waitForResponse(messageMediator);
    }

    public void setConnectionCache(ConnectionCache connectionCache) {
        this.connectionCache = connectionCache;
    }

    public ConnectionCache getConnectionCache() {
        return connectionCache;
    }

    
    
    
    

    @Override
    public void setUseSelectThreadToWait(boolean x) {
        useSelectThreadToWait = x;
    }

    public SelectableChannel getChannel() {
        return socketChannel;
    }

    public int getInterestOps() {
        return SelectionKey.OP_READ;
    }

    

    public Connection getConnection() {
        return this;
    }

    
    
    
    

    public String getName() {
        return this.toString();
    }

    @Transport
    public void doWork() {
        discardedThrowable = null;
        try {
            
            
            
            

            if (hasSocketChannel()) {
                doOptimizedReadStrategy();
            } else {
                read();
            }
        } catch (Throwable t) {
            discardedThrowable = t;
            exceptionInfo(t);
        }
    }

    public void setEnqueueTime(long timeInMillis) {
        enqueueTime = timeInMillis;
    }

    public long getEnqueueTime() {
        return enqueueTime;
    }

    
    
    
    

    public ResponseWaitingRoom getResponseWaitingRoom() {
        return responseWaitingRoom;
    }

    
    

    public void serverRequestMapPut(int reqId, MessageMediator messageMediator) {
        serverRequestMap.put(reqId, messageMediator);
    }

    public MessageMediator serverRequestMapGet(int reqId) {
        return serverRequestMap.get(reqId);
    }

    public void serverRequestMapRemove(int reqId) {
        serverRequestMap.remove(reqId);
    }

    public Queue<MessageMediator> getFragmentList(RequestId corbaRequestId) {
        return fragmentMap.get(corbaRequestId);
    }

    public void removeFragmentList(RequestId corbaRequestId) {
        fragmentMap.remove(corbaRequestId);
    }

    
    
    public java.net.Socket getSocket() {
        return socket;
    }

    
    public synchronized void serverRequestProcessingBegins() {
        serverRequestCount++;
    }

    public synchronized void serverRequestProcessingEnds() {
        serverRequestCount--;
    }

    
    
    

    public int getNextRequestId() {
        return requestId.getAndIncrement();
    }

    
    protected CodeSetComponentInfo.CodeSetContext codeSetContext = null;

    public ORB getBroker() {
        return orb;
    }

    public synchronized CodeSetComponentInfo.CodeSetContext getCodeSetContext() {
        return codeSetContext;
    }

    public synchronized void setCodeSetContext(CodeSetComponentInfo.CodeSetContext csc) {
        if (codeSetContext == null) {

            if (OSFCodeSetRegistry.lookupEntry(csc.getCharCodeSet()) == null ||
                    OSFCodeSetRegistry.lookupEntry(csc.getWCharCodeSet()) == null) {
                
                
                
                throw wrapper.badCodesetsFromClient();
            }

            codeSetContext = csc;
        }
    }

    
    
    

    
    
    
    
    
    

    public MessageMediator clientRequestMapGet(int requestId) {
        return responseWaitingRoom.getMessageMediator(requestId);
    }

    protected MessageMediator clientReply_1_1;

    public void clientReply_1_1_Put(MessageMediator x) {
        clientReply_1_1 = x;
    }

    public MessageMediator clientReply_1_1_Get() {
        return clientReply_1_1;
    }

    public void clientReply_1_1_Remove() {
        clientReply_1_1 = null;
    }

    protected MessageMediator serverRequest_1_1;

    public void serverRequest_1_1_Put(MessageMediator x) {
        serverRequest_1_1 = x;
    }

    public MessageMediator serverRequest_1_1_Get() {
        return serverRequest_1_1;
    }

    public void serverRequest_1_1_Remove() {
        serverRequest_1_1 = null;
    }

    protected String getStateString(int state) {
        synchronized (stateEvent) {
            switch (state) {
                case OPENING:
                    return "OPENING";
                case ESTABLISHED:
                    return "ESTABLISHED";
                case CLOSE_SENT:
                    return "CLOSE_SENT";
                case CLOSE_RECVD:
                    return "CLOSE_RECVD";
                case ABORT:
                    return "ABORT";
                default:
                    return "???";
            }
        }
    }

    public synchronized boolean isPostInitialContexts() {
        return postInitialContexts;
    }

    
    public synchronized void setPostInitialContexts() {
        postInitialContexts = true;
    }

    
    @Transport
    public void purgeCalls(SystemException systemException, boolean die,
                           boolean lockHeld) {

        int minor_code = systemException.minor;
        
        
        synchronized (stateEvent) {
            localStateInfo(getState());
            if ((getState() == ABORT) || (getState() == CLOSE_RECVD)) {
                return;
            }
        }

        
        try {
            if (!lockHeld) {
                writeLock();
            }
        } catch (SystemException ex) {
            exceptionInfo(ex);
        }

        
        
        synchronized (stateEvent) {
            if (minor_code == ORBUtilSystemException.CONNECTION_REBIND) {
                state = CLOSE_RECVD;
                systemException.completed = CompletionStatus.COMPLETED_NO;
            } else {
                state = ABORT;
                systemException.completed = CompletionStatus.COMPLETED_MAYBE;
            }
            stateEvent.notifyAll();
        }

        closeSocketAndTemporarySelectors();

        

        if (serverRequest_1_1 != null) { 
            serverRequest_1_1.cancelRequest();
        }

        if (serverRequestMap != null) { 
            for (MessageMediator mm : serverRequestMap.values()) {
                mm.cancelRequest();
            }
        }

        
        

        responseWaitingRoom.signalExceptionToAllWaiters(systemException);

        if (contactInfo != null) {
            ((OutboundConnectionCache) connectionCache).remove(contactInfo);
        } else if (acceptor != null) {
            ((InboundConnectionCache) connectionCache).remove(this);
        }

        
        
        

        
        
        
        
        
        
        

        writeUnlock();
    }

    

    public void sendCloseConnection(GIOPVersion giopVersion)
            throws IOException {
        Message msg = MessageBase.createCloseConnection(giopVersion);
        sendHelper(giopVersion, msg);
    }

    public void sendMessageError(GIOPVersion giopVersion)
            throws IOException {
        Message msg = MessageBase.createMessageError(giopVersion);
        sendHelper(giopVersion, msg);
    }

    
    public void sendCancelRequest(GIOPVersion giopVersion, int requestId)
            throws IOException {

        Message msg = MessageBase.createCancelRequest(giopVersion, requestId);
        sendHelper(giopVersion, msg);
    }

    protected void sendHelper(GIOPVersion giopVersion, Message msg)
            throws IOException {
        
        CDROutputObject outputObject =
                new CDROutputObject(orb, null, giopVersion, this, msg,
                        ORBConstants.STREAM_FORMAT_VERSION_1);
        msg.write(outputObject);

        outputObject.writeTo(this);
    }

    
    public void sendCancelRequestWithLock(GIOPVersion giopVersion,
                                          int requestId)
            throws IOException {
        writeLock();
        try {
            sendCancelRequest(giopVersion, requestId);
        } catch (IOException ioe) {
            if (getState() == CLOSE_RECVD) {
                throw wrapper.connectionRebind(ioe);
            } else {
                throw ioe;
            }
        } finally {
            writeUnlock();
        }
    }

    
    
    
    
    
    
    
    
    

    
    
    

    public final void setCodeBaseIOR(IOR ior) {
        codeBaseServerIOR = ior;
    }

    public final IOR getCodeBaseIOR() {
        return codeBaseServerIOR;
    }

    
    
    public final CodeBase getCodeBase() {
        return cachedCodeBase;
    }

    

    
    protected void setTcpTimeouts(TcpTimeouts tcpTimeouts) {
        this.tcpTimeouts = tcpTimeouts;
    }

    @Transport
    protected void doOptimizedReadStrategy() {
        try {
            















            
            
            resumeSelectOnMainSelector();

        } catch (ThreadDeath td) {
            try {
                purgeCalls(wrapper.connectionAbort(td), false, false);
            } catch (Throwable t) {
                exceptionInfo(t);
            }
            throw td;
        } catch (Throwable ex) {
            if (ex instanceof SystemException) {
                SystemException se = (SystemException) ex;
                if (se.minor == ORBUtilSystemException.CONNECTION_REBIND) {
                    unregisterForEventAndPurgeCalls(se);
                    throw se;
                } else {
                    try {
                        if (se instanceof INTERNAL) {
                            sendMessageError(GIOPVersion.DEFAULT_VERSION);
                        }
                    } catch (IOException e) {
                        exceptionInfo(e);
                    }
                }
            }
            unregisterForEventAndPurgeCalls(wrapper.connectionAbort(ex));

            
            
            
            
            
            
            
            throw wrapper.throwableInDoOptimizedReadStrategy(ex);
        }
    }

    public ByteBuffer extractAndProcessMessages(ByteBuffer byteBuffer) {
        messageParser.offerBuffer(byteBuffer);
        MessageMediator messageMediator = messageParser.getMessageMediator();
        while (messageMediator != null) {
            queueUpWork(messageMediator);
            byteBuffer = messageParser.getRemainderBuffer();
            messageParser.offerBuffer(byteBuffer);
            messageMediator = messageParser.getMessageMediator();
        }
        return byteBuffer;
    }

    private void parseBytesAndDispatchMessages() {
        byteBuffer.limit(byteBuffer.position())
                .position(messageParser.getNextMessageStartPosition());
        do {
            MessageMediator messageMediator = null;
            Message message = messageParser.parseBytes(byteBuffer, this);
            byteBuffer = messageParser.getRemainderBuffer();
            if (message != null) {
                messageMediator = new MessageMediatorImpl(orb, this, message, messageParser.getMsgByteBuffer());
            }

            if (messageMediator != null) {
                queueUpWork(messageMediator);
            }
        } while (messageParser.hasMoreBytesToParse());
        if (messageParser.isExpectingMoreData()) {
            
            if (byteBuffer.position() == byteBuffer.capacity()) {
                byteBuffer = messageParser.getNewBufferAndCopyOld(byteBuffer);
            }
        }
    }

    @Transport
    protected void blockingRead() {
        
        
        

        TcpTimeouts.Waiter waiter = tcpTimeouts.waiter();
        TemporarySelector tmpSelector = null;
        SelectionKey sk = null;
        try {
            getConnectionCache().stampTime(this);
            tmpSelector = getTemporaryReadSelector();
            sk = tmpSelector.registerChannel(getSocketChannel(), SelectionKey.OP_READ);
            do {
                int nsel = tmpSelector.select(waiter.getTimeForSleep());
                if (nsel > 0) {
                    tmpSelector.removeSelectedKey(sk);
                    int bytesRead = getSocketChannel().read(byteBuffer);
                    if (bytesRead > 0) {
                        parseBytesAndDispatchMessages();
                        
                        waiter = tcpTimeouts.waiter();
                    } else if (bytesRead < 0) {
                        Exception exc = new IOException("End-of-stream");
                        throw wrapper.blockingReadEndOfStream(
                                exc, exc.toString(), this.toString());
                    } else { 
                        waiter.advance();
                    }
                } else { 
                    waiter.advance();
                }
            } while (!waiter.isExpired() && messageParser.isExpectingMoreData());

            
            
            
            if (messageParser.isExpectingMoreData()) {
                
                
                throw wrapper.blockingReadTimeout(
                        tcpTimeouts.get_max_time_to_wait(), waiter.timeWaiting());
            }
        } catch (IOException ioe) {
            throw wrapper.exceptionBlockingReadWithTemporarySelector(ioe, this);
        } finally {
            if (tmpSelector != null) {
                try {
                    tmpSelector.cancelAndFlushSelector(sk);
                } catch (IOException ex) {
                    wrapper.unexpectedExceptionCancelAndFlushTempSelector(ex);
                }
            }
        }
    }

    private void queueUpWork(MessageMediator messageMediator) {
        
        boolean addToWorkerThreadQueue = true;
        Message message = messageMediator.getDispatchHeader();
        if (message.supportsFragments()) {
            
            if (message.getType() != Message.GIOPFragment) {
                
                
                if (message.moreFragmentsToFollow()) {
                    
                    
                    RequestId corbaRequestId = messageMediator.getRequestIdFromRawBytes();
                    fragmentMap.put(corbaRequestId, new LinkedList<MessageMediator>());
                    addedEntryToFragmentMap(corbaRequestId);
                }
            } else {
                
                
                
                RequestId corbaRequestId = messageMediator.getRequestIdFromRawBytes();
                Queue<MessageMediator> queue = fragmentMap.get(corbaRequestId);
                if (queue != null) {
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    synchronized (queue) {
                        queue.add(messageMediator);
                        queuedMessageFragment(corbaRequestId);
                        
                        
                        queue.notifyAll();
                    }
                    
                    
                    
                    
                    
                    addToWorkerThreadQueue = false;
                } else {
                    
                    wrapper.noFragmentQueueForRequestId(corbaRequestId.toString());
                }
            }
        }

        if (addToWorkerThreadQueue) {
            addMessageMediatorToWorkQueue(messageMediator);
        }
    }

    @Transport
    protected int nonBlockingRead() {
        int bytesRead = 0;
        SocketChannel sc = getSocketChannel();
        try {
            if (sc == null || sc.isBlocking()) {
                throw wrapper.nonBlockingReadOnBlockingSocketChannel(this);
            }
            bytesRead = sc.read(byteBuffer);
            if (bytesRead < 0) {
                throw new IOException("End-of-stream");
            }
            getConnectionCache().stampTime(this);
        } catch (IOException ioe) {
            if (getState() == CLOSE_RECVD) {
                throw wrapper.connectionRebind(ioe);
            } else {
                throw wrapper.ioexceptionWhenReadingConnection(ioe, this);
            }
        }

        return bytesRead;
    }

    @Transport
    private void addMessageMediatorToWorkQueue(final MessageMediator messageMediator) {
        
        Throwable throwable = null;
        int poolToUse = -1;
        try {
            poolToUse = messageMediator.getThreadPoolToUse();
            orb.getThreadPoolManager().getThreadPool(poolToUse).getWorkQueue(0).addWork((Work)messageMediator);
        } catch (NoSuchThreadPoolException e) {
            throwable = e;
        } catch (NoSuchWorkQueueException e) {
            throwable = e;
        }
        
        if (throwable != null) {
            throw wrapper.noSuchThreadpoolOrQueue(throwable, poolToUse);
        }
    }

    @Transport
    private void resumeSelectOnMainSelector() {
        
        
        
        
        
        

        
        
        
        
        
        orb.getTransportManager().getSelector(0).registerInterestOps(this);
    }

    @Transport
    protected TemporarySelector getTemporaryReadSelector() throws IOException {
        
        
        if (getSocketChannel() == null || getSocketChannel().isBlocking()) {
            throw wrapper.temporaryReadSelectorWithBlockingConnection(this);
        }
        synchronized (tmpReadSelectorLock) {
            if (tmpReadSelector == null) {
                tmpReadSelector = new TemporarySelector(getSocketChannel());
            }
        }
        return tmpReadSelector;
    }

    @Transport
    protected void closeTemporarySelectors() throws IOException {
        synchronized (tmpReadSelectorLock) {
            if (tmpReadSelector != null) {
                closingReadSelector(tmpReadSelector);
                try {
                    tmpReadSelector.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        if (bufferWriter != null)
            bufferWriter.closeTemporaryWriteSelector();
    }

    @Override
    public String toString() {
        synchronized (stateEvent) {
            String str;
            if (socketChannel != null) {
                str = socketChannel.toString();
            } else if (socket != null) {
                str = socket.toString();
            } else {
                str = "<no connection!>";
            }

            return "SocketOrChannelConnectionImpl[ "
                    + str + " "
                    + getStateString(getState()) + " "
                    + shouldUseSelectThreadToWait() + " "
                    + shouldUseWorkerThreadForEvent()
                    + "]";
        }
    }

    @InfoMethod
    private void exceptionInfo(Throwable t) {
    }

    @InfoMethod
    private void exceptionInfo(String string, Throwable t) {
    }

    @InfoMethod
    private void readFullySleeping(int time) {
    }

    @InfoMethod
    private void doNotCloseBusyConnection() {
    }

    @InfoMethod
    private void localStateInfo(int localState) {
    }

    @InfoMethod
    private void addedEntryToFragmentMap(RequestId corbaRequestId) {
    }

    @InfoMethod
    private void queuedMessageFragment(RequestId corbaRequestId) {
    }

    @InfoMethod
    private void closingReadSelector(TemporarySelector tmpReadSelector) {
    }
}


