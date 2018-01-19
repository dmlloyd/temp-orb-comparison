


package com.sun.corba.ee.impl.orb ;


















public class ORBConfiguratorImpl implements ORBConfigurator {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    protected void persistentServerInitialization(ORB theOrb) {
        
    }

    public static class ConfigParser extends ParserImplBase {
        private ORB orb ;

        public ConfigParser( ORB orb ) {
            this.orb = orb ;
        } ;

        public Class<?>[] userConfigurators = null ;

        public PropertyParser makeParser()
        {
            PropertyParser parser = new PropertyParser() ;
            Operation action = OperationFactory.compose( 
                OperationFactory.suffixAction(),
                OperationFactory.classAction( orb.classNameResolver() )
            ) ;
            parser.addPrefix( ORBConstants.USER_CONFIGURATOR_PREFIX, action, 
                "userConfigurators", Class.class ) ;
            return parser ;
        }
    }

    public void configure( DataCollector collector, ORB orb ) 
    {
        ORB theOrb = orb ;

        initObjectCopiers( theOrb ) ;
        initIORFinders( theOrb ) ;

        theOrb.setClientDelegateFactory( 
            
            TransportDefault.makeClientDelegateFactory( theOrb )) ;

        initializeTransport(theOrb) ;

        initializeNaming( theOrb ) ;
        initServiceContextRegistry( theOrb ) ;
        initRequestDispatcherRegistry( theOrb ) ;
        registerInitialReferences( theOrb ) ;
        
        
        
        
        
        
        
        theOrb.createPIHandler() ;

        theOrb.setInvocationInterceptor( 
            PresentationDefaults.getNullInvocationInterceptor() ) ;

        persistentServerInitialization( theOrb ) ;

        runUserConfigurators( collector, theOrb ) ;
    }

    private void runUserConfigurators( DataCollector collector, ORB orb ) 
    {
        
        
        
        ConfigParser parser = new ConfigParser( orb )  ;
        parser.init( collector ) ;
        if (parser.userConfigurators != null) {
            for (int ctr=0; ctr<parser.userConfigurators.length; ctr++) {
                Class cls = parser.userConfigurators[ctr] ;
                try {
                    ORBConfigurator config = (ORBConfigurator)(cls.newInstance()) ;
                    config.configure( collector, orb ) ;
                } catch (Exception exc) {
                    wrapper.userConfiguratorException( exc ) ;
                }
            }
        }
    }


    
    private void initializeTransport(final ORB orb)
    {
        ORBData od = orb.getORBData();

        ContactInfoListFactory contactInfoListFactory =
            od.getCorbaContactInfoListFactory();
        Acceptor[] acceptors = od.getAcceptors();

        
        ORBSocketFactory legacySocketFactory = od.getLegacySocketFactory();
        USLPort[] uslPorts = od.getUserSpecifiedListenPorts() ;
        setLegacySocketFactoryORB(orb, legacySocketFactory);
        

        
        if (legacySocketFactory != null && contactInfoListFactory != null) {
            throw wrapper.socketFactoryAndContactInfoListAtSameTime();
        }

        if (acceptors.length != 0 && legacySocketFactory != null) {
            throw wrapper.acceptorsAndLegacySocketFactoryAtSameTime();
        }

        
        od.getSocketFactory().setORB(orb);

        
        if (legacySocketFactory != null) {
            
            
            
            contactInfoListFactory =
                new ContactInfoListFactory() {
                        public void setORB(ORB orb) { }
                        public ContactInfoList create( IOR ior ) {
                            return new SocketFactoryContactInfoListImpl( 
                                orb, ior);
                        }
                    };
            
        } else if (contactInfoListFactory != null) {
            
            contactInfoListFactory.setORB(orb);
        } else {
            
            contactInfoListFactory =
                TransportDefault.makeCorbaContactInfoListFactory(orb);
        }
        orb.setCorbaContactInfoListFactory(contactInfoListFactory);

        
        
        

        if (!od.noDefaultAcceptors()) {
            
            
            
            
            
            
            
            
            

            
            int port = -1;
            if (od.getORBServerPort() != 0) {
                port = od.getORBServerPort();
            } else if (od.getPersistentPortInitialized()) {
                port = od.getPersistentServerPort();
            } else if ((acceptors.length == 0)) {
                port = 0;
            }
            if (port != -1) {
                createAndRegisterAcceptor(orb, legacySocketFactory, port,
                            LegacyServerSocketEndPointInfo.DEFAULT_ENDPOINT,
                            SocketInfo.IIOP_CLEAR_TEXT);
            }
            

            for (int i = 0; i < acceptors.length; i++) {
                orb.getCorbaTransportManager().registerAcceptor(acceptors[i]);
            }

            
            
            USLPort[] ports = od.getUserSpecifiedListenPorts() ;
            if (ports != null) {
                for (int i = 0; i < ports.length; i++) {
                    createAndRegisterAcceptor(
                        orb, legacySocketFactory, ports[i].getPort(),
                        LegacyServerSocketEndPointInfo.NO_NAME,
                        ports[i].getType());
                }
            }
            
        }
    }

    
    
    private void createAndRegisterAcceptor(ORB orb,
                                           ORBSocketFactory legacySocketFactory,
                                           int port, String name, String type)
    {
        Acceptor acceptor;
        if (legacySocketFactory == null) {
            acceptor =
                new AcceptorImpl(orb, port, name, type);
        } else {
            acceptor =
                new SocketFactoryAcceptorImpl(orb, port, name, type);
        }
        orb.getCorbaTransportManager().registerAcceptor(acceptor);
    }

    private void setLegacySocketFactoryORB(
        final ORB orb, final ORBSocketFactory legacySocketFactory)
    {
        if (legacySocketFactory == null) {
            return;
        }

        
        
        
        

        try {
            AccessController.doPrivileged(
                new PrivilegedExceptionAction<Object>() {
                    public Object run()
                        throws InstantiationException, IllegalAccessException
                    {
                        try {
                            Method method =
                                legacySocketFactory.getClass().getMethod(
                                  "setORB", ORB.class );
                            method.invoke(legacySocketFactory, orb);
                        } catch (NoSuchMethodException e) {
                            
                            
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                }
            );
        } catch (Throwable t) {
            throw wrapper.unableToSetSocketFactoryOrb(t);
        }
    }

    private void initializeNaming( ORB orb )
    { 
        LocalResolver localResolver = ResolverDefault.makeLocalResolver() ;
        orb.setLocalResolver( localResolver ) ;

        Resolver bootResolver = ResolverDefault.makeBootstrapResolver( orb,
            orb.getORBData().getORBInitialHost(),
            orb.getORBData().getORBInitialPort() ) ;

        Operation urlOperation = ResolverDefault.makeINSURLOperation( orb ) ;
        orb.setURLOperation( urlOperation ) ;

        Resolver irResolver = ResolverDefault.makeORBInitRefResolver( urlOperation,
            orb.getORBData().getORBInitialReferences() ) ;

        Resolver dirResolver = ResolverDefault.makeORBDefaultInitRefResolver( 
            urlOperation, orb.getORBData().getORBDefaultInitialReference() ) ;

        Resolver resolver = 
            ResolverDefault.makeCompositeResolver( localResolver,
                ResolverDefault.makeCompositeResolver( irResolver,
                    ResolverDefault.makeCompositeResolver( dirResolver, 
                        bootResolver ) ) ) ;
        orb.setResolver( resolver ) ;
    }

    private void initServiceContextRegistry( ORB orb ) 
    {
        ServiceContextFactoryRegistry scr = orb.getServiceContextFactoryRegistry() ;

        scr.register( 
            ServiceContextDefaults.makeUEInfoServiceContextFactory() ) ;
        scr.register( 
            ServiceContextDefaults.makeCodeSetServiceContextFactory() ) ;
        scr.register( 
            ServiceContextDefaults.makeSendingContextServiceContextFactory() ) ;
        scr.register( 
            ServiceContextDefaults.makeORBVersionServiceContextFactory() ) ;
        scr.register( 
            ServiceContextDefaults.makeMaxStreamFormatVersionServiceContextFactory() ) ;
    }

    private void registerInitialReferences( final ORB orb ) 
    {
        
        NullaryFunction<org.omg.CORBA.Object> closure =
            new NullaryFunction<org.omg.CORBA.Object>() {
                public org.omg.CORBA.Object evaluate() {
                    return new DynAnyFactoryImpl( orb ) ;
                }
            } ;

        NullaryFunction<org.omg.CORBA.Object> future =
            NullaryFunction.Factory.makeFuture( closure ) ;
        orb.getLocalResolver().register( ORBConstants.DYN_ANY_FACTORY_NAME, 
            future ) ;
    }

    private static final int ORB_STREAM = 0 ;

    private void initObjectCopiers( ORB orb )
    {
        
        ObjectCopierFactory orbStream = 
            CopyobjectDefaults.makeORBStreamObjectCopierFactory( orb ) ;

        CopierManager cm = orb.getCopierManager() ;
        cm.setDefaultId( ORB_STREAM ) ;

        cm.registerObjectCopierFactory( orbStream, ORB_STREAM ) ;
    }

    private void initIORFinders( ORB orb ) 
    {
        IdentifiableFactoryFinder profFinder = 
            orb.getTaggedProfileFactoryFinder() ;
        profFinder.registerFactory( IIOPFactories.makeIIOPProfileFactory() ) ;

        IdentifiableFactoryFinder profTempFinder = 
            orb.getTaggedProfileTemplateFactoryFinder() ;
        profTempFinder.registerFactory( 
            IIOPFactories.makeIIOPProfileTemplateFactory() ) ;

        IdentifiableFactoryFinder compFinder = 
            orb.getTaggedComponentFactoryFinder() ;
        compFinder.registerFactory( 
            IIOPFactories.makeCodeSetsComponentFactory() ) ;
        compFinder.registerFactory( 
            IIOPFactories.makeJavaCodebaseComponentFactory() ) ;
        compFinder.registerFactory( 
            IIOPFactories.makeORBTypeComponentFactory() ) ;
        compFinder.registerFactory( 
            IIOPFactories.makeMaxStreamFormatVersionComponentFactory() ) ;
        compFinder.registerFactory( 
            IIOPFactories.makeAlternateIIOPAddressComponentFactory() ) ;
        compFinder.registerFactory( 
            IIOPFactories.makeRequestPartitioningComponentFactory() ) ;
        compFinder.registerFactory(
            IIOPFactories.makeJavaSerializationComponentFactory());
        compFinder.registerFactory(
            IIOPFactories.makeLoadBalancingComponentFactory());
        compFinder.registerFactory(
            IIOPFactories.makeClusterInstanceInfoComponentFactory());

        
        IORFactories.registerValueFactories( orb ) ;

        
        orb.setObjectKeyFactory( IORFactories.makeObjectKeyFactory(orb) ) ;
    }

    private void initRequestDispatcherRegistry( ORB orb ) 
    {
        RequestDispatcherRegistry scr = orb.getRequestDispatcherRegistry() ;

        
        ClientRequestDispatcher csub =
            RequestDispatcherDefault.makeClientRequestDispatcher() ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.TOA_SCID ) ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.TRANSIENT_SCID ) ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.PERSISTENT_SCID ) ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.SC_TRANSIENT_SCID ) ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.SC_PERSISTENT_SCID ) ;
        scr.registerClientRequestDispatcher( csub,  
            ORBConstants.IISC_TRANSIENT_SCID ) ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.IISC_PERSISTENT_SCID ) ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.MINSC_TRANSIENT_SCID ) ;
        scr.registerClientRequestDispatcher( csub, 
            ORBConstants.MINSC_PERSISTENT_SCID ) ;
        
        
        ServerRequestDispatcher sd =
            RequestDispatcherDefault.makeServerRequestDispatcher( orb );
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.TOA_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.TRANSIENT_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.PERSISTENT_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.SC_TRANSIENT_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.SC_PERSISTENT_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.IISC_TRANSIENT_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.IISC_PERSISTENT_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.MINSC_TRANSIENT_SCID ) ;
        scr.registerServerRequestDispatcher( sd, 
            ORBConstants.MINSC_PERSISTENT_SCID ) ;
        
        orb.setINSDelegate( 
            RequestDispatcherDefault.makeINSServerRequestDispatcher( orb ) ) ;
            
        
        LocalClientRequestDispatcherFactory lcsf = 
            RequestDispatcherDefault.makeJIDLLocalClientRequestDispatcherFactory( 
                orb ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.TOA_SCID ) ;

        lcsf = 
            RequestDispatcherDefault.makePOALocalClientRequestDispatcherFactory( 
                orb ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.TRANSIENT_SCID ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.PERSISTENT_SCID ) ;

        lcsf = RequestDispatcherDefault.
            makeFullServantCacheLocalClientRequestDispatcherFactory( orb ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.SC_TRANSIENT_SCID ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.SC_PERSISTENT_SCID ) ;

        lcsf = RequestDispatcherDefault.
            makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory( orb ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.IISC_TRANSIENT_SCID ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.IISC_PERSISTENT_SCID ) ;

        lcsf = RequestDispatcherDefault.
            makeMinimalServantCacheLocalClientRequestDispatcherFactory( orb ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.MINSC_TRANSIENT_SCID ) ;
        scr.registerLocalClientRequestDispatcherFactory( lcsf, 
            ORBConstants.MINSC_PERSISTENT_SCID ) ;

        
        ServerRequestDispatcher bootsd =
            RequestDispatcherDefault.makeBootstrapServerRequestDispatcher( 
                orb ) ;
        scr.registerServerRequestDispatcher( bootsd, "INIT" ) ;
        scr.registerServerRequestDispatcher( bootsd, "TINI" ) ;

        
        ObjectAdapterFactory oaf = OADefault.makeTOAFactory( orb ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.TOA_SCID ) ;

        oaf = OADefault.makePOAFactory( orb ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.TRANSIENT_SCID ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.PERSISTENT_SCID ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.SC_TRANSIENT_SCID ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.SC_PERSISTENT_SCID ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.IISC_TRANSIENT_SCID ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.IISC_PERSISTENT_SCID ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.MINSC_TRANSIENT_SCID ) ;
        scr.registerObjectAdapterFactory( oaf, ORBConstants.MINSC_PERSISTENT_SCID ) ;
    } 
}


