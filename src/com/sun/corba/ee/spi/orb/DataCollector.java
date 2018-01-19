

package xxxx;



public interface DataCollector {
    
    boolean isApplet() ;

    
    boolean initialHostIsLocal() ;

    
    void setParser( PropertyParser parser ) ;

    
    Properties getProperties() ;
}
