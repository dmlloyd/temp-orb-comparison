


package com.sun.corba.ee.spi.ior ;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.ee.spi.ior.Identifiable ;

import com.sun.corba.ee.spi.orb.ORB ;


public interface IdentifiableFactory<E extends Identifiable> {
    
    public int getId() ;

    
    public E create( ORB orb, InputStream in ) ;
}
