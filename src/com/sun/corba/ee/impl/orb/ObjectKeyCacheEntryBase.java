


package com.sun.corba.ee.impl.orb ;

import com.sun.corba.ee.spi.orb.ObjectKeyCacheEntry ;

import com.sun.corba.ee.spi.ior.ObjectKey ;

import com.sun.corba.ee.spi.oa.ObjectAdapter ;

public abstract class ObjectKeyCacheEntryBase implements ObjectKeyCacheEntry {
    private ObjectKey okey ;

    public ObjectKeyCacheEntryBase( ObjectKey okey ) {
        this.okey = okey ;
    }

    public ObjectKey getObjectKey() {
        return okey ;
    }
}

