
package xxxx;


public interface MonitoringManagerFactory {
    
    MonitoringManager createMonitoringManager( String nameOfTheRoot,
        String description );

    void remove(String nameOfTheRoot);
}
