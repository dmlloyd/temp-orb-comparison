
package com.sun.corba.se.spi.monitoring;

import java.io.Closeable;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import java.util.*;


public interface MonitoringManager extends Closeable {

  
  


    public MonitoredObject getRootMonitoredObject();

    public void clearState();

} 
