

package com.sun.corba.se.spi.copyobject ;


public interface CopierManager
{
    
    void setDefaultId( int id ) ;

    
    int getDefaultId() ;

    ObjectCopierFactory getObjectCopierFactory( int id ) ;

    ObjectCopierFactory getDefaultObjectCopierFactory() ;

    
    void registerObjectCopierFactory( ObjectCopierFactory factory, int id ) ;
}
