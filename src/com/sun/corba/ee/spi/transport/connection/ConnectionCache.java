


package xxxx;


public interface ConnectionCache<C extends Connection> {
    
    String getCacheType() ;

    
    long numberOfConnections() ;

    
    long numberOfIdleConnections() ;

    
    long numberOfBusyConnections() ;

    
    long numberOfReclaimableConnections() ;

    
    int highWaterMark() ;

    
    int numberToReclaim() ;
    
    
    void close( final C conn ) ;
}
