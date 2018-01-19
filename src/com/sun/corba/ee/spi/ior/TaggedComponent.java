


package com.sun.corba.ee.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.ORB ;

import com.sun.corba.ee.spi.ior.iiop.AlternateIIOPAddressComponent ;
import com.sun.corba.ee.spi.ior.iiop.CodeSetsComponent ;                
import com.sun.corba.ee.spi.ior.iiop.JavaCodebaseComponent ;
import com.sun.corba.ee.spi.ior.iiop.MaxStreamFormatVersionComponent ;
import com.sun.corba.ee.spi.ior.iiop.ORBTypeComponent ;
import com.sun.corba.ee.spi.ior.iiop.RequestPartitioningComponent ;

import com.sun.corba.ee.impl.ior.GenericTaggedComponent ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.Description ;
import org.glassfish.gmbal.IncludeSubclass ;


@ManagedData
@Description( "Base class for all TaggedComponents" )
@IncludeSubclass( { AlternateIIOPAddressComponent.class, 
    CodeSetsComponent.class, JavaCodebaseComponent.class,
    MaxStreamFormatVersionComponent.class, ORBTypeComponent.class,
    RequestPartitioningComponent.class,
    GenericTaggedComponent.class } )
public interface TaggedComponent extends Identifiable
{
    org.omg.IOP.TaggedComponent getIOPComponent( ORB orb ) ;
}
