

package com.sun.corba.se.spi.transport;

import java.util.List;

import com.sun.corba.se.spi.ior.IOR;

public interface IORToSocketInfo
{
    
    public List getSocketInfo(IOR ior);
}


