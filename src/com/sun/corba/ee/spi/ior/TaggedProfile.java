


package com.sun.corba.ee.spi.ior;

import com.sun.corba.ee.spi.orb.ORB ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.IncludeSubclass ;
import org.glassfish.gmbal.Description ;
import org.glassfish.gmbal.ManagedAttribute ;


@ManagedData
@Description( "A TaggedProfile contained in an IOR" )
@IncludeSubclass( { com.sun.corba.ee.spi.ior.iiop.IIOPProfile.class } ) 
public interface TaggedProfile extends Identifiable, MakeImmutable
{
    @ManagedAttribute
    @Description( "Template for this TaggedProfile" ) 
    TaggedProfileTemplate getTaggedProfileTemplate() ;

    @ManagedAttribute
    @Description( "The ObjectId used in the IIOPProfile in this IOR" )
    ObjectId getObjectId() ;

    @ManagedAttribute
    @Description( "The template for the ObjectKey in the IIOPProfile in this IOR" ) 
    ObjectKeyTemplate getObjectKeyTemplate() ;

    ObjectKey getObjectKey() ;

    
    boolean isEquivalent( TaggedProfile prof ) ;

    
    org.omg.IOP.TaggedProfile getIOPProfile();

    
    boolean isLocal() ;
}
