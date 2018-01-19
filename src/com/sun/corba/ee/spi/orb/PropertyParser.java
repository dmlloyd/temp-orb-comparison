

package com.sun.corba.ee.spi.orb ;

import java.util.List ;
import java.util.LinkedList ;
import java.util.Map ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.Properties ;

import com.sun.corba.ee.impl.orb.ParserAction ;
import com.sun.corba.ee.impl.orb.ParserActionFactory ;

public class PropertyParser {
    private List<ParserAction> actions ;

    public PropertyParser( ) 
    {
        actions = new LinkedList<ParserAction>() ;
    }

    public PropertyParser add( String propName, 
        Operation action, String fieldName )
    {
        actions.add( ParserActionFactory.makeNormalAction( propName, 
            action, fieldName ) ) ;
        return this ;
    }

    public PropertyParser addPrefix( String propName, 
        Operation action, String fieldName, Class<?> componentType )
    {
        actions.add( ParserActionFactory.makePrefixAction( propName, 
            action, fieldName, componentType ) ) ;
        return this ;
    }

    
    public Map<String,Object> parse( Properties props )
    {
        Map<String,Object> map = new HashMap<String,Object>() ;
        Iterator<ParserAction> iter = actions.iterator() ;
        while (iter.hasNext()) {
            ParserAction act = iter.next() ;
            Object result = act.apply( props ) ; 
                
            
            
            if (result != null) {
                map.put(act.getFieldName(), result);
            }
        }

        return map ;
    }

    public Iterator<ParserAction> iterator()
    {
        return actions.iterator() ;
    }
}
