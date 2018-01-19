

package com.sun.corba.se.impl.orbutil.closure ;


public class Constant implements Closure {
    private Object value ;

    public Constant( Object value )
    {
        this.value = value ;
    }

    public Object evaluate()
    {
        return value ;
    }
}
