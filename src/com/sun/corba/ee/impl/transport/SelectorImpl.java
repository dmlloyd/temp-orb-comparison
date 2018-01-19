


package xxxx;







@Transport
@ManagedObject
@Description( "The Selector, which handles incoming requests to the ORB" )
public class SelectorImpl
    extends
        Thread
    implements
        com.sun.corba.ee.spi.transport.Selector
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private ORB orb;
    private Timer timer;
    private Selector selector;
    private long timeout;
    private final List<EventHandler> deferredRegistrations;
    private final List<SelectionKeyAndOp> interestOpsList;
    private final Map<EventHandler,ListenerThread> listenerThreads;
    private final Map<EventHandler,ReaderThread>  readerThreads;
    private boolean selectorStarted;
    private volatile boolean closed;
    private Map<EventHandler, Long> lastActivityTimers = new HashMap<EventHandler, Long>();

    interface Timer {
        long getCurrentTime();
    }

    private static final Timer SYSTEM_TIMER = new Timer() {
        public long getCurrentTime() {
            return System.currentTimeMillis();
        }
    };

    public SelectorImpl(ORB orb) {
        this(orb, SYSTEM_TIMER);
    }

    SelectorImpl(ORB orb, Timer timer) {
        this.orb = orb;
        this.timer = timer;
        selector = null;
        selectorStarted = false;
        timeout = 60000;
        deferredRegistrations = new ArrayList<EventHandler>();
        interestOpsList = new ArrayList<SelectionKeyAndOp>();
        listenerThreads = new HashMap<EventHandler,ListenerThread>();
        readerThreads = new HashMap<EventHandler,ReaderThread>();
        closed = false;
    }

    public void setTimeout(long timeout) 
    {
        this.timeout = timeout;
    }

    @ManagedAttribute
    @Description( "The selector timeout" ) 
    public long getTimeout()
    {
        return timeout;
    }

    @InfoMethod
    private void display( String msg, Object value ) { }

    @InfoMethod
    private void closedEventHandler() { }

    @InfoMethod
    private void defaultCaseForEventHandler() { }

    @Transport
    public void registerInterestOps(EventHandler eventHandler) {
        SelectionKey selectionKey = eventHandler.getSelectionKey();
        if (selectionKey.isValid()) {
            int ehOps = eventHandler.getInterestOps();
            SelectionKeyAndOp keyAndOp = new SelectionKeyAndOp(selectionKey, ehOps);
            synchronized(interestOpsList) {
                interestOpsList.add(keyAndOp);
            }
            
            selector.wakeup();
        }
        else {
            wrapper.selectionKeyInvalid(eventHandler.toString());
            display( "EventHandler SelectionKey not valid", eventHandler);
        }
    }

    @Transport
    public void registerForEvent(EventHandler eventHandler)
    {
        if (isClosed()) {
            closedEventHandler();
            return;
        }

        if (eventHandler.shouldUseSelectThreadToWait()) {
            synchronized (deferredRegistrations) {
                deferredRegistrations.add(eventHandler);
            }
            startSelector();
            selector.wakeup();
            return;
        }

        switch (eventHandler.getInterestOps()) {
        case SelectionKey.OP_ACCEPT :
            createListenerThread(eventHandler);
            break;
        case SelectionKey.OP_READ :
            createReaderThread(eventHandler);
            break;
        default:
            defaultCaseForEventHandler();
            throw new RuntimeException(
                "SelectorImpl.registerForEvent: unknown interest ops");
        }
    }

    @Transport
    public void unregisterForEvent(EventHandler eventHandler) {
        if (isClosed()) {
            closedEventHandler();
            return;
        }

        if (eventHandler.shouldUseSelectThreadToWait()) {
            SelectionKey selectionKey = eventHandler.getSelectionKey();
            if (selectionKey != null) {
                selectionKey.cancel();
                selector.wakeup();
            }

            return;
        }

        switch (eventHandler.getInterestOps()) {
        case SelectionKey.OP_ACCEPT :
            destroyListenerThread(eventHandler);
            break;
        case SelectionKey.OP_READ :
            destroyReaderThread(eventHandler);
            break;
        default:
            defaultCaseForEventHandler();
            throw new RuntimeException(
                "SelectorImpl.uregisterForEvent: unknown interest ops");
        }
    }

    @Transport
    public void close() {
        if (isClosed()) {
            closedEventHandler() ;
            return;
        }

        setClosed(true);

        
        synchronized (this) {
            for (ListenerThread lthread : listenerThreads.values()) {
                lthread.close() ;
            }
        }

        
        synchronized (this) {
            for (ReaderThread rthread : readerThreads.values()) {
                rthread.close() ;
            }
        }

        
        try {
            if (selector != null) {
                
                selector.wakeup();
            }
        } catch (Throwable t) {
            display( "Exception in close", t ) ;
        }
    }

    
    
    
    

    @InfoMethod
    private void beginSelect() { } 

    @InfoMethod
    private void endSelect() { }

    @InfoMethod
    private void selectorClosed() { }

    @InfoMethod
    private void selectResult( boolean hasNext, int count ) { }

    @InfoMethod
    private void skippingEventForCancelledKey() { }

    @Transport
    @Override
    public void run() {
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<Object>() {
                public Object run() {
                    setName("SelectorThread");
                    return null;
                }
            });

        while (!closed) {
            try {
                runSelectionLoopOnce();
            } catch (Throwable t) {
                
                
                display( "Ignoring exception", t ) ;
            }
        }
    }

    void runSelectionLoopOnce() throws IOException {
        beginSelect();

        int n = 0;
        handleDeferredRegistrations();
        enableInterestOps();
        try {
            n = selector.select(timeout);
        } catch (IOException  e) {
            display( "Exception in select:", e ) ;
        }
        if (closed) {
            selector.close();
            selectorClosed();
            return;
        }
        Iterator<SelectionKey> iterator =
            selector.selectedKeys().iterator();
        selectResult(iterator.hasNext(), n);
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            iterator.remove();

            
            
            
            
            
            
            
            
            

            if (selectionKey.isValid()) {
                EventHandler eventHandler = (EventHandler)selectionKey.attachment();
                try {
                    eventHandler.handleEvent();
                    if (lastActivityTimers.containsKey(eventHandler))
                        lastActivityTimers.put(eventHandler, timer.getCurrentTime());
                } catch (Throwable t) {
                    wrapper.exceptionInSelector( t, eventHandler ) ;
                }
            } else {
                wrapper.canceledSelectionKey( selectionKey ) ;
                skippingEventForCancelledKey();
                
                
                
                
            }
        }
        long currentTime = timer.getCurrentTime();
        for (EventHandler handler : lastActivityTimers.keySet()) {
            long elapsedTime = currentTime - lastActivityTimers.get(handler);
            ((Timeoutable) handler).checkForTimeout(elapsedTime);
        }
        endSelect();
    }


    
    
    
    

    private synchronized boolean isClosed ()
    {
        return closed;
    }

    private synchronized void setClosed(boolean closed)
    {
        this.closed = closed;
    }

    @InfoMethod
    private void selectorStarted() {}

    @Transport
    private synchronized void startSelector() {
        
        
        
        
        if (!selectorStarted) {
            selectorStarted() ;
            try {
                selector = Selector.open();
            } catch (IOException e) {
                throw new RuntimeException( ".startSelector: Selector.open exception", e);
            }
            setDaemon(true);
            start();
            selectorStarted = true;
        }
    }

    @InfoMethod
    private void registeringEventHandler( EventHandler eh ) { }

    @Transport
    private void handleDeferredRegistrations() {
        synchronized (deferredRegistrations) {
            for (EventHandler eventHandler : deferredRegistrations ) {
                registeringEventHandler(eventHandler);
                SelectableChannel channel = eventHandler.getChannel();
                SelectionKey selectionKey = null;
                try {
                    selectionKey = channel.register(selector, eventHandler.getInterestOps(), eventHandler);
                } catch (ClosedChannelException e) {
                    display( "Exception", e ) ;
                }
                eventHandler.setSelectionKey(selectionKey);
                if (eventHandler instanceof Timeoutable)
                    lastActivityTimers.put(eventHandler, timer.getCurrentTime());
            }
            deferredRegistrations.clear();
        }
    }

    @InfoMethod
    private void ignoringCancelledKeyException() { }

    @InfoMethod
    private void keyAndOpInfo( SelectionKeyAndOp val ) { }

    @Transport
    private void enableInterestOps() {
        synchronized (interestOpsList) {
            for (SelectionKeyAndOp keyAndOp : interestOpsList ) {
                SelectionKey selectionKey = keyAndOp.selectionKey;

                
                
                
                
                

                
                
                
                
                
                
                

                if (selectionKey.isValid()) {
                    keyAndOpInfo(keyAndOp);
                    int keyOp = keyAndOp.keyOp;
                    try {
                        int selectionKeyOps = selectionKey.interestOps();
                        selectionKey.interestOps(selectionKeyOps | keyOp);
                    } catch (CancelledKeyException cke) {
                        
                        
                        
                        
                        
                        
                        
                        ignoringCancelledKeyException();
                    }
                }
            }

            interestOpsList.clear();
        }
    }

    @Transport
    private void createListenerThread(EventHandler eventHandler) {
        Acceptor acceptor = eventHandler.getAcceptor();
        ListenerThread listenerThread =
            new ListenerThreadImpl(orb, acceptor);
        synchronized (this) {
            listenerThreads.put(eventHandler, listenerThread);
        }
        Throwable throwable = null;
        try {
            orb.getThreadPoolManager().getThreadPool(0)
                .getWorkQueue(0).addWork((Work)listenerThread);
        } catch (NoSuchThreadPoolException e) {
            throwable = e;
        } catch (NoSuchWorkQueueException e) {
            throwable = e;
        }
        if (throwable != null) {
            throw new RuntimeException(throwable);
        }
    }

    @InfoMethod
    private void cannotFindListenerThread() { }

    @Transport
    private void destroyListenerThread(EventHandler eventHandler) {
        ListenerThread listenerThread ;
        synchronized (this) {
            listenerThread = listenerThreads.get(eventHandler);
            if (listenerThread == null) {
                cannotFindListenerThread() ;
                return;
            }
            listenerThreads.remove(eventHandler);
        }

        listenerThread.close();
    }

    @Transport
    private void createReaderThread(EventHandler eventHandler) {
        Connection connection = eventHandler.getConnection();
        ReaderThread readerThread = 
            new ReaderThreadImpl(orb, connection );
        synchronized (this) {
            readerThreads.put(eventHandler, readerThread);
        }
        Throwable throwable = null;
        try {
            orb.getThreadPoolManager().getThreadPool(0)
                .getWorkQueue(0).addWork((Work)readerThread);
        } catch (NoSuchThreadPoolException e) {
            throwable = e;
        } catch (NoSuchWorkQueueException e) {
            throwable = e;
        }
        if (throwable != null) {
            throw new RuntimeException(throwable);
        }
    }

    @InfoMethod
    private void cannotFindReaderThread() { }

    @Transport
    private void destroyReaderThread(EventHandler eventHandler) {
        ReaderThread readerThread ;
        synchronized (this) {
            readerThread = readerThreads.get(eventHandler);
            if (readerThread == null) {
                cannotFindReaderThread();
                return;
            }
            readerThreads.remove(eventHandler);
        }
        readerThread.close();
    }

    
    
    
    
    
    private static class SelectionKeyAndOp
    {
        
        public int keyOp;
        public SelectionKey selectionKey;

        
        public SelectionKeyAndOp(SelectionKey selectionKey, int keyOp) {
            this.selectionKey = selectionKey;
            this.keyOp = keyOp;
        }
    }


}

