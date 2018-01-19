

package com.sun.corba.ee.spi.ior.iiop;



@ManagedData
@Description( "Component encoding request paritioning ID" )
public interface RequestPartitioningComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "Request paritioning id (0-63); commonly 0" ) 
    public int getRequestPartitioningId();
}
