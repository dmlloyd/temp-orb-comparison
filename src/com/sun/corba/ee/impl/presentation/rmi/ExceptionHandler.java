


package com.sun.corba.ee.impl.presentation.rmi ;



public interface ExceptionHandler 
{
    
    boolean isDeclaredException( Class cls ) ;

    
    void writeException( OutputStream os, Exception ex ) ;

    
    Exception readException( ApplicationException ae ) ;
}
