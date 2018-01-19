


package com.sun.corba.ee.spi.ior;



public interface IdentifiableFactoryFinder<E extends Identifiable> 
{
    
    E create(int id, InputStream is);

    
    void registerFactory( IdentifiableFactory<E> factory ) ; 
}
