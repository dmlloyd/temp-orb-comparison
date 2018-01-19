


package com.sun.corba.ee.impl.orb ;

import com.sun.corba.ee.spi.orb.ObjectKeyCacheEntry ;

import com.sun.corba.ee.spi.ior.ObjectKey ;

import com.sun.corba.ee.spi.oa.ObjectAdapter ;

public class ObjectKeyCacheEntryNoObjectAdapterImpl extends ObjectKeyCacheEntryBase {
    public ObjectKeyCacheEntryNoObjectAdapterImpl( ObjectKey okey ) {
        super( okey ) ;
    }

    public ObjectAdapter getObjectAdapter() {
        return null ;
    }

    public void clearObjectAdapter() { }

    public void setObjectAdapter( ObjectAdapter oa ) { }
}
