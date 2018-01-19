


package xxxx;










public abstract class ServiceContextDefaults {

    private static ORBVersion orbVersion = ORBVersionFactory.getORBVersion();
    private static ORBVersionServiceContext orbVersionContext = 
                        new ORBVersionServiceContextImpl( orbVersion );

    private ServiceContextDefaults() {}

    public static ServiceContexts makeServiceContexts( ORB orb )
    {
        return new ServiceContextsImpl( orb ) ;
    }

    public static ServiceContexts makeServiceContexts( InputStream is ) 
    {
        return new ServiceContextsImpl( is ) ;
    }

    public static ServiceContextFactoryRegistry makeServiceContextFactoryRegistry( 
        ORB orb ) 
    {
        return new ServiceContextFactoryRegistryImpl( orb ) ;
    }

    public static CodeSetServiceContext makeCodeSetServiceContext( 
        CodeSetComponentInfo.CodeSetContext csc ) 
    {
        return new CodeSetServiceContextImpl( csc ) ;
    }

    public static ServiceContext.Factory makeCodeSetServiceContextFactory()
    {
        return new ServiceContext.Factory() {
            public int getId()
            {
                return CodeSetServiceContext.SERVICE_CONTEXT_ID ;
            }

            public ServiceContext create( InputStream s, GIOPVersion gv )
            {
                return new CodeSetServiceContextImpl( s, gv ) ;
            }
        } ;
    }

    public static ServiceContext.Factory 
        makeMaxStreamFormatVersionServiceContextFactory()
    {
        return new ServiceContext.Factory() {
            public int getId()
            {
                return MaxStreamFormatVersionServiceContext.SERVICE_CONTEXT_ID ;
            }

            public ServiceContext create( InputStream s, GIOPVersion gv )
            {
                return new MaxStreamFormatVersionServiceContextImpl( s, gv ) ;
            }
        } ;
    }

    public static MaxStreamFormatVersionServiceContext 
        getMaxStreamFormatVersionServiceContext()
    {
        return MaxStreamFormatVersionServiceContextImpl.singleton ;
    }

    public static MaxStreamFormatVersionServiceContext 
        makeMaxStreamFormatVersionServiceContext( byte version )
    {
        return new MaxStreamFormatVersionServiceContextImpl( version ) ;
    }

    public static ServiceContext.Factory makeORBVersionServiceContextFactory()
    {
        return new ServiceContext.Factory() {
            public int getId()
            {
                return ORBVersionServiceContext.SERVICE_CONTEXT_ID ;
            }

            public ServiceContext create( InputStream s, GIOPVersion gv )
            {
                return new ORBVersionServiceContextImpl( s, gv ) ;
            }
        } ;
    }

    public static ORBVersionServiceContext getORBVersionServiceContext()
    {
        return ORBVersionServiceContextImpl.singleton ;
    }

    public static ORBVersionServiceContext makeORBVersionServiceContext()
    {
        return orbVersionContext ;
    }

    public static ServiceContext.Factory makeSendingContextServiceContextFactory()
    {
        return new ServiceContext.Factory() {
            public int getId()
            {
                return SendingContextServiceContext.SERVICE_CONTEXT_ID ;
            }

            public ServiceContext create( InputStream s, GIOPVersion gv )
            {
                return new SendingContextServiceContextImpl( s, gv ) ;
            }
        } ;
    }

    public static SendingContextServiceContext 
        makeSendingContextServiceContext( IOR ior )
    {
        return new SendingContextServiceContextImpl( ior ) ;
    }

    public static ServiceContext.Factory makeUEInfoServiceContextFactory()
    {
        return new ServiceContext.Factory() {
            public int getId()
            {
                return UEInfoServiceContext.SERVICE_CONTEXT_ID ;
            }

            public ServiceContext create( InputStream s, GIOPVersion gv )
            {
                return new UEInfoServiceContextImpl( s, gv ) ;
            }
        } ;
    }

    public static UEInfoServiceContext 
        makeUEInfoServiceContext( Throwable thr )
    {
        return new UEInfoServiceContextImpl( thr ) ;
    }

    public static UnknownServiceContext 
        makeUnknownServiceContext( int id, byte[] data )
    {
        return new UnknownServiceContextImpl( id, data ) ;
    }

    public static UnknownServiceContext 
        makeUnknownServiceContext( int id, InputStream str )
    {
        return new UnknownServiceContextImpl( id, str ) ;
    }
}

