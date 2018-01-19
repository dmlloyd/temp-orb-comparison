


package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.iiop.IIOPAddress ;
import com.sun.corba.ee.spi.ior.TaggedComponent ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "Component containing an alternate IIOP address to use" )
public interface AlternateIIOPAddressComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "The Alternate address" ) 
    public IIOPAddress getAddress() ;
}
