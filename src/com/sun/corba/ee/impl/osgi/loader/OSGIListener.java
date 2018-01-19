

package xxxx;









@Osgi
public class OSGIListener implements BundleActivator, SynchronousBundleListener {
    private static final ReadWriteLock lock = new ReentrantReadWriteLock() ;

    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private static final String ORB_PROVIDER_KEY = "ORB-Class-Provider" ;

    private static PackageAdmin pkgAdmin ;

    private static void setPackageAdmin( PackageAdmin pa ) {
        pkgAdmin = pa ;
    }

    private static Dictionary secureGetHeaders( final Bundle bundle ) {
        if (System.getSecurityManager() == null) {
            return bundle.getHeaders() ;
        } else {
            return AccessController.doPrivileged(
                new PrivilegedAction<Dictionary>() {
                    public Dictionary run() {
                        return bundle.getHeaders() ;
                    }
                }
            ) ;
        }
    }

    private static Class<?> secureLoadClass( final Bundle bundle, 
        final String className ) throws ClassNotFoundException {

        if (System.getSecurityManager() == null) {
            return bundle.loadClass( className ) ;
        } else {
            try {
                return AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Class<?>>() {
                        public Class<?> run() throws ClassNotFoundException {
                            return bundle.loadClass( className ) ;
                        }
                    }
                ) ;
            } catch (PrivilegedActionException exc) {
                throw (ClassNotFoundException)exc.getException() ;
            }
        }
    }

    
    
    private static Map<String,Bundle> classNameMap =
        new ConcurrentHashMap<String,Bundle>() ;

    
    
    private static Map<String,Bundle> packageNameMap = 
        new ConcurrentHashMap<String,Bundle>() ;

    private static String getBundleEventType( int type ) {
        if (type == BundleEvent.INSTALLED) {
            return "INSTALLED";
        } else if (type == BundleEvent.LAZY_ACTIVATION) {
            return "LAZY_ACTIVATION";
        } else if (type == BundleEvent.RESOLVED) {
            return "RESOLVED" ;
        } else if (type == BundleEvent.STARTED) {
            return "STARTED" ;
        } else if (type == BundleEvent.STARTING) {
            return "STARTING" ;
        } else if (type == BundleEvent.STOPPED) {
            return "STOPPED" ;
        } else if (type == BundleEvent.STOPPING) {
            return "STOPPING" ;
        } else if (type == BundleEvent.UNINSTALLED) {
            return "UNINSTALLED" ;
        } else if (type == BundleEvent.UNRESOLVED) {
            return "UNRESOLVED" ;
        } else if (type == BundleEvent.UPDATED) {
            return "UPDATED" ;
        } else {
            return "ILLEGAL-EVENT-TYPE" ;
        }
    }

    @InfoMethod
    private void classNotFoundInBundle( String arg ) {}

    @InfoMethod
    private void foundClassInBundle( String arg, String name ) {}

    @Osgi
    private static class ClassNameResolverImpl implements
        UnaryFunction<String,Class<?>> {

        @InfoMethod
        private void classNotFoundInBundle( String arg ) {}

        @InfoMethod
        private void foundClassInBundle( String arg, String name ) {}

        @Osgi
        public Class<?> evaluate(String arg) {
            Bundle bundle = getBundleForClass( arg ) ;
            if (bundle == null) {
                classNotFoundInBundle( arg ) ;
                return null ;
            } else {
                foundClassInBundle( arg, bundle.getSymbolicName() ) ;
            }

            try {
                return secureLoadClass( bundle, arg );
            } catch (ClassNotFoundException ex) {
                throw wrapper.bundleCouldNotLoadClass( ex, arg, 
                    bundle.getSymbolicName() ) ;
            }
        }

        @Override
        public String toString() {
            return "OSGiClassNameResolver" ;
        }
    }

    private static UnaryFunction<String,Class<?>> classNameResolver =
        new ClassNameResolverImpl() ;

    @Osgi
    public static UnaryFunction<String,Class<?>> classNameResolver() {
        return classNameResolver ;
    }

    @Osgi
    private static class ClassCodeBaseHandlerImpl implements ClassCodeBaseHandler {
        private static final String PREFIX = "osgi://" ;

        @InfoMethod
        private void classNotFoundInBundle( String name ) {}

        @InfoMethod
        private void foundClassInBundleVersion( Class<?> cls, String name,
            String version ) {}

        @Osgi
        public String getCodeBase( Class<?> cls ) {
            if (cls == null) {
                return null ;
            }

            if (pkgAdmin == null) {
                return null ;
            }

            Bundle bundle = pkgAdmin.getBundle( cls ) ;
            if (bundle == null) {
                classNotFoundInBundle( cls.getName() ) ;
                return null ;
            }
            
            String name = bundle.getSymbolicName() ;

            Dictionary headers = secureGetHeaders( bundle ) ;
            String version = "0.0.0" ;
            if (headers != null) {
                String hver = (String)headers.get( "Bundle-Version" ) ;
                if (hver != null) {
                    version = hver;
                }
            }

            foundClassInBundleVersion( cls, name, version ) ;

            return PREFIX + name + "/" + version ;
        }

        @InfoMethod
        private void couldNotLoadClassInBundle( ClassNotFoundException exc,
            String className, String bname ) {}

        @InfoMethod
        private void foundClassInBundleVersion( String cname,
            String bname, String version ) {}

        @InfoMethod
        private void classNotFoundInBundleVersion( String cname,
            String bname, String version ) {}

        @Osgi
        public Class<?> loadClass( String codebase, String className ) {
            if (codebase == null) {
                Bundle bundle = getBundleForClass( className ) ;
                if (bundle != null) {
                    try {
                        return secureLoadClass( bundle, className ) ;
                    } catch (ClassNotFoundException exc) {
                        couldNotLoadClassInBundle( exc, className,
                            bundle.getSymbolicName() ) ;
                        return null ;
                    }
                } else {
                    return null ;
                }
            }

            if (codebase.startsWith( PREFIX )) {
                String rest = codebase.substring( PREFIX.length() ) ;
                int index = rest.indexOf( '/') ;
                if (index > 0) {
                    String name = rest.substring( 0, index ) ;
                    String version = rest.substring( index+1 ) ;
                    
                    if (pkgAdmin != null) {
                        Bundle[] defBundles = pkgAdmin.getBundles( name,
                            version ) ;
                        if (defBundles != null) {
                            
                            try {
                                foundClassInBundleVersion(
                                    className, name, version ) ;
                                return secureLoadClass( defBundles[0], className ) ;
                            } catch (ClassNotFoundException cnfe) {
                                classNotFoundInBundleVersion(
                                    className, name, version ) ;
                                
                            }
                        }
                    }
                }
            }

            return null ;
        }
    }

    private static ClassCodeBaseHandler ccbHandler =
        new ClassCodeBaseHandlerImpl() ;

    @Osgi
    public static ClassCodeBaseHandler classCodeBaseHandler() {
        return ccbHandler ;
    }

    @InfoMethod
    private void insertOrbProvider( String cname, String bname ) {}

    @InfoMethod
    private void insertBundlePackage( String pname, String bname ) {}

    @Osgi
    private void insertClasses( Bundle bundle ) {
        lock.writeLock().lock() ;
        try {
            final Dictionary dict = secureGetHeaders( bundle ) ;
            final String name = bundle.getSymbolicName() ;
            if (dict != null) {
                final String orbProvider = (String)dict.get( ORB_PROVIDER_KEY ) ;
                if (orbProvider != null) {
                    for (String str : orbProvider.split(",") ) {
                        String className = str.trim() ;
                        classNameMap.put( className, bundle ) ;
                        insertOrbProvider( className, name ) ;
                    }
                }
            }

            if (pkgAdmin != null) {
                final ExportedPackage[] epkgs = pkgAdmin.getExportedPackages(
                    bundle ) ;
                if (epkgs != null) {
                    for (ExportedPackage ep : epkgs) {
                        final String pname = ep.getName() ;
                        packageNameMap.put( pname, bundle ) ;
                        insertBundlePackage( pname,
                            bundle.getSymbolicName() ) ;
                    }
                }
            }
        } finally {
            lock.writeLock().unlock() ;
        }
    }

    @InfoMethod
    private void removeOrbProvider( String cname, String bname ) {}

    @InfoMethod
    private void removeBundlePackage( String pname, String bname ) {}

    @Osgi
    private void removeClasses( Bundle bundle ) {
        lock.writeLock().lock() ;
        try {
            final Dictionary dict = secureGetHeaders( bundle ) ;
            final String name = bundle.getSymbolicName() ;
            if (dict != null) {
                final String orbProvider = (String)dict.get( ORB_PROVIDER_KEY ) ;
                if (orbProvider != null) {
                    for (String className : orbProvider.split(",") ) {
                        classNameMap.remove( className ) ;
                        removeOrbProvider( className, name ) ;
                    }
                }
            }

            if (pkgAdmin != null) {
                final ExportedPackage[] epkgs = pkgAdmin.getExportedPackages(
                    bundle ) ;
                if (epkgs != null) {
                    for (ExportedPackage ep : epkgs) {
                        final String pname = ep.getName() ;
                        packageNameMap.remove( pname ) ;
                        removeBundlePackage( pname,
                            bundle.getSymbolicName() ) ;
                    }
                }
            }
        } finally {
            lock.writeLock().unlock() ;
        }
    }

    @Osgi
    private static Bundle getBundleForClass( String className ) {
        lock.readLock().lock() ;
        try {
            Bundle result = classNameMap.get( className ) ;
            if (result == null) {
                
                final int index = className.lastIndexOf( '.') ;
                if (index > 0) {
                    final String packageName = className.substring( 0, index ) ;
                    result = packageNameMap.get( packageName ) ;
                }
            }

            return result ;
        } finally {
            lock.readLock().unlock() ;
        }
    }

    @InfoMethod
    private void probeBundlesForProviders() {}

    @Osgi
    public void start( BundleContext context ) {
        
        
        final ServiceReference sref = context.getServiceReference( 
            "org.osgi.service.packageadmin.PackageAdmin" ) ;
        setPackageAdmin( (PackageAdmin)context.getService( sref ) ) ;

        if (pkgAdmin == null) {
            wrapper.packageAdminServiceNotAvailable() ;
        }

        context.addBundleListener(this);
        
        
        probeBundlesForProviders() ;
        for (Bundle bundle : context.getBundles()) {
            insertClasses( bundle ) ;
        }
    }

    @Osgi
    public void stop( BundleContext context ) {
        final Bundle myBundle = context.getBundle() ;
        removeClasses( myBundle ) ;
    }

    @InfoMethod
    private void receivedBundleEvent( String type, String name ) {}

    @Osgi
    public void bundleChanged(BundleEvent event) {
        final int type = event.getType() ;
        final Bundle bundle = event.getBundle() ;
        final String name = bundle.getSymbolicName() ;

        receivedBundleEvent( getBundleEventType( type ), name ) ;

        if (type == Bundle.INSTALLED) {
            insertClasses( bundle ) ;
        } else if (type == Bundle.UNINSTALLED) {
            removeClasses( bundle ) ;
        }
    }
}
