

package com.sun.corba.se.spi.ior ;




public interface IdentifiableFactory {
    
    public int getId() ;

    
    public Identifiable create( InputStream in ) ;
}
