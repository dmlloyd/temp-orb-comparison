

package com.sun.corba.se.spi.ior;

import java.util.List ;
import java.util.Iterator ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.ior.Identifiable ;
import com.sun.corba.se.spi.ior.Writeable ;
import com.sun.corba.se.spi.ior.ObjectId ;
import com.sun.corba.se.spi.ior.WriteContents ;

import com.sun.corba.se.spi.orb.ORB ;


public interface TaggedProfileTemplate extends List, Identifiable,
    WriteContents, MakeImmutable
{
    
    public Iterator iteratorById( int id ) ;

    
    TaggedProfile create( ObjectKeyTemplate oktemp, ObjectId id ) ;

    
    void write( ObjectKeyTemplate oktemp, ObjectId id, OutputStream os) ;

    
    boolean isEquivalent( TaggedProfileTemplate temp );

    
    org.omg.IOP.TaggedComponent[] getIOPComponents(
        ORB orb, int id );
}
