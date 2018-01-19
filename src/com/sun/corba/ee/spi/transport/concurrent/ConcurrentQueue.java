


package xxxx;


public interface ConcurrentQueue<V> {
    
    public interface Handle<V> {
        
        V value() ;

        
        boolean remove() ;

        
        long expiration() ;
    }

    
    int size() ;

    
    Handle<V> offer( V arg ) ;

    
    Handle<V> poll() ;

    
    Handle<V> peek() ;
} 
