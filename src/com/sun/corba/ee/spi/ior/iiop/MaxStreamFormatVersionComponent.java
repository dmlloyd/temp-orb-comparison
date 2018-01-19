

package com.sun.corba.ee.spi.ior.iiop;





@ManagedData
@Description( "Component representing the maximum RMI-IIOP stream format " 
    + "version to be used with this IOR" )
public interface MaxStreamFormatVersionComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "The maximum RMI-IIOP stream format version "
        + "(usually 2)" ) 
    public byte getMaxStreamFormatVersion() ;
}
