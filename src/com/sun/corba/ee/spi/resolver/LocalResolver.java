


package com.sun.corba.ee.spi.resolver ;



public interface LocalResolver extends Resolver {
    
    void register( String name, NullaryFunction<org.omg.CORBA.Object> closure ) ;
}
