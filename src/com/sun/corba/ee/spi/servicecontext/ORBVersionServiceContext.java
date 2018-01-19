


package com.sun.corba.ee.spi.servicecontext;

import com.sun.corba.ee.spi.orb.ORBVersion ;

import com.sun.corba.ee.spi.misc.ORBConstants ;

public interface ORBVersionServiceContext extends ServiceContext {
    int SERVICE_CONTEXT_ID = ORBConstants.TAG_ORB_VERSION ;

    ORBVersion getVersion() ;
}
