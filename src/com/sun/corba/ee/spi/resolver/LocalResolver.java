


package com.sun.corba.ee.spi.resolver ;

import org.glassfish.pfl.basic.func.NullaryFunction;


public interface LocalResolver extends Resolver {
    
    void register( String name, NullaryFunction<org.omg.CORBA.Object> closure ) ;
}
