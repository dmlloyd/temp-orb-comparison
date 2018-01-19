
package com.sun.corba.se.spi.monitoring;

import java.util.*;


public abstract class StringMonitoredAttributeBase
    extends MonitoredAttributeBase
{

  
  



    public  StringMonitoredAttributeBase(String name, String description) {
        super( name );
        MonitoredAttributeInfoFactory f =
            MonitoringFactories.getMonitoredAttributeInfoFactory();
        MonitoredAttributeInfo maInfo = f.createMonitoredAttributeInfo(
            description, String.class, false, false );
       this.setMonitoredAttributeInfo( maInfo );
    } 


} 
