

package com.sun.corba.se.spi.protocol;






public interface CorbaServerRequestDispatcher
    extends ServerRequestDispatcher
{
    
    public IOR locate(ObjectKey key);
}


