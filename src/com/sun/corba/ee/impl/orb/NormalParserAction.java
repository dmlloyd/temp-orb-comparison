


package com.sun.corba.ee.impl.orb ;

import java.util.Properties ;

import com.sun.corba.ee.spi.orb.Operation ;

public class NormalParserAction extends ParserActionBase {
    public NormalParserAction( String propertyName, 
        Operation operation, String fieldName )
    {
        super( propertyName, false, operation, fieldName ) ;
    }

    
    public Object apply( Properties props ) 
    {
        Object value = props.getProperty( getPropertyName() ) ;
        if (value != null)
            return getOperation().operate( value ) ;
        else 
            return null ;
    }
}

