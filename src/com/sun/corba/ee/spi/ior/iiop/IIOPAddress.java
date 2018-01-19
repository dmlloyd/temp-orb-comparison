


package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.Writeable ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "An IP address for the IIOP protocol" )
public interface IIOPAddress extends Writeable 
{
    @ManagedAttribute
    @Description( "The target host (name or IP address)" )
    public String getHost() ;

    @ManagedAttribute
    @Description( "The target port (0-65535)" )
    public int getPort() ;
}
