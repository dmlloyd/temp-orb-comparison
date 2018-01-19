

package com.sun.corba.ee.spi.transport;


public interface MessageData {
    
    Message[] getMessages() ;

     
    CDRInputObject getStream() ;
}
