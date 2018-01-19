

package com.sun.corba.se.impl.orb ;


public interface ParserAction {
    
    String getPropertyName() ;

    
    boolean isPrefix() ;

    
    String getFieldName() ;

    
    Object apply( Properties props ) ;
}
