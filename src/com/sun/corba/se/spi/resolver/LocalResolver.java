

package com.sun.corba.se.spi.resolver ;



public interface LocalResolver extends Resolver {
    
    void register( String name, Closure closure ) ;
}
