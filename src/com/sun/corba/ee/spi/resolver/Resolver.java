

package com.sun.corba.ee.spi.resolver ;


public interface Resolver {
    
    org.omg.CORBA.Object resolve( String name ) ;

    
    java.util.Set<String> list() ;
}
