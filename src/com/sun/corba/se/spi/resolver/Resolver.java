

package com.sun.corba.se.spi.resolver ;


public interface Resolver {
    
    org.omg.CORBA.Object resolve( String name ) ;

    
    java.util.Set list() ;
}
