

package com.sun.corba.se.impl.transport;





public class DefaultSocketFactoryImpl
    implements ORBSocketFactory
{
    private ORB orb;
    private static final boolean keepAlive;

    static {
        keepAlive = java.security.AccessController.doPrivileged(
            new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run () {
                    String value =
                        System.getProperty("com.sun.CORBA.transport.enableTcpKeepAlive");
                    if (value != null)
                        return new Boolean(!"false".equalsIgnoreCase(value));

                    return Boolean.FALSE;
                }
            });
    }

    public void setORB(ORB orb)
    {
        this.orb = orb;
    }

    public ServerSocket createServerSocket(String type,
                                           InetSocketAddress inetSocketAddress)
        throws IOException
    {
        ServerSocketChannel serverSocketChannel = null;
        ServerSocket serverSocket = null;

        if (orb.getORBData().acceptorSocketType().equals(ORBConstants.SOCKETCHANNEL)) {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocket = serverSocketChannel.socket();
        } else {
            serverSocket = new ServerSocket();
        }
        serverSocket.bind(inetSocketAddress);
        return serverSocket;
    }

    public Socket createSocket(String type,
                               InetSocketAddress inetSocketAddress)
        throws IOException
    {
        SocketChannel socketChannel = null;
        Socket socket = null;

        if (orb.getORBData().connectionSocketType().equals(ORBConstants.SOCKETCHANNEL)) {
            socketChannel = SocketChannel.open(inetSocketAddress);
            socket = socketChannel.socket();
        } else {
            socket = new Socket(inetSocketAddress.getHostName(),
                                inetSocketAddress.getPort());
        }

        
        socket.setTcpNoDelay(true);

        if (keepAlive)
            socket.setKeepAlive(true);

        return socket;
    }

    public void setAcceptedSocketOptions(Acceptor acceptor,
                                         ServerSocket serverSocket,
                                         Socket socket)
        throws SocketException
    {
        
        socket.setTcpNoDelay(true);
        if (keepAlive)
            socket.setKeepAlive(true);
    }
}


