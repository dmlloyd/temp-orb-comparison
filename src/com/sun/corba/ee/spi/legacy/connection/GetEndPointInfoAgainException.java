


package com.sun.corba.ee.spi.legacy.connection;

import com.sun.corba.ee.spi.transport.SocketInfo;



public class GetEndPointInfoAgainException
    extends Exception
{
    private SocketInfo socketInfo;

    public GetEndPointInfoAgainException(SocketInfo socketInfo)
    {
        this.socketInfo = socketInfo;
    }

    public SocketInfo getEndPointInfo()
    {
        return socketInfo;
    }
}
