


package com.sun.corba.ee.spi.servicecontext;


public interface ServiceContextFactoryRegistry {

    public void register( ServiceContext.Factory factory ) ;

    public ServiceContext.Factory find( int scId ) ;
}
