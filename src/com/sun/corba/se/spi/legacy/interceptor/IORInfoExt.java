

package com.sun.corba.se.spi.legacy.interceptor;


public interface IORInfoExt
{
    public int getServerPort(String type)
        throws
            UnknownType;

    public ObjectAdapter getObjectAdapter();
}


