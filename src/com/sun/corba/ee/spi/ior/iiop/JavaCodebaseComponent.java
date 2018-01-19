


package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedComponent ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "Component representing Codebase URLs for downloading code" )
public interface JavaCodebaseComponent extends TaggedComponent 
{
    @ManagedAttribute
    @Description( "List of URLs in the codebase" ) 
    public String getURLs() ;
}
