

package com.sun.corba.se.impl.oa.toa ;



public interface TOA extends ObjectAdapter {
    
    void connect( org.omg.CORBA.Object servant ) ;

    
    void disconnect( org.omg.CORBA.Object obj ) ;
}
