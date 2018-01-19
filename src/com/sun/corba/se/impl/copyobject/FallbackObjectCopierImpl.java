

package com.sun.corba.se.impl.copyobject ;

import com.sun.corba.se.spi.copyobject.ObjectCopier ;
import com.sun.corba.se.spi.copyobject.ReflectiveCopyException ;


public class FallbackObjectCopierImpl implements ObjectCopier
{
    private ObjectCopier first ;
    private ObjectCopier second ;

    public FallbackObjectCopierImpl( ObjectCopier first,
        ObjectCopier second )
    {
        this.first = first ;
        this.second = second ;
    }

    public Object copy( Object src ) throws ReflectiveCopyException
    {
        try {
            return first.copy( src ) ;
        } catch (ReflectiveCopyException rce ) {
            
            return second.copy( src ) ;
        }
    }
}
