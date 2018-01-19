


package com.sun.corba.ee.spi.servicecontext;

import com.sun.corba.ee.spi.servicecontext.ServiceContext ;

public interface UEInfoServiceContext extends ServiceContext
{
    int SERVICE_CONTEXT_ID = 9 ;

    Throwable getUE() ;
}

