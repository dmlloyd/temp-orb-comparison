


package com.sun.corba.ee.spi.transport;



public interface IORToSocketInfo
{
    
    public List<? extends SocketInfo> getSocketInfo(IOR ior, 
        List<? extends SocketInfo> previous);
}


