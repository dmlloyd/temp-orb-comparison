

package com.sun.corba.se.spi.ior.iiop;





public interface IIOPProfile extends TaggedProfile
{
    ORBVersion getORBVersion() ;

    
    java.lang.Object getServant() ;

    
    GIOPVersion getGIOPVersion() ;

    
    String getCodebase() ;
}
