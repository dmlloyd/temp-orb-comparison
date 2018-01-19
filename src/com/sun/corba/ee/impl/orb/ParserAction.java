


package com.sun.corba.ee.impl.orb ;


public interface ParserAction {
    
    String getPropertyName() ;

    
    boolean isPrefix() ;

    
    String getFieldName() ;

    
    Object apply( Properties props ) ;
}
