
package com.sun.corba.se.spi.monitoring;


public interface MonitoringManagerFactory {
    
    MonitoringManager createMonitoringManager( String nameOfTheRoot,
        String description );

    void remove(String nameOfTheRoot);
}
