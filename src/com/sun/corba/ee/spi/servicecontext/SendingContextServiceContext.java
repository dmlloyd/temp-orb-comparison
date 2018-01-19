


package com.sun.corba.ee.spi.servicecontext;

import com.sun.corba.ee.spi.ior.IOR ;

import com.sun.corba.ee.spi.servicecontext.ServiceContext ;

public interface SendingContextServiceContext extends ServiceContext
{
    int SERVICE_CONTEXT_ID = 6 ;

    IOR getIOR() ;
}
