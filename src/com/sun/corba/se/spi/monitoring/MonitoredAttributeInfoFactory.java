
package xxxx;



public interface MonitoredAttributeInfoFactory {
    
    MonitoredAttributeInfo createMonitoredAttributeInfo( String description,
        Class type, boolean isWritable, boolean isStatistic  );
}
