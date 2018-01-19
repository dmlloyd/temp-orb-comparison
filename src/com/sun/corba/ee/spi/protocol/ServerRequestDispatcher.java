


package com.sun.corba.ee.spi.protocol;





public abstract interface ServerRequestDispatcher
{
    
    public IOR locate(ObjectKey key);

    public void dispatch(MessageMediator messageMediator);
}



