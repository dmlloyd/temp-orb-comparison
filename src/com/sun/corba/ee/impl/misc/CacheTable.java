

package com.sun.corba.ee.impl.misc;

import com.sun.corba.ee.spi.orb.ORB;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;

import com.sun.corba.ee.spi.trace.Cdr;


@Cdr
public class CacheTable<K> {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private class Entry<K> {
        private K key;
        private int val;
        private Entry<K> next;  
        private Entry<K> rnext; 
        public Entry(K k, int v) {
            key = k;
            val = v;
            next = null;
            rnext = null;
        } 
    }

    private boolean noReverseMap;
    private String cacheType ;

    
    private static final int INITIAL_SIZE = 64 ;
    private static final int MAX_SIZE = 1 << 30;
    private static final int INITIAL_THRESHHOLD = 48 ; 
    private int size;
    private int threshhold ;
    private int entryCount;
    private Entry<K>[] map;
    private Entry<K>[] rmap;
      
    private ORB orb;

    public  CacheTable(String cacheType, ORB orb, boolean u) {
        this.orb = orb;
        this.cacheType = cacheType ;
        noReverseMap = u;
        size = INITIAL_SIZE;
        threshhold = INITIAL_THRESHHOLD ;
        entryCount = 0;
        initTables();
    }

    private void initTables() {
        map = new Entry[size];
        if (noReverseMap) {
            rmap = null;
        } else {
            rmap = new Entry[size];
        }
    }

    private void grow() {
        if (size == MAX_SIZE) {
            return;
        }

        Entry<K>[] oldMap = map;
        int oldSize = size;
        size <<= 1;
        threshhold <<= 1 ;

        initTables();
        
        for (int i = 0; i < oldSize; i++) {
            for (Entry<K> e = oldMap[i]; e != null; e = e.next) {
                put_table(e.key, e.val);
            }
        }
    }

    private int hashModTableSize(int h) {
        
        
        
        
        
        h ^= (h >>> 20) ^ (h >>> 12) ;
        return (h ^ (h >>> 7) ^ (h >>> 4)) & (size - 1) ;
    }

    private int hash(K key) {
        return hashModTableSize(System.identityHashCode(key));
    }

    private int hash(int val) {
        return hashModTableSize(val);
    }

    
    public final void put(K key, int val) {
        if (put_table(key, val)) {
            entryCount++;
            if (entryCount > threshhold) {
                grow();
            }
        }
    }

    @Cdr
    private boolean put_table(K key, int val) {
        int index = hash(key);

        for (Entry<K> e = map[index]; e != null; e = e.next) {
            if (e.key == key) {
                if (e.val != val) {
                    
                    
                    
                    
                    
                    
                    wrapper.duplicateIndirectionOffset();
                } else {        
                    
                    
                    return false;
                }
            }
        }
        
        Entry<K> newEntry = new Entry<K>(key, val);
        newEntry.next = map[index];
        map[index] = newEntry;
        if (!noReverseMap) {
            int rindex = hash(val);
            newEntry.rnext = rmap[rindex];
            rmap[rindex] = newEntry;
        }

        return true;
    }

    public final boolean containsKey(K key) {
        return (getVal(key) != -1);
    }

    
    public final int getVal(K key) {
        int index = hash(key);
        for (Entry<K> e = map[index]; e != null; e = e.next) {
            if (e.key == key) {
                return e.val;
            }
        }

        return -1;
    }

    public final boolean containsVal(int val) {
        return (getKey(val) != null); 
    }

    
    public final K getKey(int val) {
        if (noReverseMap) {
            throw wrapper.getKeyInvalidInCacheTable();
        }

        int index = hash(val);
        for (Entry<K> e = rmap[index]; e != null; e = e.rnext) {
            if (e.val == val) {
                return e.key;
            }
        }

        return null;
    }

    public void done() {
        map = null;
        rmap = null;
    }
}
