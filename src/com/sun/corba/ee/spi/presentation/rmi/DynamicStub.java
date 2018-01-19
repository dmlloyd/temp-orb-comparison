


package com.sun.corba.ee.spi.presentation.rmi ;

import java.rmi.RemoteException ;

import org.omg.CORBA.portable.Delegate ;
import org.omg.CORBA.portable.OutputStream ;

import org.omg.CORBA.ORB ;


public interface DynamicStub extends org.omg.CORBA.Object
{
    
    void setDelegate( Delegate delegate ) ;

    
    Delegate getDelegate() ;

    
    ORB getORB() ;

    
    String[] getTypeIds() ; 

    
    void connect( ORB orb ) throws RemoteException ;

    boolean isLocal() ;

    OutputStream request( String operation, boolean responseExpected ) ;
}

