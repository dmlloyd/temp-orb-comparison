


package xxxx;







@Naming
public abstract class NamingContextImpl 
    extends NamingContextExtPOA
    implements NamingContextDataStore
{

    protected ORB orb ;
    protected POA nsPOA;
    private static final NamingSystemException wrapper =
        NamingSystemException.self ;

    
    
    private InterOperableNamingImpl insImpl; 
    
    public NamingContextImpl(ORB orb, POA poa) throws java.lang.Exception {
        super();
        this.orb = orb ;
        insImpl = new InterOperableNamingImpl( );
        this.nsPOA = poa;
    }

    public POA getNSPOA( ) {
        return nsPOA;
    }
  
    
    @Naming
    public void bind(NameComponent[] n, org.omg.CORBA.Object obj)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound
    {
        if( obj == null ) {
            throw wrapper.objectIsNull() ;
        }
        
        NamingContextDataStore impl = this;
        doBind(impl,n,obj,false,BindingType.nobject);
    }

  
    
    @Naming
    public void bind_context(NameComponent[] n, NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound
    {
        if( nc == null ) {
            wrapper.objectIsNull() ;
        }
        
        NamingContextDataStore impl = this;
        doBind(impl,n,nc,false,BindingType.ncontext);
    }
  
    
    @Naming
    public  void rebind(NameComponent[] n, org.omg.CORBA.Object obj)
        throws       org.omg.CosNaming.NamingContextPackage.NotFound,
                     org.omg.CosNaming.NamingContextPackage.CannotProceed,
                     org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if( obj == null ) {
            throw wrapper.objectIsNull() ;
        }

        try {
            
            NamingContextDataStore impl = this;
            doBind(impl,n,obj,true,BindingType.nobject);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound ex) {
            throw wrapper.namingCtxRebindAlreadyBound( ex ) ;
        }
    }

    
    @Naming
    public  void rebind_context(NameComponent[] n, NamingContext nc)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if( nc == null ) {
            throw wrapper.objectIsNull() ;
        }

        try {
            
            NamingContextDataStore impl = this;
            doBind(impl,n,nc,true,BindingType.ncontext);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound ex) {      
            throw wrapper.namingCtxRebindctxAlreadyBound( ex ) ;
        }
    }

    
    @Naming
    public  org.omg.CORBA.Object resolve(NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        
        NamingContextDataStore impl = this;
        return doResolve(impl,n);
    }
            

    
    @Naming
    public  void unbind(NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        
        NamingContextDataStore impl = this;
        doUnbind(impl,n);
    }

    
    @Naming
    public  void list(int how_many, BindingListHolder bl, 
        BindingIteratorHolder bi)
    {
        
        NamingContextDataStore impl = this;
        synchronized (impl) {
            impl.listImpl(how_many,bl,bi);
        }
    }

    
    @Naming
    public synchronized NamingContext new_context()
    {
        NamingContextDataStore impl = this;
        synchronized (impl) {
            return impl.newContextImpl();
        }
    }

    
    @Naming
    public  NamingContext bind_new_context(NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        NamingContext nc = null;
        NamingContext rnc = null;
        try {
            nc = this.new_context();
            this.bind_context(n,nc);
            rnc = nc;
            nc = null;
        } finally {
            try {
                if (nc != null) {
                    nc.destroy();
                }
            } catch (org.omg.CosNaming.NamingContextPackage.NotEmpty e) {
                throw new CannotProceed( "Old naming context is not empty", 
                    nc, n) ;
            }
        }

        return rnc;
    }

    
    @Naming
    public  void destroy()
        throws org.omg.CosNaming.NamingContextPackage.NotEmpty
    {
        NamingContextDataStore impl = this;
        synchronized (impl) {
            if (impl.isEmptyImpl()) {
                
                impl.destroyImpl();
            } else {
                throw new NotEmpty();
            }
        }
    }

    
    @Naming
    public static void doBind(NamingContextDataStore impl,
                              NameComponent[] n,
                              org.omg.CORBA.Object obj,
                              boolean rebind,
                              org.omg.CosNaming.BindingType bt)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName,
               org.omg.CosNaming.NamingContextPackage.AlreadyBound
    {
        if (n.length < 1) {
            throw new InvalidName();
        }
    
        if (n.length == 1) {
            if ( (n[0].id.length() == 0) && (n[0].kind.length() == 0 ) ) {
                throw new InvalidName();
            }

            synchronized (impl) {
                BindingTypeHolder bth = new BindingTypeHolder();
                if (rebind) {
                    org.omg.CORBA.Object objRef = impl.resolveImpl( n[0], bth );
                    if( objRef != null ) {
                        
                        
                        
                        
                        
                        
                        if ( bth.value.value() == BindingType.nobject.value() ){
                            if ( bt.value() == BindingType.ncontext.value() ) {
                                throw new NotFound(
                                    NotFoundReason.not_context, n);
                            }
                        } else {
                            
                            
                            if ( bt.value() == BindingType.nobject.value() ) {
                                throw new NotFound(
                                    NotFoundReason.not_object, n);
                            }
                        }

                        impl.unbindImpl(n[0]);
                    }
                } else {
                    if (impl.resolveImpl(n[0],bth) != null) {
                        throw new AlreadyBound();
                    }
                }
        
                
                impl.bindImpl(n[0],obj,bt);
            }
        } else {
            NamingContext context = resolveFirstAsContext(impl,n);
            NameComponent[] tail = new NameComponent[n.length - 1];
            System.arraycopy(n,1,tail,0,n.length-1);

            switch (bt.value()) {
            case BindingType._nobject:
                if (rebind) {
                    context.rebind(tail, obj);
                } else {
                    context.bind(tail, obj);
                }
                break;
            case BindingType._ncontext:
                NamingContext objContext = (NamingContext)obj;
                if (rebind) {
                context.rebind_context(tail, objContext);
            }
                else {
                context.bind_context(tail, objContext);
            }
                break;
            default:
                throw wrapper.namingCtxBadBindingtype() ;
            }
        }
    }

    
    @Naming
    public static org.omg.CORBA.Object doResolve(NamingContextDataStore impl,
                                                 NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        org.omg.CORBA.Object obj;
        BindingTypeHolder bth = new BindingTypeHolder();
            
        if (n.length < 1) {
            throw new InvalidName();
        }

        if (n.length == 1) {
            synchronized (impl) {
                obj = impl.resolveImpl(n[0],bth);
            }
            if (obj == null) {
                throw new NotFound(NotFoundReason.missing_node,n);
            }
            return obj;
        } else {
            if ( (n[1].id.length() == 0) && (n[1].kind.length() == 0) ) {
                throw new InvalidName();
            }

            NamingContext context = resolveFirstAsContext(impl,n);
            NameComponent[] tail = new NameComponent[n.length -1];
            System.arraycopy(n,1,tail,0,n.length-1);

            try {
                
                
                Servant servant = impl.getNSPOA().reference_to_servant( 
                    context );
                return doResolve(((NamingContextDataStore)servant), tail) ;
            } catch( Exception e ) {
                return context.resolve(tail);
            }
        }
    }

    
    @Naming
    public static void doUnbind(NamingContextDataStore impl,
                                NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound,
               org.omg.CosNaming.NamingContextPackage.CannotProceed,
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if (n.length < 1) {
            throw new InvalidName();
        }

        if (n.length == 1) {
            if ( (n[0].id.length() == 0) && (n[0].kind.length() == 0 ) ) {
                throw new InvalidName();
            }

            org.omg.CORBA.Object objRef;
            synchronized (impl) {
                objRef = impl.unbindImpl(n[0]);
            }
      
            if (objRef == null) {
                throw new NotFound(NotFoundReason.missing_node, n);
            }
        } else {
            NamingContext context = resolveFirstAsContext(impl,n);
            NameComponent[] tail = new NameComponent[n.length - 1];
            System.arraycopy(n,1,tail,0,n.length-1);

            context.unbind(tail);
        }
    }

    
    @Naming
    protected static NamingContext resolveFirstAsContext(
        NamingContextDataStore impl, NameComponent[] n)
        throws org.omg.CosNaming.NamingContextPackage.NotFound {

        org.omg.CORBA.Object topRef;
        BindingTypeHolder bth = new BindingTypeHolder();
        NamingContext context;
    
        synchronized (impl) {
            topRef = impl.resolveImpl(n[0],bth);
            if (topRef == null) {
                throw new NotFound(NotFoundReason.missing_node,n);
            }
        }
      
        if (bth.value != BindingType.ncontext) {
            throw new NotFound(NotFoundReason.not_context,n);
        }
      
        try {
            context = NamingContextHelper.narrow(topRef);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            throw new NotFound(NotFoundReason.not_context,n);
        }

        return context;
    }


   
    @Naming
    public String to_string(org.omg.CosNaming.NameComponent[] n)
         throws org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if ( (n == null ) || (n.length == 0) ) {
            throw new InvalidName();
        }

        String theStringifiedName = insImpl.convertToString( n );

        if (theStringifiedName == null) {
            throw new InvalidName();
        }
        
        return theStringifiedName;
    }


   
    @Naming
    public org.omg.CosNaming.NameComponent[] to_name(String sn)
         throws org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if  ((sn == null ) || (sn.length() == 0)) {
            throw new InvalidName();
        }

        org.omg.CosNaming.NameComponent[] theNameComponents =
            insImpl.convertToNameComponent( sn );
        if (( theNameComponents == null ) || (theNameComponents.length == 0)) {
            throw new InvalidName();
        }

        for (NameComponent theNameComponent : theNameComponents) {
            if (((theNameComponent.id == null)
                    || (theNameComponent.id.length() == 0))
                    && ((theNameComponent.kind == null)
                    || (theNameComponent.kind.length() == 0))) {
                throw new InvalidName();
            }
        }

        return theNameComponents;
    }

   
 
    @Naming
    public String to_url(String addr, String sn)
        throws org.omg.CosNaming.NamingContextExtPackage.InvalidAddress, 
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        if ((sn == null ) || (sn.length() == 0)) {
            throw new InvalidName();
        }

        if( addr == null ) {
            throw new org.omg.CosNaming.NamingContextExtPackage.InvalidAddress();
        }

        String urlBasedAddress;
        urlBasedAddress = insImpl.createURLBasedAddress( addr, sn );

        try {
            INSURLHandler.getINSURLHandler( ).parseURL( urlBasedAddress );
        } catch( BAD_PARAM e ) {
            throw new org.omg.CosNaming.NamingContextExtPackage.InvalidAddress();
        }

        return urlBasedAddress;
    }

    
    @Naming
    public org.omg.CORBA.Object resolve_str(String sn)
        throws org.omg.CosNaming.NamingContextPackage.NotFound, 
               org.omg.CosNaming.NamingContextPackage.CannotProceed, 
               org.omg.CosNaming.NamingContextPackage.InvalidName
    {
        org.omg.CORBA.Object theObject;
        if ((sn == null) || (sn.length() == 0)) {
            throw new InvalidName();
        }

        org.omg.CosNaming.NameComponent[] theNameComponents =
            insImpl.convertToNameComponent( sn );

        if ((theNameComponents == null) || (theNameComponents.length == 0 )) {
            throw new InvalidName();
        }

        theObject = resolve( theNameComponents );
        return theObject;
    }

}
