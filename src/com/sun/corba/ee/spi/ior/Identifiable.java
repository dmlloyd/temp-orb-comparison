



package com.sun.corba.ee.spi.ior;



public interface Identifiable extends Writeable
{
    
    @ManagedAttribute
    @Description( "Id of tagged component or profile" )
    public int getId();
}
