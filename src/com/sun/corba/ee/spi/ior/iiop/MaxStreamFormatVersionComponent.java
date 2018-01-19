

package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedComponent ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;



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
