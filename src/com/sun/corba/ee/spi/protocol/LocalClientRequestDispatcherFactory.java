


package com.sun.corba.ee.spi.protocol;

import com.sun.corba.ee.spi.ior.IOR ;

public interface LocalClientRequestDispatcherFactory {
    public LocalClientRequestDispatcher create( int id, IOR ior )  ;
}
    
