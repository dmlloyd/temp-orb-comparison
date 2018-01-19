


package com.sun.corba.ee.impl.copyobject ;

import java.rmi.Remote;

import org.omg.CORBA.ORB ;

import com.sun.corba.ee.impl.util.Utility;

import org.glassfish.pfl.dynamic.copyobject.impl.JavaStreamObjectCopierImpl ;


public class JavaStreamORBObjectCopierImpl extends JavaStreamObjectCopierImpl {
    private ORB orb ;

    public JavaStreamORBObjectCopierImpl( ORB orb ) {
        this.orb = orb ;
    }

    public Object copy(Object obj, boolean debug ) {
        return copy( obj ) ;
    }

    @Override
    public Object copy(Object obj) {
        if (obj instanceof Remote) {
            
            
            return Utility.autoConnect(obj,orb,true);
        }

        return super.copy( obj ) ;
    }
}
