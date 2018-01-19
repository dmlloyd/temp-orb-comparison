

package com.sun.corba.se.org.omg.CORBA ;

import org.omg.CORBA.ORBPackage.InvalidName ;


abstract public class ORB extends org.omg.CORBA_2_3.ORB
{
    
    public void register_initial_reference( String id,
                                            org.omg.CORBA.Object obj )
        throws InvalidName
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
