


package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedComponent ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "The ORB type" ) 
public interface ORBTypeComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "The ORB type" ) 
    public int getORBType() ;
}
