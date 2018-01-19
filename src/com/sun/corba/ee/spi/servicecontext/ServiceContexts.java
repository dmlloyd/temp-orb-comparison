


package com.sun.corba.ee.spi.servicecontext;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;

import com.sun.corba.ee.spi.servicecontext.ServiceContext ;


public interface ServiceContexts {
    
    void write( OutputStream os, GIOPVersion gv ) ;
    
    
    public void put( ServiceContext sc ) ;

    
    public void delete( int scId ) ;

    
    public ServiceContext get( int scId) ;

    public ServiceContexts copy() ;
}

