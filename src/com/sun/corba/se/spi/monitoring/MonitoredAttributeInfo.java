
package com.sun.corba.se.spi.monitoring;

import java.util.*;


public interface MonitoredAttributeInfo {

  
  


    public boolean isWritable();

    public boolean isStatistic();

    public Class type();

    public String getDescription();

} 
