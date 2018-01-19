


package com.sun.corba.ee.spi.ior.iiop;






@ManagedData
@Description( "The IIOPProfile version of a TaggedProfile" )
public interface IIOPProfile extends TaggedProfile
{
    @ManagedAttribute
    @Description( "The ORB version in use" ) 
    ORBVersion getORBVersion() ;

    
    java.lang.Object getServant() ;

    
    GIOPVersion getGIOPVersion() ;

    
    String getCodebase() ;
}
