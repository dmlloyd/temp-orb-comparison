


package com.sun.corba.ee.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream ;


public interface IdentifiableFactoryFinder<E extends Identifiable> 
{
    
    E create(int id, InputStream is);

    
    void registerFactory( IdentifiableFactory<E> factory ) ; 
}
