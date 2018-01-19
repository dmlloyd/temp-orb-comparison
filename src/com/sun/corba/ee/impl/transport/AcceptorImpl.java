


package com.sun.corba.ee.impl.transport;







@Transport public class AcceptorImpl extends AcceptorBase {
    protected ServerSocketChannel serverSocketChannel;
    protected ServerSocket serverSocket;
    
    private Class<?> lastExceptionClassSeen = null ;

    public AcceptorImpl(ORB orb, int port,
                                       String name, String type)
    {
        super( orb, port, name, type ) ;
    }

    @Transport
    public synchronized boolean initialize() {
        if (initialized) {
            return false;
        }
        InetSocketAddress inetSocketAddress = null;
        String host = "all interfaces";
        try {
            if (orb.getORBData().getListenOnAllInterfaces()) {
                inetSocketAddress = new InetSocketAddress(port);
            } else {
                host = orb.getORBData().getORBServerHost();
                inetSocketAddress = new InetSocketAddress(host, port);
            }
            serverSocket = orb.getORBData().getSocketFactory()
                .createServerSocket(type, inetSocketAddress);
            internalInitialize();
            if (orb.getORBData().showInfoMessages()) {
                wrapper.infoCreateListenerSucceeded(host, Integer.toString(port));
            }
        } catch (Throwable t) {
            throw wrapper.createListenerFailed(t, host, port);
        }
        initialized = true;
        return true;
    }

    protected void internalInitialize()
        throws Exception
    {
        
        
        

        port = serverSocket.getLocalPort();

        

        orb.getCorbaTransportManager().getInboundConnectionCache(this);

        

        serverSocketChannel = serverSocket.getChannel();

        if (serverSocketChannel != null) {
            setUseSelectThreadToWait(
                orb.getORBData().acceptorSocketUseSelectThreadToWait());
            serverSocketChannel.configureBlocking(
                ! orb.getORBData().acceptorSocketUseSelectThreadToWait());
        } else {
            
            setUseSelectThreadToWait(false);
        }
        setUseWorkerThreadForEvent(
            orb.getORBData().acceptorSocketUseWorkerThreadForEvent());

    }

    @InfoMethod
    private void usingServerSocket( ServerSocket ss ) { }

    @InfoMethod
    private void usingServerSocketChannel( ServerSocketChannel ssc ) { }

    @Transport
    public Socket getAcceptedSocket() {
        SocketChannel socketChannel = null;
        Socket socket = null;

        try {
            if (serverSocketChannel == null) {
                socket = serverSocket.accept();
                usingServerSocket( serverSocket ) ;
            } else {
                socketChannel = serverSocketChannel.accept();
                socket = socketChannel.socket();
                usingServerSocketChannel(serverSocketChannel);
            }

            orb.getORBData().getSocketFactory()
                .setAcceptedSocketOptions(this, serverSocket, socket);

            
            
            lastExceptionClassSeen = null ;
        } catch (IOException e) {
            
            
            
            
            
            
            
            if (e.getClass() == lastExceptionClassSeen) {
                wrapper.ioexceptionInAcceptFine(e);
            } else {
                lastExceptionClassSeen = e.getClass() ;
                wrapper.ioexceptionInAccept(e);
            }

            orb.getTransportManager().getSelector(0).unregisterForEvent(this);
            
            orb.getTransportManager().getSelector(0).registerForEvent(this);
            
            
            
        }

        return socket ;
    }

    @InfoMethod
    private void closeException( IOException exc ) { }

    @Transport
    public void close () {
        try {
            Selector selector = orb.getTransportManager().getSelector(0);
            selector.unregisterForEvent(this);
            if (serverSocketChannel != null) {
                serverSocketChannel.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            closeException(e);
        } finally {
        }
    }

    
    

    public SelectableChannel getChannel() {
        return serverSocketChannel;
    }

    
    
    
    

    protected void accept() {
        processSocket( getAcceptedSocket() ) ;
    }

    @Transport
    public void doWork() {
        try {
            if (selectionKey.isAcceptable()) {
                AccessController.doPrivileged(
                    new PrivilegedAction<Object>() {
                        public java.lang.Object run() {
                            accept() ;
                            return null;
                        }
                    }
                );
            } else {
                selectionKeyNotAcceptable() ;
            }
        } catch (SecurityException se) {
            securityException( se ) ;
            String permissionStr = ORBUtility.getClassSecurityInfo(getClass());
            wrapper.securityExceptionInAccept(se, permissionStr);
        } catch (Exception ex) {
            otherException( ex ) ;
            wrapper.exceptionInAccept(ex, ex.toString() );
        } catch (Throwable t) {
            otherException( t ) ;
        } finally {

            
            
            
            
            
            
            
            
            
            
            
            
            
            

            Selector selector = orb.getTransportManager().getSelector(0);
            selector.registerInterestOps(this);
        }
    }

    
    
    
    

    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }

    @InfoMethod
    private void selectionKeyNotAcceptable() { }

    @InfoMethod
    private void securityException(SecurityException se) { }

    @InfoMethod
    private void otherException(Throwable t) { }
    
}


