


package com.sun.corba.ee.spi.ior;

import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;

import java.util.List ;
import java.util.Iterator ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.ior.Identifiable ;
import com.sun.corba.ee.spi.ior.Writeable ;
import com.sun.corba.ee.spi.ior.ObjectId ;
import com.sun.corba.ee.spi.ior.WriteContents ;

import com.sun.corba.ee.spi.orb.ORB ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.Description ;
import org.glassfish.gmbal.InheritedAttribute ;
import org.glassfish.gmbal.IncludeSubclass ;


@ManagedData
@Description( "A template for creating a TaggedProfile" ) 
@IncludeSubclass( { com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate.class } )
public interface TaggedProfileTemplate extends List<TaggedComponent>, 
    Identifiable, WriteContents, MakeImmutable
{    
    @ManagedAttribute
    @Description( "The list of TaggedComponents in this TaggedProfileTemplate" ) 
    public Iterator<TaggedComponent> getTaggedComponents() ;

    
    public Iterator<TaggedComponent> iteratorById( int id ) ;

    public <T extends TaggedComponent> Iterator<T> iteratorById( int id, 
        Class<T> cls )  ;

    
    TaggedProfile create( ObjectKeyTemplate oktemp, ObjectId id ) ;

    
    void write( ObjectKeyTemplate oktemp, ObjectId id, OutputStream os) ;

    
    boolean isEquivalent( TaggedProfileTemplate temp );

    
    org.omg.IOP.TaggedComponent[] getIOPComponents( 
        ORB orb, int id );
}
