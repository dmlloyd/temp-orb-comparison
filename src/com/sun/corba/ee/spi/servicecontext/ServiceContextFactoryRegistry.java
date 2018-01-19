


package com.sun.corba.ee.spi.servicecontext;

import com.sun.corba.ee.spi.servicecontext.ServiceContext ;

public interface ServiceContextFactoryRegistry {

    public void register( ServiceContext.Factory factory ) ;

    public ServiceContext.Factory find( int scId ) ;
}
