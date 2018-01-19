



package com.sun.corba.ee.impl.util;









public class JDKBridge {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;
    private static Logger logger = Logger.getLogger(JDKBridge.class.getName()) ;

    
    public static String getLocalCodebase () {
        return localCodebase;
    }
  
    
    public static boolean useCodebaseOnly () {
        return useCodebaseOnly;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    private static class LoadClassCache {
        private static Map<String,Map<String,Entry>> nullLoaderMap =
            new HashMap<String,Map<String,Entry>>() ;
        private static Map<ClassLoader,Map<String,Entry>> nonNullLoaderMap =
            new WeakHashMap<ClassLoader,Map<String,Entry>>() ;
        private static ReferenceQueue<Class> queue =
            new ReferenceQueue<Class>() ;

        private static class Entry extends SoftReference<Class> {
            String codeBase ;
            ClassLoader loader ;

            public Entry( Class cls, String codeBase, ClassLoader loader ) {
                super( cls, queue ) ;
                this.codeBase = codeBase ;
                this.loader = loader ;
            }

            @Override
            public void clear() {
                codeBase = null ;
                loader = null ;
            }
        }
 
        private static void checkQueue() {
            while (true) {
                Object obj = queue.poll() ;
                if (obj == null) {
                    return ;
                } else {
                    Entry entry = (Entry)obj ;
                    String className = entry.get().getName() ;
                    if (entry.loader == null) {
                        Map<String,Entry> mse = nullLoaderMap.get( entry.codeBase ) ;
                        mse.remove( className ) ;
                        if (mse.isEmpty()) {
                            nullLoaderMap.remove( entry.codeBase ) ;
                        }
                    } else {
                        Map<String,Entry> mse = nonNullLoaderMap.get( entry.loader ) ;
                        mse.remove( className ) ;
                        if (mse.isEmpty()) {
                            nonNullLoaderMap.remove( entry.loader ) ;
                        }
                    }
                    entry.clear() ;
                }
            } 
        }

        
        public static synchronized Class get( String className, String remoteCodebase, 
            ClassLoader loader ) throws ClassNotFoundException {
            
            checkQueue() ;

            Map<String,Entry> scm ;
            if (loader == null) {
                scm = nullLoaderMap.get( remoteCodebase ) ;
            } else {
                scm = nonNullLoaderMap.get( loader ) ;
            }

            Class cls = null ;
            if (scm != null) {
                Entry entry = scm.get( className ) ;
                if (entry != null)
                    cls = entry.get() ;
            }

            return cls ;
        }

        public static synchronized void put( String className, String remoteCodebase, 
            ClassLoader loader, Class cls ) {
            
            checkQueue() ;

            Map<String,Entry> scm ;
            if (loader == null) {
                scm = nullLoaderMap.get( remoteCodebase ) ;
                if (scm == null) {
                    scm = new HashMap<String,Entry>() ;
                    nullLoaderMap.put( remoteCodebase, scm ) ;
                }
            } else {
                scm = nonNullLoaderMap.get( loader ) ;
                if (scm == null) {
                    scm = new HashMap<String,Entry>() ;
                    nonNullLoaderMap.put( loader, scm ) ;
                }
            }

            scm.put( className, new Entry( cls, remoteCodebase, loader ) ) ;
        }
    }

    
    public static Class loadClass (String className,
                                   String remoteCodebase,
                                   ClassLoader loader)
        throws ClassNotFoundException {
        
        
        
        
        
        Class cls = null ; 
        
        if (cls == null) {
            if (loader == null) {
                cls = loadClassM(className,remoteCodebase,useCodebaseOnly);
            } else {
                try {
                    cls = loadClassM(className,remoteCodebase,useCodebaseOnly);
                } catch (ClassNotFoundException e) {
                    
                    
                    if (logger.isLoggable(Level.FINE)) {
                        wrapper.classNotFoundInCodebase( className, remoteCodebase ) ;
                    }
                    cls = loader.loadClass(className);
                }
            }
            
        }

        return cls ;
    }
    
    
    public static Class loadClass (String className,
                                   String remoteCodebase)
        throws ClassNotFoundException {
        return loadClass(className,remoteCodebase,null);
    }
    
    
    public static Class loadClass (String className)
        throws ClassNotFoundException {
        return loadClass(className,null,null);
    }

    private static final String LOCAL_CODEBASE_KEY = "java.rmi.server.codebase";
    private static final String USE_CODEBASE_ONLY_KEY = "java.rmi.server.useCodebaseOnly";
    private static String localCodebase = null;
    private static boolean useCodebaseOnly;

    static {
        setCodebaseProperties();
    }
 
    
    public static synchronized void setCodebaseProperties () {
        String prop = (String)AccessController.doPrivileged(
            new GetPropertyAction(LOCAL_CODEBASE_KEY));

        if (prop != null && prop.trim().length() > 0) {
            localCodebase = prop;
        }

        prop = (String)AccessController.doPrivileged(
            new GetPropertyAction(USE_CODEBASE_ONLY_KEY));

        if (prop != null && prop.trim().length() > 0) {
            useCodebaseOnly = Boolean.valueOf(prop).booleanValue();
        }
    }

    
    public static synchronized void setLocalCodebase(String codebase) {
        localCodebase = codebase;    
    }
 
    private static Class loadClassM (String className, String remoteCodebase, 
        boolean useCodebaseOnly) throws ClassNotFoundException {

        try {
            return JDKClassLoader.loadClass(null,className);
        } catch (ClassNotFoundException e) {
            
            
            if (logger.isLoggable(Level.FINE)) {
                wrapper.classNotFoundInJDKClassLoader( className, e ) ;
            }
        }

        try {
            if (!useCodebaseOnly && remoteCodebase != null) {
                return RMIClassLoader.loadClass(remoteCodebase,
                                                className);
            } else {
                return RMIClassLoader.loadClass(className);
            }
        } catch (MalformedURLException e) {
            className = className + ": " + e.toString();
        }

        throw new ClassNotFoundException(className);
    }
}

