

package com.sun.corba.ee.spi.transport;



public interface MessageTraceManager {
    
    void clear() ;

    
    boolean isEnabled() ;

    
    void enable( boolean flag ) ;

    
    byte[][] getDataSent() ;

    
    byte[][] getDataReceived() ;

    void recordDataSent(ByteBuffer message);
}
