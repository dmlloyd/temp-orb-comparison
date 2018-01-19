

package com.sun.corba.se.spi.orbutil.proxy ;



public interface LinkedInvocationHandler extends InvocationHandler
{
    void setProxy( Proxy proxy ) ;

    Proxy getProxy() ;
}
