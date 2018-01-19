


package com.sun.corba.ee.impl.transport.concurrent ;

import com.sun.corba.ee.spi.transport.concurrent.ConcurrentQueue ;

public class ConcurrentQueueNonBlockingImpl<V> implements ConcurrentQueue<V> {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    final Entry<V> head = new Entry<V>( null, 0 ) ;
    final Object lock = new Object() ;
    int count = 0 ;
    private long ttl ;

    public ConcurrentQueueNonBlockingImpl( long ttl ) {
        head.next = head ;
        head.prev = head ;
        this.ttl = ttl ;
    }

    private final class Entry<V> {
        Entry<V> next = null ;
        Entry<V> prev = null ;
        private HandleImpl<V> handle ;
        private long expiration ;

        Entry( V value, long expiration ) {
            handle = new HandleImpl<V>( this, value, expiration ) ;
            this.expiration = expiration ;
        }

        HandleImpl<V> handle() {
            return handle ;
        }
    }

    private final class HandleImpl<V> implements Handle<V> {
        private Entry<V> entry ;
        private final V value ;
        private boolean valid ;
        private long expiration ;

        HandleImpl( Entry<V> entry, V value, long expiration ) {
            this.entry = entry ;
            this.value = value ;
            this.valid = true ;
            this.expiration = expiration ;
        }

        Entry<V> entry() {
            return entry ;
        }

        public V value() {
            return value ;
        }

        
        public boolean remove() {
            synchronized (lock) {
                if (!valid) {
                    return false ;
                }

                valid = false ;

                entry.next.prev = entry.prev ;
                entry.prev.next = entry.next ;
                count-- ;
            }

            entry.prev = null ;
            entry.next = null ;
            entry.handle = null ;
            entry = null ;
            valid = false ;
            return true ;
        }

        public long expiration() {
            return expiration ;
        }
    }

    public int size() {
        synchronized (lock) {
            return count ;
        }
    }

    
    public Handle<V> offer( V arg ) {
        if (arg == null)
            throw new IllegalArgumentException( "Argument cannot be null" ) ;

        Entry<V> entry = new Entry<V>( arg, System.currentTimeMillis() + ttl ) ;
        
        synchronized (lock) {
            entry.next = head ;
            entry.prev = head.prev ;
            head.prev.next = entry ;
            head.prev = entry ;
            count++ ;
        }

        return entry.handle() ;
    }

    
    public Handle<V> poll() {
        Entry<V> first = null ;

        synchronized (lock) {
            first = head.next ;
            if (first == head)
                return null ;
            else {
                final Handle<V> result = first.handle() ;
                result.remove() ;
                return result ;
            }
        }
    }

    public Handle<V> peek() {
        synchronized (lock) {
            Entry<V> first = head.next ;
            if (first == head) 
                return null ;
            else
                return first.handle() ;
        }
    }
} 

