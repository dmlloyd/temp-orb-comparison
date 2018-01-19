


package xxxx;




public class FreezableList<E> extends AbstractList<E> {
    private List<E> delegate = null ;
    private boolean immutable = false ;

    @Override
    public boolean equals( Object obj )
    {
        if (obj == null)
            return false ;

        if (!(obj instanceof FreezableList))
            return false ;

        FreezableList other = FreezableList.class.cast( obj ) ;

        return delegate.equals( other.delegate ) &&
            (immutable == other.immutable) ;
    }

    @Override
    public int hashCode()
    {
        return delegate.hashCode() ;
    }

    public FreezableList( List<E> delegate, boolean immutable  )
    {
        this.delegate = delegate ;
        this.immutable = immutable ;
    }

    public FreezableList( List<E> delegate )
    {
        this( delegate, false ) ;
    }

    public void makeImmutable()
    {
        immutable = true ;
    }

    public boolean isImmutable()
    {
        return immutable ;
    }

    public void makeElementsImmutable()
    {
        for (E x : this) {
            if (x instanceof MakeImmutable) {
                MakeImmutable element = MakeImmutable.class.cast( x ) ;
                element.makeImmutable() ;
            }
        }
    }

    

    public int size()
    {
        return delegate.size() ;
    }

    public E get(int index)
    {
        return delegate.get(index) ;
    }

    @Override
    public E set(int index, E element)
    {
        if (immutable)
            throw new UnsupportedOperationException() ;

        return delegate.set(index, element) ;
    }

    @Override
    public void add(int index, E element)
    {
        if (immutable)
            throw new UnsupportedOperationException() ;

        delegate.add(index, element) ;
    }

    @Override
    public E remove(int index)
    {
        if (immutable)
            throw new UnsupportedOperationException() ;

        return delegate.remove(index) ;
    }

    
    @Override
    public List<E> subList(int fromIndex, int toIndex)
    {
        List<E> list = delegate.subList(fromIndex, toIndex) ;
        List<E> result = new FreezableList<E>( list, immutable ) ;
        return result ;
    }
}
