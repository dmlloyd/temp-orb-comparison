

package com.sun.corba.se.spi.orbutil.proxy ;


public interface InvocationHandlerFactory
{
    
    InvocationHandler getInvocationHandler() ;

    
    Class[] getProxyInterfaces() ;
}
