


package com.sun.corba.ee.impl.oa.toa ;

import com.sun.corba.ee.spi.oa.ObjectAdapter ;


public interface TOA extends ObjectAdapter {
    
    void connect( org.omg.CORBA.Object servant ) ;

    
    void disconnect( org.omg.CORBA.Object obj ) ;
}

