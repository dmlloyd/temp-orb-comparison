


package com.sun.corba.ee.spi.orb ;

import com.sun.corba.ee.spi.ior.ObjectKey ;

import com.sun.corba.ee.spi.oa.ObjectAdapter ;


public interface ObjectKeyCacheEntry {
    ObjectKey getObjectKey() ;

    ObjectAdapter getObjectAdapter() ;

    void clearObjectAdapter() ;

    void setObjectAdapter( ObjectAdapter oa ) ;
}
