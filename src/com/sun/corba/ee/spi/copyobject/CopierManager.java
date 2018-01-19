


package com.sun.corba.ee.spi.copyobject ;

import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopierFactory ;


public interface CopierManager
{
    
    void setDefaultId( int id ) ;

    
    int getDefaultId() ;

    ObjectCopierFactory getObjectCopierFactory( int id ) ;

    ObjectCopierFactory getDefaultObjectCopierFactory() ;

    
    void registerObjectCopierFactory( ObjectCopierFactory factory, int id ) ;
}
