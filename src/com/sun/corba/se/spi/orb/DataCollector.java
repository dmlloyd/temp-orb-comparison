
package com.sun.corba.se.spi.orb ;

import java.applet.Applet ;
import java.util.Properties ;
import java.util.Vector ;


public interface DataCollector {
    
    boolean isApplet() ;

    
    boolean initialHostIsLocal() ;

    
    void setParser( PropertyParser parser ) ;

    
    Properties getProperties() ;
}
