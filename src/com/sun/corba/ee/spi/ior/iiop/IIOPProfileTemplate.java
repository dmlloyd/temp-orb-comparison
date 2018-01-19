


package com.sun.corba.ee.spi.ior.iiop;





@ManagedData
@Description( "Template for an IIOP profile" )
public interface IIOPProfileTemplate extends TaggedProfileTemplate
{
    
    public GIOPVersion getGIOPVersion() ;

    
    @ManagedAttribute
    @Description( "The host and port of the IP address for the primary endpoint of this profile" )
    public IIOPAddress getPrimaryAddress()  ;
}
