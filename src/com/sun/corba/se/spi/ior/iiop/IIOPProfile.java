

package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedProfile ;

import com.sun.corba.se.spi.orb.ORB ;
import com.sun.corba.se.spi.orb.ORBVersion ;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion ;


public interface IIOPProfile extends TaggedProfile
{
    ORBVersion getORBVersion() ;

    
    java.lang.Object getServant() ;

    
    GIOPVersion getGIOPVersion() ;

    
    String getCodebase() ;
}
