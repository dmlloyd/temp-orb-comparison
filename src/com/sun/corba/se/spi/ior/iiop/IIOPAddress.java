

package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.Writeable ;


public interface IIOPAddress extends Writeable
{
    public String getHost() ;

    public int getPort() ;
}
