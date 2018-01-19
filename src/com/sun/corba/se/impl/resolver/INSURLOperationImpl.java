

package xxxx;







public class INSURLOperationImpl implements Operation
{
    ORB orb;
    ORBUtilSystemException wrapper ;
    OMGSystemException omgWrapper ;
    Resolver rirResolver ;

    
    private NamingContextExt rootNamingContextExt;
    private Object rootContextCacheLock = new Object() ;

    
    private INSURLHandler insURLHandler = INSURLHandler.getINSURLHandler() ;

    public INSURLOperationImpl( ORB orb, Resolver rirResolver )
    {
        this.orb = orb ;
        wrapper = ORBUtilSystemException.get( orb,
            CORBALogDomains.ORB_RESOLVER ) ;
        omgWrapper = OMGSystemException.get( orb,
            CORBALogDomains.ORB_RESOLVER ) ;
        this.rirResolver = rirResolver ;
    }

    private static final int NIBBLES_PER_BYTE = 2 ;
    private static final int UN_SHIFT = 4 ; 

    
    private org.omg.CORBA.Object getIORFromString( String str )
    {
        
        if ( (str.length() & 1) == 1 )
            throw wrapper.badStringifiedIorLen() ;

        byte[] buf = new byte[(str.length() - ORBConstants.STRINGIFY_PREFIX.length()) / NIBBLES_PER_BYTE];
        for (int i=ORBConstants.STRINGIFY_PREFIX.length(), j=0; i < str.length(); i +=NIBBLES_PER_BYTE, j++) {
             buf[j] = (byte)((ORBUtility.hexOf(str.charAt(i)) << UN_SHIFT) & 0xF0);
             buf[j] |= (byte)(ORBUtility.hexOf(str.charAt(i+1)) & 0x0F);
        }
        EncapsInputStream s = EncapsInputStreamFactory.newEncapsInputStream(orb, buf, buf.length,
                orb.getORBData().getGIOPVersion());
        s.consumeEndian();
        return s.read_Object() ;
    }

    public Object operate( Object arg )
    {
        if (arg instanceof String) {
            String str = (String)arg ;

            if (str.startsWith( ORBConstants.STRINGIFY_PREFIX ))
                
                return getIORFromString( str ) ;
            else {
                INSURL insURL = insURLHandler.parseURL( str ) ;
                if (insURL == null)
                    throw omgWrapper.soBadSchemeName() ;
                return resolveINSURL( insURL ) ;
            }
        }

        throw wrapper.stringExpected() ;
    }

    private org.omg.CORBA.Object resolveINSURL( INSURL theURLObject ) {
        
        if( theURLObject.isCorbanameURL() ) {
            return resolveCorbaname( (CorbanameURL)theURLObject );
        } else {
            return resolveCorbaloc( (CorbalocURL)theURLObject );
        }
    }

    
    private org.omg.CORBA.Object resolveCorbaloc(
        CorbalocURL theCorbaLocObject )
    {
        try {
            org.omg.CORBA.Object result = null;
            
            if (theCorbaLocObject.getRIRFlag()) {
                result = rirResolver.resolve(theCorbaLocObject.getKeyString());
            } else {
                result = getIORUsingCorbaloc(theCorbaLocObject);
            }

            return result;
        } catch(Exception e){
            clearRootNamingContextCache( );
            return null;
        }
    }

    
    private org.omg.CORBA.Object resolveCorbaname( CorbanameURL theCorbaName ) {
        org.omg.CORBA.Object result = null;

        try {
            NamingContextExt theNamingContext = null;

            if( theCorbaName.getRIRFlag( ) ) {
                
                theNamingContext = getDefaultRootNamingContext( );
            } else {
                
                org.omg.CORBA.Object corbalocResult =
                    getIORUsingCorbaloc( theCorbaName );
                if( corbalocResult == null ) {
                    return null;
                }

                theNamingContext =
                    NamingContextExtHelper.narrow( corbalocResult );
            }

            String StringifiedName = theCorbaName.getStringifiedName( );

            if( StringifiedName == null ) {
                
                return theNamingContext;
            } else {
                return theNamingContext.resolve_str( StringifiedName );
            }
        } catch( Exception e ) {
            clearRootNamingContextCache( );
            return null;
        }
     }

    
     private org.omg.CORBA.Object getIORUsingCorbaloc( INSURL corbalocObject ) throws UserException
     {
        Map     profileMap = new HashMap();
        List    profileList1_0 = new ArrayList();

        
        
        java.util.List theEndpointInfo = corbalocObject.getEndpointInfo();
        String theKeyString = corbalocObject.getKeyString();
        
        if( theKeyString == null ) {
            return null;
        }

        ObjectKey key = orb.getObjectKeyFactory().create(
            theKeyString.getBytes() );
        IORTemplate iortemp = IORFactories.makeIORTemplate( key.getTemplate() );
        java.util.Iterator iterator = theEndpointInfo.iterator( );
        while( iterator.hasNext( ) ) {
            IIOPEndpointInfo element =
                (IIOPEndpointInfo) iterator.next( );
            IIOPAddress addr = IIOPFactories.makeIIOPAddress( orb, element.getHost(),
                element.getPort() );
            GIOPVersion giopVersion = GIOPVersion.getInstance( (byte)element.getMajor(),
                                             (byte)element.getMinor());
            IIOPProfileTemplate profileTemplate = null;
            if (giopVersion.equals(GIOPVersion.V1_0)) {
                profileTemplate = IIOPFactories.makeIIOPProfileTemplate(
                    orb, giopVersion, addr);
                profileList1_0.add(profileTemplate);
            } else {
                if (profileMap.get(giopVersion) == null) {
                    profileTemplate = IIOPFactories.makeIIOPProfileTemplate(
                        orb, giopVersion, addr);
                    profileMap.put(giopVersion, profileTemplate);
                } else {
                    profileTemplate = (IIOPProfileTemplate)profileMap.get(giopVersion);
                    AlternateIIOPAddressComponent iiopAddressComponent =
                                IIOPFactories.makeAlternateIIOPAddressComponent(addr);
                    profileTemplate.add(iiopAddressComponent);
                }
            }

            if ( element.isSecured() ) {
                createSSLTaggedComponent( profileTemplate, element.getPort() );
            }
        }

        GIOPVersion giopVersion = orb.getORBData().getGIOPVersion();
        IIOPProfileTemplate pTemplate = (IIOPProfileTemplate)profileMap.get(giopVersion);
        if (pTemplate != null) {
            iortemp.add(pTemplate); 
            profileMap.remove(giopVersion); 
        }

        
        Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                GIOPVersion gv1 = (GIOPVersion)o1;
                GIOPVersion gv2 = (GIOPVersion)o2;
                return (gv1.lessThan(gv2) ? 1 : (gv1.equals(gv2) ? 0 : -1));
            };
        };

        
        List list = new ArrayList(profileMap.keySet());
        Collections.sort(list, comp);

        
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
            IIOPProfileTemplate pt = (IIOPProfileTemplate)profileMap.get(iter.next());
            iortemp.add(pt);
        }

        
        iortemp.addAll(profileList1_0);

        IOR ior = iortemp.makeIOR( orb, "", key.getId() ) ;
        return ORBUtility.makeObjectReference( ior ) ;
    }

    private void createSSLTaggedComponent(IIOPProfileTemplate profileTemplate, int port) throws UserException
    {
        SSL ssl = new SSL();
        ssl.port = (short) port;

        short sslOptions = Integrity.value | Confidentiality.value | DetectMisordering.value | DetectReplay.value
                | EstablishTrustInTarget.value | EstablishTrustInClient.value;
        ssl.target_supports = sslOptions;
        ssl.target_requires = sslOptions;

        GIOPVersion giopVersion = orb.getORBData().getGIOPVersion();
        CDREncapsCodec codec = new CDREncapsCodec(orb, giopVersion.getMajor(),giopVersion.getMinor());

        Any any = orb.create_any();
        SSLHelper.insert(any, ssl);
        byte[] componentData = codec.encode_value(any);

        TaggedComponent sslTaggedComponent = new TaggedComponent(TAG_SSL_SEC_TRANS.value, componentData);

        TaggedComponentFactoryFinder finder =
                orb.getTaggedComponentFactoryFinder();
        Object newTaggedComponent = finder.create( orb, sslTaggedComponent);

        profileTemplate.add(newTaggedComponent);
    }


    
    private NamingContextExt getDefaultRootNamingContext( ) {
        synchronized( rootContextCacheLock ) {
            if( rootNamingContextExt == null ) {
                try {
                    rootNamingContextExt =
                        NamingContextExtHelper.narrow(
                        orb.getLocalResolver().resolve( "NameService" ) );
                } catch( Exception e ) {
                    rootNamingContextExt = null;
                }
            }
        }
        return rootNamingContextExt;
    }

    
    private void clearRootNamingContextCache( ) {
        synchronized( rootContextCacheLock ) {
            rootNamingContextExt = null;
        }
    }
}
