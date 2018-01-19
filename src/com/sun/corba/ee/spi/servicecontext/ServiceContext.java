


package com.sun.corba.ee.spi.servicecontext;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;


public interface ServiceContext {
    public interface Factory {
        int getId() ;

        ServiceContext create( InputStream s, GIOPVersion gv ) ;        
    }

    int getId() ;

    void write(OutputStream s, GIOPVersion gv )  ;
}
