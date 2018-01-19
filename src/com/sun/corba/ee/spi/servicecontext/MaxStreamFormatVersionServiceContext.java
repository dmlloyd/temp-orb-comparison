


package com.sun.corba.ee.spi.servicecontext;

import org.omg.IOP.RMICustomMaxStreamFormat;

public interface MaxStreamFormatVersionServiceContext extends ServiceContext {
    int SERVICE_CONTEXT_ID = RMICustomMaxStreamFormat.value ;

    byte getMaximumStreamFormatVersion() ;
}
