


package com.sun.corba.ee.spi.protocol;

import com.sun.corba.ee.spi.resolver.Resolver ;


public interface InitialServerRequestDispatcher 
    extends ServerRequestDispatcher
{
    
    void init( Resolver resolver ) ;
}

