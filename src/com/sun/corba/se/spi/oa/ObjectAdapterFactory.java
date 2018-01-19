

package xxxx;




public interface ObjectAdapterFactory {
    
    void init( ORB orb ) ;

    
    void shutdown( boolean waitForCompletion ) ;

    
    ObjectAdapter find( ObjectAdapterId oaid ) ;

    ORB getORB() ;
}
