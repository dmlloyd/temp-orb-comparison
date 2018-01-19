


package com.sun.corba.ee.impl.orb ;




public abstract class ObjectKeyCacheEntryBase implements ObjectKeyCacheEntry {
    private ObjectKey okey ;

    public ObjectKeyCacheEntryBase( ObjectKey okey ) {
        this.okey = okey ;
    }

    public ObjectKey getObjectKey() {
        return okey ;
    }
}

