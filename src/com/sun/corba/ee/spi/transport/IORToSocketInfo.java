


package com.sun.corba.ee.spi.transport;

import java.util.List;

import com.sun.corba.ee.spi.ior.IOR;

public interface IORToSocketInfo
{
    
    public List<? extends SocketInfo> getSocketInfo(IOR ior, 
        List<? extends SocketInfo> previous);
}


