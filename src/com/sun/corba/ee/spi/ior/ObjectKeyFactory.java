


package com.sun.corba.ee.spi.ior;




public interface ObjectKeyFactory 
{
    
    ObjectKey create( byte[] key ) ;

    
    ObjectKeyTemplate createTemplate( InputStream is ) ;
}
