


package com.sun.corba.ee.impl.ior;

import org.omg.CORBA.ORB ;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion ;

import com.sun.corba.ee.spi.ior.TaggedComponent ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "Generic representation of a tagged component of a type "
    + "unknown to the ORB" ) 
public class GenericTaggedComponent extends GenericIdentifiable 
    implements TaggedComponent 
{
    public GenericTaggedComponent( int id, InputStream is ) 
    {
        super( id, is ) ;
    }

    public GenericTaggedComponent( int id, byte[] data ) 
    {
        super( id, data ) ;
    }
    
    
    public org.omg.IOP.TaggedComponent getIOPComponent( ORB orb ) 
    {
        return new org.omg.IOP.TaggedComponent( getId(), 
            getData() ) ;
    }
}
