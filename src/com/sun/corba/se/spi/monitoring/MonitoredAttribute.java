
package com.sun.corba.se.spi.monitoring;



public interface MonitoredAttribute {

  
  


    public MonitoredAttributeInfo getAttributeInfo();

    public void setValue(Object value);



    public Object getValue();

    public String getName();

    public void clearState();

} 
