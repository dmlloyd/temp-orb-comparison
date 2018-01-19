

package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.se.spi.ior.ObjectKey ;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate ;


public interface ObjectKeyFactory
{
    
    ObjectKey create( byte[] key ) ;

    
    ObjectKeyTemplate createTemplate( InputStream is ) ;
}
