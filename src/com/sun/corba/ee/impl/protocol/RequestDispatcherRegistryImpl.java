


package com.sun.corba.ee.impl.protocol;






public class RequestDispatcherRegistryImpl implements RequestDispatcherRegistry {
    protected int defaultId; 
                             

    private DenseIntMapImpl<ServerRequestDispatcher> SDRegistry ;
    private DenseIntMapImpl<ClientRequestDispatcher> CSRegistry ;
    private DenseIntMapImpl<ObjectAdapterFactory> OAFRegistry ; 
    private DenseIntMapImpl<LocalClientRequestDispatcherFactory> LCSFRegistry ; 
    private Set<ObjectAdapterFactory> objectAdapterFactories ;  
    private Set<ObjectAdapterFactory> objectAdapterFactoriesView ;      
    private Map<String,ServerRequestDispatcher> stringToServerSubcontract ;

    public RequestDispatcherRegistryImpl(int defaultId ) 
    {
        this.defaultId = defaultId;
        SDRegistry = new DenseIntMapImpl<ServerRequestDispatcher>() ;
        CSRegistry = new DenseIntMapImpl<ClientRequestDispatcher>() ;
        OAFRegistry = new DenseIntMapImpl<ObjectAdapterFactory>() ;
        LCSFRegistry = new DenseIntMapImpl<LocalClientRequestDispatcherFactory>() ;
        objectAdapterFactories = new HashSet<ObjectAdapterFactory>() ;
        objectAdapterFactoriesView = Collections.unmodifiableSet( objectAdapterFactories ) ;
        stringToServerSubcontract = new HashMap<String,ServerRequestDispatcher>() ;
    }

    public synchronized void registerClientRequestDispatcher( 
        ClientRequestDispatcher csc, int scid)
    {
        CSRegistry.set( scid, csc ) ;
    }

    public synchronized void registerLocalClientRequestDispatcherFactory( 
        LocalClientRequestDispatcherFactory csc, int scid)
    {
        LCSFRegistry.set( scid, csc ) ;
    }

    public synchronized void registerServerRequestDispatcher( 
        ServerRequestDispatcher ssc, int scid)
    {
        SDRegistry.set( scid, ssc ) ;
    }

    public synchronized void registerServerRequestDispatcher(
        ServerRequestDispatcher scc, String name )
    {
        stringToServerSubcontract.put( name, scc ) ;
    }

    public synchronized void registerObjectAdapterFactory( 
        ObjectAdapterFactory oaf, int scid)
    {
        objectAdapterFactories.add( oaf ) ;
        OAFRegistry.set( scid, oaf ) ;
    }

    
    
    

    
    
    
    
    
    
    
    
    public ServerRequestDispatcher getServerRequestDispatcher(int scid)
    {
        ServerRequestDispatcher sdel = SDRegistry.get(scid) ;
        if ( sdel == null )
            sdel = SDRegistry.get(defaultId) ;

        return sdel;
    }

    public ServerRequestDispatcher getServerRequestDispatcher( String name )
    {
        ServerRequestDispatcher sdel = stringToServerSubcontract.get( name ) ;

        if ( sdel == null )
            sdel = SDRegistry.get(defaultId) ;

        return sdel;
    }

    public LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory( 
        int scid )
    {
        LocalClientRequestDispatcherFactory factory = LCSFRegistry.get(scid) ;
        if (factory == null) {
            factory = LCSFRegistry.get(defaultId) ;
        }

        return factory ;
    }

    public ClientRequestDispatcher getClientRequestDispatcher( int scid )
    {
        ClientRequestDispatcher subcontract = CSRegistry.get(scid) ;
        if (subcontract == null) {
            subcontract = CSRegistry.get(defaultId) ;
        }

        return subcontract ;
    }

    public ObjectAdapterFactory getObjectAdapterFactory( int scid )
    {
        ObjectAdapterFactory oaf = OAFRegistry.get(scid) ;
        if ( oaf == null )
            oaf = OAFRegistry.get(defaultId) ;

        return oaf;
    }

    public Set<ObjectAdapterFactory> getObjectAdapterFactories() 
    {
        return objectAdapterFactoriesView ;
    }
}
