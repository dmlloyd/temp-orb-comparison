

package com.sun.corba.se.spi.ior;



public interface IdentifiableFactoryFinder
{
    
    Identifiable create(int id, InputStream is);

    
    void registerFactory( IdentifiableFactory factory ) ;
}
