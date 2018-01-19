


package com.sun.corba.ee.spi.transport.connection;



public interface ContactInfo<C extends Connection> {
    
    C createConnection() throws IOException ;
}
