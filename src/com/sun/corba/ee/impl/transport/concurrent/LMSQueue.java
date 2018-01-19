


package com.sun.corba.ee.impl.transport.concurrent ;


public class LMSQueue<V> {
    static private class Node<V> {
        private V value ;
        Node<V> next ;
        Node<V> prev ;

        public Node( V value ) {
            this.value = value ;
        }

        public V getValue() {
            return value ;
        }
    }

    private AtomicReference<Node<V>> head ;
    private AtomicReference<Node<V>> tail ;

    public final Node<V> dummyNode = new Node<V>( null ) ;

    public void enqueue( V val ) {
        if (val == null)
            throw new IllegalArgumentException( "Cannot enqueue null value" ) ;

        Node<V> tl ;
        Node<V> nd = new Node<V>( val ) ;
        while (true) {
            tl = tail.get() ;
            nd.next = tl ;
            if (tail.compareAndSet( tl, nd )) {
                tail.get().prev = nd ;
                break ;
            }
        }
    }

    public V dequeue() {
        Node<V> tl ;
        Node<V> hd ;
        Node<V> firstNodePrev ;
        Node<V> ndDummy ;
        V val ;

        while (true) {                                              
            hd = head.get() ;                                       
            tl = tail.get() ;                                       
            firstNodePrev = hd.prev ;                               
            val = hd.getValue() ;                                   
            if (hd == head.get()) {                                 
                if (val != null) {                                  
                    if (tl != hd) {                                 
                        if (firstNodePrev == null) {                
                            fixList( tl, hd ) ;                     
                            continue ;                              
                        }                                           
                    } else {                                        
                        ndDummy = new Node<V>( null ) ;             
                        ndDummy.next = tl ;                         
                        if (tail.compareAndSet( tl, ndDummy )) {    
                            hd.prev = ndDummy ;                     
                        } else {                                    
                            ndDummy = null ;                        
                        }                                           
                        continue ;                                  
                    }                                               
                    if (head.compareAndSet( hd, firstNodePrev )) {  
                        hd = null ;                                 
                        return val ;                                
                    }                                               
                } else {                                            
                    if (tail == head) {                             
                        return null ;                               
                    } else {                                        
                        if (firstNodePrev == null) {                
                            fixList( tl, hd ) ;                     
                            continue ;                              
                        }                                           
                        head.compareAndSet( hd, firstNodePrev ) ;   
                    }
                }
            }
        }
    }

    private void fixList( Node<V> tl, Node<V> hd ) {
        Node<V> curNode = tl ;
        Node<V> curNodeNext = null ;
        Node<V> nextNodePrev = null ;

        while ((hd == head.get()) && (curNode != head.get())) {
            curNodeNext = curNode.next ;
            if (curNodeNext == null) {
                return ;
            }
            nextNodePrev = curNodeNext.prev ;
            if (nextNodePrev != curNode) {
                curNodeNext.prev = curNode ;
            }
            curNode = curNodeNext ;
        }
    }
}
