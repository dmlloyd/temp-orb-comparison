


package com.sun.corba.ee.impl.copyobject ;

import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopier;


public class ReferenceObjectCopierImpl implements ObjectCopier
{
    public Object copy( Object obj, boolean debug ) {
        return obj ;
    }

    public Object copy( Object obj )
    {
        return obj ;
    }
}

