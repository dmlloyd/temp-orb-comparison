
package com.sun.corba.se.spi.orb ;



public interface DataCollector {
    
    boolean isApplet() ;

    
    boolean initialHostIsLocal() ;

    
    void setParser( PropertyParser parser ) ;

    
    Properties getProperties() ;
}
