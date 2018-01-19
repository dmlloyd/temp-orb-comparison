

package com.sun.corba.se.spi.presentation.rmi ;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.portable.ApplicationException ;

import java.lang.reflect.Method ;

import java.rmi.RemoteException ;

import com.sun.corba.se.spi.orb.ORB ;


public interface DynamicMethodMarshaller
{
    
    Method getMethod() ;

    
    Object[] copyArguments( Object[] args, ORB orb ) throws RemoteException ;

    
    Object[] readArguments( InputStream is ) ;

    
    void writeArguments( OutputStream os, Object[] args ) ;

    
    Object copyResult( Object result, ORB orb ) throws RemoteException ;

    
    Object readResult( InputStream is ) ;

    
    void writeResult( OutputStream os, Object result ) ;

    
    boolean isDeclaredException( Throwable thr ) ;

    
    void writeException( OutputStream os, Exception ex ) ;

    
    Exception readException( ApplicationException ae ) ;
}
