

package com.sun.corba.ee.spi.orb ;



public interface DataCollector {
    
    boolean isApplet() ;

    
    boolean initialHostIsLocal() ;

    
    void setParser( PropertyParser parser ) ;

    
    Properties getProperties() ;
}
