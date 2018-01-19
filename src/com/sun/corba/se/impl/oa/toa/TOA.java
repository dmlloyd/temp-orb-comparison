

package com.sun.corba.se.impl.oa.toa ;

import com.sun.corba.se.spi.oa.ObjectAdapter ;


public interface TOA extends ObjectAdapter {
    
    void connect( org.omg.CORBA.Object servant ) ;

    
    void disconnect( org.omg.CORBA.Object obj ) ;
}
