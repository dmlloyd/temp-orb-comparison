


package xxxx;


public interface InboundConnectionCache<C extends Connection> extends ConnectionCache<C> {
   
    void requestReceived( C conn ) ;

    
    void requestProcessed( C conn, int numResponseExpected ) ;

    
    void responseSent( C conn ) ;
}
