

package com.sun.corba.se.spi.oa ;

import com.sun.corba.se.spi.oa.ObjectAdapter ;

import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.spi.ior.ObjectAdapterId ;

public interface ObjectAdapterFactory {
    
    void init( ORB orb ) ;

    
    void shutdown( boolean waitForCompletion ) ;

    
    ObjectAdapter find( ObjectAdapterId oaid ) ;

    ORB getORB() ;
}
