


package xxxx;







public class ReflectObjectCopierImpl implements ObjectCopier {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    
    
    

    
    
    
    
    
    
    
    static final ThreadLocal localORB = new ThreadLocal() ;

    
    
    
    
    private static ClassCopier remoteClassCopier =
        new ClassCopierBase( "remote" ) {
            public Object createCopy( Object source ) {
                ORB orb = (ORB)localORB.get() ;
                return Utility.autoConnect( source, orb, true ) ;
            }
        } ;

    private static ClassCopier identityClassCopier =
        new ClassCopierBase( "identity" ) {
            public Object createCopy( Object source ) {
                return source ;
            } 
        } ;

    
    
    private static ClassCopier corbaClassCopier = 
        new ClassCopierBase( "corba" ) {
            public Object createCopy( Object source) {
                ObjectImpl oi = (ObjectImpl)source ;
                Delegate del = oi._get_delegate() ;

                try {
                    
                    ObjectImpl result = (ObjectImpl)source.getClass().newInstance() ;
                    result._set_delegate( del ) ;

                    return result ;
                } catch (Exception exc) {
                    throw wrapper.exceptionInCreateCopy( exc ) ;

                }
            }
        } ;

    private static ClassCopierFactory specialClassCopierFactory = 
        new ClassCopierFactory() {
            public ClassCopier getClassCopier( Class cls 
            ) throws ReflectiveCopyException
            {
                ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( cls ) ;
                
                
                
                if (cinfo.isARemote(cls)) {
                    return remoteClassCopier;
                }

                
                if (cinfo.isAObjectImpl(cls)) {
                    return corbaClassCopier;
                }

                
                if (cinfo.isAORB(cls)) {
                    return identityClassCopier ;
                }

                return null ;
            }
        } ;

    
    
    
    
    
    private static PipelineClassCopierFactory ccf = 
        DefaultClassCopierFactories.getPipelineClassCopierFactory() ; 
    
    static {
        ccf.setSpecialClassCopierFactory( specialClassCopierFactory ) ;
    }

    private Map oldToNew ;

    
    public ReflectObjectCopierImpl( ORB orb )
    {
        localORB.set( orb ) ;
        if (DefaultClassCopierFactories.USE_FAST_CACHE) {
            oldToNew =
                new FastCache(new IdentityHashMap());
        } else {
            oldToNew = new IdentityHashMap();
        }
    }

    
    public Object copy( Object obj ) throws ReflectiveCopyException
    {
        return copy( obj, false ) ;
    }

    public Object copy( Object obj, boolean debug ) throws ReflectiveCopyException
    {
        if (obj == null) {
            return null;
        }

        OperationTracer.begin( "ReflectObjectCopierImpl" ) ;
        Class<?> cls = obj.getClass() ;
        ClassCopier copier = ccf.getClassCopier( cls ) ;
        return copier.copy( oldToNew, obj) ;
    }
}
