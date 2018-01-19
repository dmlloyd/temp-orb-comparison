


package com.sun.corba.ee.impl.orb ;




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
