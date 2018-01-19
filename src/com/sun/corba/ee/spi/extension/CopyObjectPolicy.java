


package com.sun.corba.ee.spi.extension ;

import org.omg.CORBA.Policy ;
import org.omg.CORBA.LocalObject ;
import com.sun.corba.ee.spi.misc.ORBConstants ;


public class CopyObjectPolicy extends LocalObject implements Policy
{
    private final int value ;

    public CopyObjectPolicy( int value ) 
    {
        this.value = value ;
    }

    public int getValue()
    {
        return value ;
    }

    public int policy_type ()
    {
        return ORBConstants.COPY_OBJECT_POLICY ;
    }

    public org.omg.CORBA.Policy copy ()
    {
        return this ;
    }

    public void destroy ()
    {
        
    }

    public String toString() 
    {
        return "CopyObjectPolicy[" + value + "]" ;
    }
}
