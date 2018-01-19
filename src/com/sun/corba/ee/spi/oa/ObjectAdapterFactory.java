


package com.sun.corba.ee.spi.oa ;

import com.sun.corba.ee.spi.orb.ORB ;

import com.sun.corba.ee.spi.ior.ObjectAdapterId ;

public interface ObjectAdapterFactory {
    
    void init( ORB orb ) ;

    
    void shutdown( boolean waitForCompletion ) ;

    
    ObjectAdapter find( ObjectAdapterId oaid ) ;

    ORB getORB() ;
}
