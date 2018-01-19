

package com.sun.corba.se.spi.presentation.rmi ;

import java.lang.reflect.Method ;


public interface IDLNameTranslator
{
    
    Class[] getInterfaces() ;

    
    Method[] getMethods() ;

    
    Method getMethod( String idlName )  ;

    
    String getIDLName( Method method )  ;
}
