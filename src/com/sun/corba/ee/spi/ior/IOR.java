


package com.sun.corba.ee.spi.ior;

import java.util.List ;
import java.util.Iterator ;

import com.sun.corba.ee.spi.orb.ORBVersion ;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion ;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfile ;

import com.sun.corba.ee.spi.orb.ORB ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.InheritedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "Interoperable Object Reference: the internal structure of a remote object reference" )
public interface IOR extends List<TaggedProfile>, Writeable, MakeImmutable
{
    
    @ManagedAttribute
    @Description( "The list of profiles in this IOR" ) 
    Iterator<TaggedProfile> getTaggedProfiles() ;

    ORB getORB() ;

    
    @ManagedAttribute
    @Description( "The repository ID of the IOR" ) 
    String getTypeId() ;
   
    
    Iterator<TaggedProfile> iteratorById( int id ) ;

    
    String stringify() ;

    
    org.omg.IOP.IOR getIOPIOR() ;

    
    boolean isNil() ;

    
    boolean isEquivalent(IOR ior) ;

    
    IORTemplateList getIORTemplates() ;

    
    IIOPProfile getProfile() ;
}
