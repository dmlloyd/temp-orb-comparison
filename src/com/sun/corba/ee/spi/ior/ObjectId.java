


package com.sun.corba.ee.spi.ior;



@ManagedData
@Description( "The ObjectId field within an ObjectKey in an IOR" )
public interface ObjectId extends Writeable
{
    @ManagedAttribute( id = "Id" ) 
    @Description( "The actual bytes in the ObjectKey" ) 
    String getIdString() ;

    public byte[] getId() ;
}
