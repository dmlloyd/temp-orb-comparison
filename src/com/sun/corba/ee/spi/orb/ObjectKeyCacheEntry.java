


package com.sun.corba.ee.spi.orb ;




public interface ObjectKeyCacheEntry {
    ObjectKey getObjectKey() ;

    ObjectAdapter getObjectAdapter() ;

    void clearObjectAdapter() ;

    void setObjectAdapter( ObjectAdapter oa ) ;
}
