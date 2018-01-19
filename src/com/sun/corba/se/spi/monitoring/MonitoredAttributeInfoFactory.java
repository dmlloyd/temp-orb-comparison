
package com.sun.corba.se.spi.monitoring;



public interface MonitoredAttributeInfoFactory {
    
    MonitoredAttributeInfo createMonitoredAttributeInfo( String description,
        Class type, boolean isWritable, boolean isStatistic  );
}
