


package com.sun.corba.ee.spi.protocol;


import com.sun.corba.ee.spi.ior.ObjectKey;

import com.sun.corba.ee.spi.ior.IOR ;


public abstract interface ServerRequestDispatcher
{
    
    public IOR locate(ObjectKey key);

    public void dispatch(MessageMediator messageMediator);
}



