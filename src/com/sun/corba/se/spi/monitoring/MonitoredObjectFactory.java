
package com.sun.corba.se.spi.monitoring;


public interface MonitoredObjectFactory {
    
    MonitoredObject createMonitoredObject( String name, String description );
}
