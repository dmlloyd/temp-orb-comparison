


package xxxx;




















@Subcontract
public class VirtualAddressAgentImpl 
    extends LocalObject 
    implements ORBConfigurator, ORBInitializer, IORInterceptor_3_0
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public static final String VAA_HOST_PROPERTY = ORBConstants.SUN_PREFIX + 
        "ORBVAAHost" ;
    public static final String VAA_PORT_PROPERTY = ORBConstants.SUN_PREFIX + 
        "ORBVAAPort" ;
    private static final long serialVersionUID = 5670615031510472636L;

    private String host = null ;
    private int port = 0 ;
    private ORB orb = null ;
    private IIOPAddress addr = null ;
    private ORBInitializer[] newOrbInits = null ;

    @Subcontract
    private class AddressParser extends ParserImplBase {
        private String _host = null ;
        private int _port = 0 ;

        public PropertyParser makeParser() {
            PropertyParser parser = new PropertyParser() ;
            parser.add( VAA_HOST_PROPERTY, OperationFactory.stringAction(),
                "_host" ) ;
            parser.add( VAA_PORT_PROPERTY, OperationFactory.integerAction(),
                "_port" ) ;
            return parser ;
        }

        @Subcontract
        @Override
        protected void complete() {
            host = _host ;
            port = _port ;
        }
    }

    @InfoMethod 
    private void agentAddress( IIOPAddress addr ) { }

    @Subcontract
    public void configure( DataCollector dc, final ORB orb ) {
        this.orb = orb ;

        orb.setBadServerIdHandler( 
            new BadServerIdHandler() {
                public void handle( ObjectKey objectkey ) {
                    
                }
            }
        ) ;

        
        
        final AddressParser parser = new AddressParser() ;
        parser.init( dc ) ;
        addr = IIOPFactories.makeIIOPAddress( host, port ) ;    
        agentAddress(addr);

        
        
        
        
        IdentifiableFactoryFinder finder = 
            orb.getTaggedProfileFactoryFinder() ;
        finder.registerFactory( 
            new EncapsulationFactoryBase( TAG_INTERNET_IOP.value ) {
                public Identifiable readContents( InputStream in ) {
                    Identifiable result = new SpecialIIOPProfileImpl( in ) ;
                    return result ;
                }
            }
        ) ;

        
        

        final ORBData odata = orb.getORBData() ;

        
        
        final ORBInitializer[] oldOrbInits = odata.getORBInitializers() ;
        final int newIndex = oldOrbInits.length ;
        newOrbInits = new ORBInitializer[newIndex+1] ;
        for (int ctr=0; ctr<newIndex; ctr++)
            newOrbInits[ctr] = oldOrbInits[ctr] ;
        newOrbInits[newIndex] = this ;

        
        
        AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    try {
                        final Field fld = 
                            ORBDataParserImpl.class.getDeclaredField( 
                                "orbInitializers" ) ;
                        fld.setAccessible( true ) ;
                        fld.set( odata, newOrbInits ) ;
                        return null ;
                    } catch (Exception exc) {
                        throw wrapper.couldNotSetOrbInitializer( exc ) ;
                    }
                }
            }
        )  ;
    }

    @Subcontract
    public void pre_init( ORBInitInfo info ) {
        
    }

    @Subcontract
    public void post_init( ORBInitInfo info ) {
        
        try {
            info.add_ior_interceptor( this ) ;
        } catch (Exception exc) {
            wrapper.vaaErrorInPostInit( exc ) ;
        }
    }

    @Subcontract
    public void establish_components( IORInfo info ) {
        
    }

    
    
    
    @Subcontract
    private class SpecialIIOPProfileImpl extends
        IIOPProfileImpl {

        private boolean isLocalChecked = false ;
        private boolean isLocalCachedValue = false ;

        public SpecialIIOPProfileImpl( InputStream in ) {
            super( in ) ;
        }

        public SpecialIIOPProfileImpl( ORB orb, ObjectKeyTemplate oktemp,
            ObjectId id, IIOPProfileTemplate ptemp ) {
            super( orb, oktemp, id, ptemp ) ;
        }

        @InfoMethod
        private void iiopProfileTemplate( IIOPProfileTemplate temp ) { }

        @InfoMethod
        private void templateAddress( IIOPAddress addr ) { }

        @Subcontract
        @Override
        public boolean isLocal() {
            if (!isLocalChecked) {
                isLocalChecked = true ;

                IIOPProfileTemplate ptemp = 
                    (IIOPProfileTemplate)getTaggedProfileTemplate() ;

                iiopProfileTemplate(ptemp);
                templateAddress(addr);

                isLocalCachedValue = addr.equals( ptemp.getPrimaryAddress() ) ;
            }

            return isLocalCachedValue ;
        }
    }

    
    
    private class SpecialIIOPProfileTemplateImpl extends
        IIOPProfileTemplateImpl {
        
        private ORB orb ; 

        public SpecialIIOPProfileTemplateImpl( ORB orb, GIOPVersion version,
            IIOPAddress primary ) {
            super( orb, version, primary ) ;
            this.orb = orb ;
        }

        @Override
        public TaggedProfile create( ObjectKeyTemplate oktemp, ObjectId id ) {
            return new SpecialIIOPProfileImpl( orb, oktemp, id, this ) ;
        }
    }

    @Subcontract
    private TaggedProfileTemplate makeCopy( TaggedProfileTemplate temp ) {
        if (temp instanceof IIOPProfileTemplate) {
            final IIOPProfileTemplate oldTemplate = (IIOPProfileTemplate)temp ;

            
            
            
            
            
            
            
            
            
            
            
            
            final IIOPProfileTemplate result = 
                new SpecialIIOPProfileTemplateImpl(
                    orb, oldTemplate.getGIOPVersion(), addr ) ;

            final Iterator iter = oldTemplate.iterator() ;
            while (iter.hasNext()) {
                TaggedComponent comp = (TaggedComponent)iter.next() ;
                if (!(comp instanceof AlternateIIOPAddressComponent)) 
                    result.add( comp ) ;
            }
        
            return result ;
        } else {
            return temp ;
        }
    }

    @Subcontract
    public void components_established( IORInfo info ) {
        
        
        
        
        IORInfoImpl myInfo = (IORInfoImpl)info ;

        
        final IORTemplate iort = 
            (IORTemplate)IORFactories.getIORFactory( 
                myInfo.adapter_template() ) ;

        
        final IORTemplate result = IORFactories.makeIORTemplate( 
            iort.getObjectKeyTemplate() ) ;

        
        
        
        final Iterator iter = iort.iterator() ;
        while (iter.hasNext()) {
            TaggedProfileTemplate tpt = (TaggedProfileTemplate)iter.next() ;
            result.add( makeCopy( tpt ) ) ;
        }

        final ObjectReferenceTemplate newOrt = 
            IORFactories.makeObjectReferenceTemplate( orb, result ) ;

        
        
        myInfo.current_factory( newOrt );
    }

    public void adapter_manager_state_changed( int id,
        short state ) {
        
    }

    public void adapter_state_changed( ObjectReferenceTemplate[] templates,
        short state ) {
        
    }

    public String name() {
        return this.getClass().getName() ;
    }

    public void destroy() {
        
    }
}
