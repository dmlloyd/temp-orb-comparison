
package com.sun.corba.se.spi.orb ;


public interface ParserData {
    public String  getPropertyName() ;

    public Operation getOperation() ;

    public String getFieldName() ;

    public Object getDefaultValue() ;

    public Object getTestValue() ;

    public void addToParser( PropertyParser parser ) ;

    public void addToProperties( Properties props ) ;
}
