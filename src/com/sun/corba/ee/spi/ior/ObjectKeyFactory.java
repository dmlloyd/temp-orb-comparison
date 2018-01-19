


package com.sun.corba.ee.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.ee.spi.ior.ObjectKey ;
import com.sun.corba.ee.spi.ior.ObjectKeyTemplate ;


public interface ObjectKeyFactory 
{
    
    ObjectKey create( byte[] key ) ;

    
    ObjectKeyTemplate createTemplate( InputStream is ) ;
}
