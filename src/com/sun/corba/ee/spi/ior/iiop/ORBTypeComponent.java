


package com.sun.corba.ee.spi.ior.iiop;




@ManagedData
@Description( "The ORB type" ) 
public interface ORBTypeComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "The ORB type" ) 
    public int getORBType() ;
}
