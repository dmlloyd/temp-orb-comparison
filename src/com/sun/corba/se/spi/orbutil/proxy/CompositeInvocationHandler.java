

package com.sun.corba.se.spi.orbutil.proxy ;

import java.io.Serializable ;
import java.lang.reflect.InvocationHandler ;

public interface CompositeInvocationHandler extends InvocationHandler,
    Serializable
{
    
    void addInvocationHandler( Class interf, InvocationHandler handler ) ;

    
    void setDefaultHandler( InvocationHandler handler ) ;
}
