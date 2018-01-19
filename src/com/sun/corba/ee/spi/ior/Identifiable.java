



package com.sun.corba.ee.spi.ior;

import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


public interface Identifiable extends Writeable
{
    
    @ManagedAttribute
    @Description( "Id of tagged component or profile" )
    public int getId();
}
