

package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.pept.protocol.ServerRequestDispatcher;

import com.sun.corba.se.spi.ior.ObjectKey;


import com.sun.corba.se.spi.ior.IOR ;


public interface CorbaServerRequestDispatcher
    extends ServerRequestDispatcher
{
    
    public IOR locate(ObjectKey key);
}


