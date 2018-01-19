


package com.sun.corba.ee.spi.servicecontext;

import com.sun.corba.ee.impl.encoding.CodeSetComponentInfo  ;

public interface CodeSetServiceContext extends ServiceContext {
    int SERVICE_CONTEXT_ID = 1 ;

    CodeSetComponentInfo.CodeSetContext getCodeSetContext() ;
}

