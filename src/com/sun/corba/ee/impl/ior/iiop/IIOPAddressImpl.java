


package com.sun.corba.ee.impl.ior.iiop;





public final class IIOPAddressImpl extends IIOPAddressBase
{
    private static final IORSystemException wrapper =
        IORSystemException.self ;

    private String host;
    private int port;
    
    public IIOPAddressImpl( String host, int port ) 
    {
        if ((port < 0) || (port > 65535)) {
            throw wrapper.badIiopAddressPort(port);
        }

        this.host = host ;
        this.port = port ;
    }

    public IIOPAddressImpl( InputStream is )
    {
        host = is.read_string() ;
        short thePort = is.read_short() ;
        port = shortToInt( thePort ) ;
    }

    public String getHost()
    {
        return host ;
    }

    public int getPort()
    {
        return port ;
    }
}
