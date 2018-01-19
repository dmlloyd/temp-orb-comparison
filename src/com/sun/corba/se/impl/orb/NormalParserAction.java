

package com.sun.corba.se.impl.orb ;

import java.util.Properties ;

import com.sun.corba.se.spi.orb.Operation ;

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
