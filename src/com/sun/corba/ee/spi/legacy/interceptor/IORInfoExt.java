


package com.sun.corba.ee.spi.legacy.interceptor;


public interface IORInfoExt
{
    public int getServerPort(String type)
        throws
            UnknownType;

    public ObjectAdapter getObjectAdapter();
}


