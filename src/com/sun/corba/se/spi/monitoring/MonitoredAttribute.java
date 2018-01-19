
package com.sun.corba.se.spi.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;
import java.util.*;


public interface MonitoredAttribute {

  
  


    public MonitoredAttributeInfo getAttributeInfo();

    public void setValue(Object value);



    public Object getValue();

    public String getName();

    public void clearState();

} 
