


package com.sun.corba.ee.impl.copyobject ;

import com.sun.corba.ee.spi.copyobject.CopierManager;
import org.glassfish.pfl.basic.contain.DenseIntMapImpl;
import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopierFactory;

public class CopierManagerImpl implements CopierManager
{
    private int defaultId ;
    private DenseIntMapImpl<ObjectCopierFactory> map ;

    public CopierManagerImpl()
    {
        defaultId = 0 ;
        map = new DenseIntMapImpl<ObjectCopierFactory>() ;
    }

    public void setDefaultId( int id ) 
    {
        defaultId = id ;
    }

    public int getDefaultId() 
    {
        return defaultId ;
    }

    public ObjectCopierFactory getObjectCopierFactory( int id ) 
    {
        return map.get( id ) ;
    }

    public ObjectCopierFactory getDefaultObjectCopierFactory()
    {
        return map.get( defaultId ) ;
    }

    public void registerObjectCopierFactory( ObjectCopierFactory factory, int id ) 
    {
        map.set( id, factory ) ;
    }
}

