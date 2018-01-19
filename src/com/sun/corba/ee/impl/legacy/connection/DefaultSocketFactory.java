

package xxxx;





public class DefaultSocketFactory 
    implements 
        ORBSocketFactory
{
    private com.sun.corba.ee.spi.orb.ORB orb;
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public DefaultSocketFactory()
    {
    }

    public void setORB(com.sun.corba.ee.spi.orb.ORB orb)
    {
        this.orb = orb;
    }

    public ServerSocket createServerSocket(String type, int port)
        throws
            IOException
    {
        if (! type.equals(ORBSocketFactory.IIOP_CLEAR_TEXT)) {
            throw wrapper.defaultCreateServerSocketGivenNonIiopClearText( type ) ;
        }

        ServerSocket serverSocket;

        if (orb.getORBData().acceptorSocketType().equals(ORBConstants.SOCKETCHANNEL)) {
            ServerSocketChannel serverSocketChannel =
                ServerSocketChannel.open();
            serverSocket = serverSocketChannel.socket();
        } else {
            serverSocket = new ServerSocket();
        }
        serverSocket.bind(new InetSocketAddress(port));
        return serverSocket;
    }

    public SocketInfo getEndPointInfo(ORB orb,
                                        IOR ior,
                                        SocketInfo socketInfo)
    {
        IIOPProfileTemplate temp = 
            (IIOPProfileTemplate)ior.getProfile().getTaggedProfileTemplate() ;
        IIOPAddress primary = temp.getPrimaryAddress() ;

        return new EndPointInfoImpl(ORBSocketFactory.IIOP_CLEAR_TEXT,
                                    primary.getPort(),
                                    primary.getHost().toLowerCase());
    }

    public Socket createSocket(SocketInfo socketInfo)
        throws
            IOException,
            GetEndPointInfoAgainException
    {
        Socket socket;

        if (orb.getORBData().acceptorSocketType().equals(ORBConstants.SOCKETCHANNEL)) {
            InetSocketAddress address = 
                new InetSocketAddress(socketInfo.getHost(), 
                                      socketInfo.getPort());
            SocketChannel socketChannel = ORBUtility.openSocketChannel(address);
            socket = socketChannel.socket();
        } else {
            socket = new Socket(socketInfo.getHost(), 
                                socketInfo.getPort());
        }

        
        try {
            socket.setTcpNoDelay(true);
        } catch (Exception e) {
            wrapper.couldNotSetTcpNoDelay( e ) ;
        }
        return socket;
    }
}



