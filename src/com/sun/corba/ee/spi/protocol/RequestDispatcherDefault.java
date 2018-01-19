

package com.sun.corba.ee.spi.protocol ;







public final class RequestDispatcherDefault {
    private RequestDispatcherDefault() {}

    public static ClientRequestDispatcher makeClientRequestDispatcher()
    {
        return new ClientRequestDispatcherImpl() ;
    }

    public static ServerRequestDispatcher makeServerRequestDispatcher( ORB orb )
    {
        return new ServerRequestDispatcherImpl( (com.sun.corba.ee.spi.orb.ORB)orb ) ;
    }

    public static ServerRequestDispatcher makeBootstrapServerRequestDispatcher( ORB orb )
    {
        return new BootstrapServerRequestDispatcher( orb ) ;
    }

    public static ServerRequestDispatcher makeINSServerRequestDispatcher( ORB orb )
    {
        return new INSServerRequestDispatcher( orb ) ;
    }

    public static LocalClientRequestDispatcherFactory makeMinimalServantCacheLocalClientRequestDispatcherFactory( final ORB orb ) 
    {
        return new LocalClientRequestDispatcherFactory() {
            public LocalClientRequestDispatcher create( int id, IOR ior ) {
                return new MinimalServantCacheLocalCRDImpl( orb, id, ior ) ;
            }
        } ;
    }

    public static LocalClientRequestDispatcherFactory makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory( final ORB orb ) 
    {
        return new LocalClientRequestDispatcherFactory() {
            public LocalClientRequestDispatcher create( int id, IOR ior ) {
                return new InfoOnlyServantCacheLocalCRDImpl( orb, id, ior ) ;
            }
        } ;
    }

    public static LocalClientRequestDispatcherFactory makeFullServantCacheLocalClientRequestDispatcherFactory( final ORB orb ) 
    {
        return new LocalClientRequestDispatcherFactory() {
            public LocalClientRequestDispatcher create( int id, IOR ior ) {
                return new FullServantCacheLocalCRDImpl( orb, id, ior ) ;
            }
        } ;
    }

    public static LocalClientRequestDispatcherFactory makeJIDLLocalClientRequestDispatcherFactory( final ORB orb ) 
    {
        return new LocalClientRequestDispatcherFactory() {
            public LocalClientRequestDispatcher create( int id, IOR ior ) {
                return new JIDLLocalCRDImpl( orb, id, ior ) ;
            }
        } ;
    }

    public static LocalClientRequestDispatcherFactory makePOALocalClientRequestDispatcherFactory( final ORB orb ) 
    {
        return new LocalClientRequestDispatcherFactory() {
            public LocalClientRequestDispatcher create( int id, IOR ior ) {
                return new POALocalCRDImpl( orb, id, ior ) ;
            }
        } ;
    }
}
