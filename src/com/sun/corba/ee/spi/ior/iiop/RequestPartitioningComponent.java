

package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedComponent;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;

@ManagedData
@Description( "Component encoding request paritioning ID" )
public interface RequestPartitioningComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "Request paritioning id (0-63); commonly 0" ) 
    public int getRequestPartitioningId();
}
