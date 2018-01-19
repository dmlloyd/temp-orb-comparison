


package com.sun.corba.ee.spi.protocol;

import org.omg.CORBA.BAD_PARAM ;

import com.sun.corba.ee.impl.misc.ORBUtility ;

import com.sun.corba.ee.spi.ior.IOR ;

import com.sun.corba.ee.spi.orb.ORB ;


public class ForwardException extends RuntimeException {
    private ORB orb ;
    private org.omg.CORBA.Object obj;
    private IOR ior ;

    public ForwardException( ORB orb, IOR ior ) {
        super();

        this.orb = orb ;
        this.obj = null ;
        this.ior = ior ;
    }

    public ForwardException( ORB orb, org.omg.CORBA.Object obj) {
        super();

        
        
        
        
        if (obj instanceof org.omg.CORBA.LocalObject)
            throw new BAD_PARAM() ;

        this.orb = orb ;
        this.obj = obj ;
        this.ior = null ;
    }

    public synchronized org.omg.CORBA.Object getObject()
    {
        if (obj == null) {
            obj = ORBUtility.makeObjectReference( ior ) ;
        }

        return obj ;
    }

    public synchronized IOR getIOR() 
    {
        if (ior == null) {
            ior = orb.getIOR( obj, false ) ;
        }

        return ior ;
    }
}
