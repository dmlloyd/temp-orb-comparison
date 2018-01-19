


package com.sun.corba.ee.spi.protocol;

import java.util.Set;

import com.sun.corba.ee.spi.protocol.ServerRequestDispatcher ;
import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcherFactory ;

import com.sun.corba.ee.spi.oa.ObjectAdapterFactory ;


public interface RequestDispatcherRegistry {

    
    void registerClientRequestDispatcher( ClientRequestDispatcher csc, int scid) ;

    
    ClientRequestDispatcher getClientRequestDispatcher( int scid ) ;

    
    void registerLocalClientRequestDispatcherFactory( LocalClientRequestDispatcherFactory csc, int scid) ;

    
    LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory( int scid ) ;

    
    void registerServerRequestDispatcher( ServerRequestDispatcher ssc, int scid) ;

    
    ServerRequestDispatcher getServerRequestDispatcher(int scid) ;

    
    void registerServerRequestDispatcher( ServerRequestDispatcher ssc, String name ) ;

    
    ServerRequestDispatcher getServerRequestDispatcher( String name ) ;

    
    void registerObjectAdapterFactory( ObjectAdapterFactory oaf, int scid) ;

    
    ObjectAdapterFactory getObjectAdapterFactory( int scid ) ;

    
    Set<ObjectAdapterFactory> getObjectAdapterFactories() ;
}
