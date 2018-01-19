


package com.sun.corba.ee.spi.transport.connection;

import java.io.IOException ;


public interface ContactInfo<C extends Connection> {
    
    C createConnection() throws IOException ;
}
