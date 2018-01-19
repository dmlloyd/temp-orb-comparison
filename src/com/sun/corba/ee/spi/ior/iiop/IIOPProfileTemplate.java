


package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedProfileTemplate ;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "Template for an IIOP profile" )
public interface IIOPProfileTemplate extends TaggedProfileTemplate
{
    
    public GIOPVersion getGIOPVersion() ;

    
    @ManagedAttribute
    @Description( "The host and port of the IP address for the primary endpoint of this profile" )
    public IIOPAddress getPrimaryAddress()  ;
}
