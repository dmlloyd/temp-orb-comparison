

package com.sun.corba.ee.spi.transport;

import java.nio.ByteBuffer;


public interface MessageTraceManager {
    
    void clear() ;

    
    boolean isEnabled() ;

    
    void enable( boolean flag ) ;

    
    byte[][] getDataSent() ;

    
    byte[][] getDataReceived() ;

    void recordDataSent(ByteBuffer message);
}
