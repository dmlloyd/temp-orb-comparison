


package com.sun.corba.ee.spi.presentation.rmi ;



public interface IDLNameTranslator 
{
    
    Class[] getInterfaces() ;

    
    Method[] getMethods() ;

    
    Method getMethod( String idlName )  ;

    
    String getIDLName( Method method )  ;
}

