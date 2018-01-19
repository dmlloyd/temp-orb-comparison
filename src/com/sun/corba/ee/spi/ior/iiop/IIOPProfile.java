


package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedProfile ;

import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.spi.orb.ORBVersion ;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.Description ;
import org.glassfish.gmbal.ManagedAttribute ;


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
