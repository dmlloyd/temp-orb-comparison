


package com.sun.corba.ee.impl.ior ;






public abstract class IdentifiableFactoryFinderBase<E extends Identifiable> 
    implements IdentifiableFactoryFinder<E>
{
    protected static final IORSystemException wrapper =
        IORSystemException.self ;

    private ORB orb ;
    private Map<Integer,IdentifiableFactory<E>> map ;

    protected IdentifiableFactoryFinderBase( ORB orb )
    {
        map = new HashMap<Integer,IdentifiableFactory<E>>() ;
        this.orb = orb ;
    }

    protected IdentifiableFactory<E> getFactory(int id) 
    {
        return map.get( id ) ;
    }

    public abstract E handleMissingFactory( int id, 
        InputStream is ) ;
        
    public E create(int id, InputStream is) 
    {
        IdentifiableFactory<E> factory = getFactory( id ) ;

        if (factory != null) {
            return factory.create(orb, is);
        } else {
            return handleMissingFactory(id, is);
        }
    }
    
    public void registerFactory(IdentifiableFactory<E> factory) 
    {
        map.put( factory.getId(), factory ) ;
    }
}
