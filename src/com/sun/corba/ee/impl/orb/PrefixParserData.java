


package com.sun.corba.ee.impl.orb ;



public class PrefixParserData extends ParserDataBase {

    private Pair<String,String>[] testData ;
    private Class componentType ;

    public PrefixParserData( String  propertyName,
        Operation operation, String fieldName, Object defaultValue,
        Object testValue, Pair<String,String>[] testData, Class componentType )
    {
        super( propertyName, operation, fieldName, defaultValue, testValue ) ;
        this.testData = testData ;
        this.componentType = componentType ;
    }

    public void addToParser( PropertyParser parser ) 
    {
        parser.addPrefix( getPropertyName(), getOperation(), getFieldName(), 
            componentType ) ;
    }

    public void addToProperties( Properties props ) 
    {
        for (Pair<String,String> sp : testData) {
            String propName = getPropertyName() ;
            if (propName.charAt( propName.length() - 1 ) != '.')
                propName += "." ;

            props.setProperty( propName + sp.first(), sp.second() ) ;
        }
    }
}
