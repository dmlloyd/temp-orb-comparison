


package com.sun.corba.ee.spi.transport;



public interface InboundConnectionCache extends ConnectionCache
{
    public Connection get(Acceptor acceptor); 

    public void put(Acceptor acceptor, Connection connection);

    public void remove( Connection connection ) ;
}



