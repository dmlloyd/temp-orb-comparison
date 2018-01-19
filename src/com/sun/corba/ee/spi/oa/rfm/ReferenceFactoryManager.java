


package com.sun.corba.ee.spi.oa.rfm ;

import java.util.Map ;
import java.util.List ;
import org.glassfish.pfl.basic.contain.Pair;

import org.omg.CORBA.Policy ;

import org.omg.PortableServer.ServantLocator ;


public interface ReferenceFactoryManager extends org.omg.CORBA.Object,
    org.omg.CORBA.portable.IDLEntity 
{
    public enum RFMState { READY, SUSPENDED }

    
    public RFMState getState();

    
    public void activate() ;

    
    public void suspend() ;

    
    public void resume() ;

    
    public ReferenceFactory create( String name, String repositoryId, List<Policy> policies,
        ServantLocator manager ) ;

    
    public ReferenceFactory find( String[] adapterName ) ;

    
    public ReferenceFactory find( String name ) ;

    
    public void restartFactories( Map<String,Pair<ServantLocator,List<Policy>>> updates ) ;

    
    public void restartFactories() ;

    
    public void restart( Map<String,Pair<ServantLocator,List<Policy>>> updates ) ;

    
    public void restart() ;

    public boolean isRfmName( String[] adapterName ) ;

}
