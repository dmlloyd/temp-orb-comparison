

package com.sun.corba.se.spi.ior ;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.se.spi.ior.Identifiable ;


public interface IdentifiableFactory {
    
    public int getId() ;

    
    public Identifiable create( InputStream in ) ;
}
