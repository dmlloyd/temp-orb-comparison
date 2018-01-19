


package xxxx;




public class IdentifiableContainerBase<E extends Identifiable> 
    extends FreezableList<E>
{
    
    public IdentifiableContainerBase() 
    {
        super( new ArrayList<E>() ) ;
    }
    
    
    public Iterator<E> iteratorById( final int id) 
    {
        return new Iterator<E>() {
            Iterator<E> iter = 
                IdentifiableContainerBase.this.iterator() ;
            E current = advance() ;

            private E advance()
            {
                while (iter.hasNext()) {
                    E ide = iter.next() ;
                    if (ide.getId() == id)
                        return ide ;
                }

                return null ;
            }

            public boolean hasNext() 
            {
                return current != null ;
            }

            public E next()
            {
                E result = current ;
                current = advance() ;
                return result ;
            }

            public void remove()
            {
                iter.remove() ;
            }
        } ;
    }
}
