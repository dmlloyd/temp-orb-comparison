


package com.sun.corba.ee.spi.legacy.interceptor;

import com.sun.corba.ee.spi.oa.ObjectAdapter;

public interface IORInfoExt
{
    public int getServerPort(String type)
        throws
            UnknownType;

    public ObjectAdapter getObjectAdapter();
}


