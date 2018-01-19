
package com.sun.corba.se.spi.monitoring;



public interface MonitoringManager extends Closeable {

  
  


    public MonitoredObject getRootMonitoredObject();

    public void clearState();

} 
