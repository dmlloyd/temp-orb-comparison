


package xxxx;






public class PrefixParserAction extends ParserActionBase {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private Class componentType ;

    public PrefixParserAction( String propertyName, 
        Operation operation, String fieldName, Class componentType )
    {
        super( propertyName, true, operation, fieldName ) ;
        this.componentType = componentType ;
    }

    
    public Object apply( Properties props ) 
    {
        String prefix = getPropertyName() ;
        int prefixLength = prefix.length() ;
        if (prefix.charAt( prefixLength - 1 ) != '.') {
            prefix += '.' ;
            prefixLength++ ;
        }
            
        List matches = new LinkedList() ;

        
        Iterator iter = props.keySet().iterator() ;
        while (iter.hasNext()) {
            String key = (String)(iter.next()) ;
            if (key.startsWith( prefix )) {
                String suffix = key.substring( prefixLength ) ;
                String value = props.getProperty( key ) ;
                Pair<String,String> data = new Pair<String,String>( suffix, value ) ;
                Object result = getOperation().operate( data ) ;
                matches.add( result ) ;
            }
        }

        int size = matches.size() ;
        if (size > 0) {
            
            
            
            
            Object result = null ;
            try {
                result = Array.newInstance( componentType, size ) ;
            } catch (Throwable thr) {
                throw wrapper.couldNotCreateArray( thr,
                    getPropertyName(), componentType, size ) ;
            }

            Iterator iter2 = matches.iterator() ;
            int ctr = 0 ;
            while (iter2.hasNext()) {
                Object obj = iter2.next() ;

                try {
                    Array.set( result, ctr, obj ) ;
                } catch (Throwable thr) {
                    throw wrapper.couldNotSetArray( thr,
                        getPropertyName(), ctr, componentType, size,
                        obj ) ;
                }
                ctr++ ;
            }

            return result ;
        } else {
            return null;
        }
    }
}
