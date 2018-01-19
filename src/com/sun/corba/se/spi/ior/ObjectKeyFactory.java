

package com.sun.corba.se.spi.ior;




public interface ObjectKeyFactory
{
    
    ObjectKey create( byte[] key ) ;

    
    ObjectKeyTemplate createTemplate( InputStream is ) ;
}
