


package com.sun.corba.ee.impl.oa.poa;




public abstract class ActiveObjectMap
{
    static class Key {
        private byte[] id;

        byte[] id() {
            return id ;
        }

        Key(byte[] id) {
            this.id = id;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            for(int i = 0; i < id.length; i++) {
                buffer.append(Integer.toString((int) id[i], 16));
                if (i != id.length-1) {
                    buffer.append(":");
                }
            }
            return buffer.toString();
        }

        @Override
        public boolean equals(java.lang.Object key) {
            if (!(key instanceof Key)) {
                return false;
            }
            Key k = (Key) key;
            if (k.id.length != this.id.length) {
                return false;
            }
            for(int i = 0; i < this.id.length; i++) {
                if (this.id[i] != k.id[i]) {
                    return false;
                }
            }
            return true;
        }

        
        @Override
        public int hashCode() {
            int h = 0;
            for (int i = 0; i < id.length; i++) {
                h = 31 * h + id[i];
            }
            return h;
        }
    }
   
    protected POAImpl poa ;

    protected ActiveObjectMap( POAImpl poa ) 
    {
        this.poa = poa ;
    }

    public static ActiveObjectMap create( POAImpl poa, boolean multipleIDsAllowed ) 
    {
        if (multipleIDsAllowed) {
            return new MultipleObjectMap(poa);
        } else {
            return new SingleObjectMap(poa);
        }
    }

    private Map<Key,AOMEntry> keyToEntry =
        new HashMap<Key,AOMEntry>() ;
    private Map<AOMEntry,Servant> entryToServant =
        new HashMap<AOMEntry,Servant>() ;
    private Map<Servant,AOMEntry> servantToEntry =
        new HashMap<Servant,AOMEntry>() ;

    public final boolean contains(Servant value)  
    {
        return servantToEntry.containsKey( value ) ;
    }

    public final boolean containsKey(Key key) 
    {
        return keyToEntry.containsKey(key);
    }

    
    public final AOMEntry get(Key key) 
    {
        AOMEntry result = keyToEntry.get(key);
        if (result == null) {
            result = new AOMEntry( poa ) ;
            putEntry( key, result ) ;
        }

        return result ;
    }

    public final Servant getServant( AOMEntry entry ) 
    {
        return entryToServant.get(entry) ;
    }

    public abstract Key getKey(AOMEntry value) throws WrongPolicy ;

    public Key getKey(Servant value) throws WrongPolicy 
    {
        AOMEntry entry = servantToEntry.get(value) ;
        return getKey( entry ) ;
    }

    protected void putEntry( Key key, AOMEntry value ) 
    {
        keyToEntry.put( key, value ) ;
    }

    public final void putServant( Servant servant, AOMEntry value )
    {
        entryToServant.put( value, servant ) ;
        servantToEntry.put( servant, value ) ;
    }

    protected abstract void removeEntry( AOMEntry entry, Key key ) ;

    public final void remove( Key key ) 
    {
        AOMEntry entry = keyToEntry.remove(key) ;
        Servant servant = entryToServant.remove(entry) ;
        if (servant != null) {
            servantToEntry.remove(servant);
        }

        removeEntry( entry, key ) ;
    }

    public abstract boolean hasMultipleIDs(AOMEntry value) ;

    protected  void clear() 
    {
        keyToEntry.clear();
    }

    public final Set<Key> keySet()
    {
        return keyToEntry.keySet() ;
    }
}

class SingleObjectMap extends ActiveObjectMap
{
    private Map<AOMEntry,Key> entryToKey = new HashMap<AOMEntry,Key>() ;

    SingleObjectMap( POAImpl poa )
    {
        super( poa ) ;
    }

    public  Key getKey(AOMEntry value) throws WrongPolicy
    {
        return entryToKey.get(value) ;
    }

    @Override
    protected void putEntry(Key key, AOMEntry value) 
    {
        super.putEntry( key, value);

        entryToKey.put( value, key ) ;
    }

    public  boolean hasMultipleIDs(AOMEntry value) 
    {
        return false;
    }

    
    protected void removeEntry(AOMEntry entry, Key key) 
    {
        entryToKey.remove( entry ) ;
    }

    @Override
    public  void clear() 
    {
        super.clear() ;
        entryToKey.clear() ;
    }
}

class MultipleObjectMap extends ActiveObjectMap 
{
    private Map<AOMEntry,Set<Key>> entryToKeys =
        new HashMap<AOMEntry,Set<Key>>() ;

    MultipleObjectMap( POAImpl poa )
    {
        super( poa ) ;
    }

    public  Key getKey(AOMEntry value) throws WrongPolicy
    {
        throw new WrongPolicy() ;
    }

    @Override
    protected void putEntry(Key key, AOMEntry value) 
    {
        super.putEntry( key, value);

        Set<Key> set = entryToKeys.get( value ) ;
        if (set == null) {
            set = new HashSet<Key>() ;
            entryToKeys.put( value, set ) ;
        }
        set.add( key ) ;
    }

    public  boolean hasMultipleIDs(AOMEntry value) 
    {
        Set<Key> set = entryToKeys.get( value ) ;
        if (set == null) {
            return false;
        }
        return set.size() > 1 ;
    }

    protected void removeEntry(AOMEntry entry, Key key) 
    {
        Set<Key> keys = entryToKeys.get( entry ) ;
        if (keys != null) {
            keys.remove( key ) ;
            if (keys.isEmpty()) {
                entryToKeys.remove(entry);
            }
        }
    }

    @Override
    public  void clear() 
    {
        super.clear() ;
        entryToKeys.clear() ;
    }
}

