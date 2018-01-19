


package com.sun.corba.ee.spi.orb;

public interface ClassCodeBaseHandler {
    
    String getCodeBase( Class<?> cls ) ;

    
    Class<?> loadClass( String codebase, String className ) ;
}
