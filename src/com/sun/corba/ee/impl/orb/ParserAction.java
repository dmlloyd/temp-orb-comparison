


package com.sun.corba.ee.impl.orb ;

import java.util.Properties ;

public interface ParserAction {
    
    String getPropertyName() ;

    
    boolean isPrefix() ;

    
    String getFieldName() ;

    
    Object apply( Properties props ) ;
}
