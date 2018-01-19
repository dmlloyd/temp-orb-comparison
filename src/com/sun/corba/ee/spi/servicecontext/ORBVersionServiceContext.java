


package com.sun.corba.ee.spi.servicecontext;



public interface ORBVersionServiceContext extends ServiceContext {
    int SERVICE_CONTEXT_ID = ORBConstants.TAG_ORB_VERSION ;

    ORBVersion getVersion() ;
}
