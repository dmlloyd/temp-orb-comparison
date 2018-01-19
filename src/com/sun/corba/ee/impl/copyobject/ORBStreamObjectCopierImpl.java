


package com.sun.corba.ee.impl.copyobject ;


import java.io.Serializable;
import java.rmi.Remote;

import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.CORBA.ORB ;

import com.sun.corba.ee.impl.util.Utility;
import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopier;

public class ORBStreamObjectCopierImpl implements ObjectCopier {

    public ORBStreamObjectCopierImpl( ORB orb ) 
    {
        this.orb = orb ;
    }

    public Object copy(Object obj, boolean debug) {
        return copy( obj ) ;
    }

    public Object copy(Object obj) {
        if (obj instanceof Remote) {
            
            
            return Utility.autoConnect(obj,orb,true);
        }

        OutputStream out = (OutputStream)orb.create_output_stream();
        out.write_value((Serializable)obj);
        InputStream in = (InputStream)out.create_input_stream();
        return in.read_value();
    }

    private ORB orb;
}
