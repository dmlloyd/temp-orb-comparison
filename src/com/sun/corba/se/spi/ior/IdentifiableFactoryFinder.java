

package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream ;


public interface IdentifiableFactoryFinder
{
    
    Identifiable create(int id, InputStream is);

    
    void registerFactory( IdentifiableFactory factory ) ;
}
