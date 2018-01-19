


package com.sun.corba.ee.impl.presentation.rmi ;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import org.omg.CORBA.portable.ApplicationException ;

public interface ExceptionHandler 
{
    
    boolean isDeclaredException( Class cls ) ;

    
    void writeException( OutputStream os, Exception ex ) ;

    
    Exception readException( ApplicationException ae ) ;
}
