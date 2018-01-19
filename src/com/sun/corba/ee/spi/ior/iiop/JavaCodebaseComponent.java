


package com.sun.corba.ee.spi.ior.iiop;




@ManagedData
@Description( "Component representing Codebase URLs for downloading code" )
public interface JavaCodebaseComponent extends TaggedComponent 
{
    @ManagedAttribute
    @Description( "List of URLs in the codebase" ) 
    public String getURLs() ;
}
