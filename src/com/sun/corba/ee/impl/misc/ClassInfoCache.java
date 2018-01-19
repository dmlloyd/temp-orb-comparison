


package xxxx;










public class ClassInfoCache {
    
    
    public static class ClassInfo {

        public static class LazyWrapper {
            Class<?> isAClass ;
            boolean initialized ;
            boolean value ;

            public LazyWrapper( Class<?> isAClass ) {
                this.isAClass = isAClass ;
                this.initialized = false ;
                this.value = false ;
            }

            synchronized boolean get( Class<?> cls ) {
                if (!initialized) {
                    initialized = true ;
                    value = isAClass.isAssignableFrom( cls ) ;
                }

                return value ;
            }
        }

        private boolean isAValueBase ;
        private boolean isAString ;
        private boolean isAIDLEntity ;

        private LazyWrapper isARemote = new LazyWrapper( 
            Remote.class ) ;
        private LazyWrapper isARemoteException = new LazyWrapper( 
            RemoteException.class ) ;
        private LazyWrapper isAUserException = new LazyWrapper( 
            UserException.class ) ;
        private LazyWrapper isAObjectImpl = new LazyWrapper( 
            ObjectImpl.class ) ;
        private LazyWrapper isAORB = new LazyWrapper( 
            ORB.class ) ;
        private LazyWrapper isAStreamable = new LazyWrapper( 
            Streamable.class ) ;
        private LazyWrapper isAStreamableValue = new LazyWrapper( 
            StreamableValue.class ) ;
        private LazyWrapper isACustomValue = new LazyWrapper( 
            CustomValue.class ) ;
        private LazyWrapper isACORBAObject = new LazyWrapper( 
            org.omg.CORBA.Object.class ) ;
        private LazyWrapper isASerializable = new LazyWrapper( 
            Serializable.class ) ;
        private LazyWrapper isAExternalizable = new LazyWrapper( 
            Externalizable.class ) ;
        private LazyWrapper isAClass = new LazyWrapper( 
            Class.class ) ;

        private String repositoryId = null ;

        private boolean isArray ;
        private boolean isEnum ;
        private boolean isInterface ;
        private boolean isProxyClass ;
        private ClassInfo superInfo ;

        ClassInfo( Class<?> cls ) {
            isArray = cls.isArray() ;
            isEnum = isEnum(cls) ;
            isInterface = cls.isInterface() ;
            isProxyClass = Proxy.isProxyClass( cls ) ;

            isAValueBase = ValueBase.class.isAssignableFrom( cls ) ;
            isAString = String.class.isAssignableFrom( cls ) ;
            isAIDLEntity = IDLEntity.class.isAssignableFrom( cls ) ;

            Class<?> superClass = cls.getSuperclass() ;
            if (superClass != null) {
                superInfo = ClassInfoCache.get( superClass ) ;
            }
        }

        private boolean isEnum(Class<?> cls) {
            
            
            
            
            
            Class<?> current = cls ;
            while (current != null) {
                if (current.equals( Enum.class )) {
                    return true ;
                }
                current = current.getSuperclass() ;
            }

            return false ;
        }
        
        public synchronized String getRepositoryId() {
            return repositoryId ;
        }

        public synchronized void setRepositoryId( String repositoryId ) {
            this.repositoryId = repositoryId ;
        }

        public boolean isARemote( Class<?> cls ) { 
            return isARemote.get(cls) ; 
        }
        public boolean isARemoteException( Class<?> cls ) { 
            return isARemoteException.get(cls) ; 
        }
        public boolean isAUserException( Class<?> cls ) { 
            return isAUserException.get(cls) ; 
        }
        public boolean isAObjectImpl( Class<?> cls ) { 
            return isAObjectImpl.get(cls) ; 
        }
        public boolean isAORB( Class<?> cls ) { 
            return isAORB.get(cls) ; 
        }
        public boolean isAIDLEntity( Class<?> cls ) { 
            return isAIDLEntity ; 
        }
        public boolean isAStreamable( Class<?> cls ) { 
            return isAStreamable.get(cls) ; 
        }
        public boolean isAStreamableValue( Class<?> cls ) { 
            return isAStreamableValue.get(cls) ; 
        }
        public boolean isACustomValue( Class<?> cls ) { 
            return isACustomValue.get(cls) ; 
        }
        public boolean isAValueBase( Class<?> cls ) { 
            return isAValueBase ; 
        }
        public boolean isACORBAObject( Class<?> cls ) { 
            return isACORBAObject.get(cls) ; 
        }
        public boolean isASerializable( Class<?> cls ) { 
            return isASerializable.get(cls) ; 
        }
        public boolean isAExternalizable( Class<?> cls ) { 
            return isAExternalizable.get(cls) ; 
        }
        public boolean isAString( Class<?> cls ) { 
            return isAString ; 
        }
        public boolean isAClass( Class<?> cls ) { 
            return isAClass.get(cls) ; 
        }

        public boolean isArray() { return isArray ; }
        public boolean isEnum() { return isEnum ; }
        public boolean isInterface() { return isInterface ; }
        public boolean isProxyClass() { return isProxyClass ; }
        public ClassInfo getSuper() { return superInfo ; }
    }

    
    
    

    

    private static Map<Class,ClassInfo> classData = new WeakHashMap<Class,ClassInfo>() ;

    public static synchronized ClassInfo get( Class<?> cls ) {
        ClassInfo result = classData.get( cls ) ;
        if (result == null && cls != null) {
            result = new ClassInfo( cls ) ;
            classData.put( cls, result ) ;
        }

        return result ;
    }

    
    public static Class getEnumClass( ClassInfo cinfo, Class cls ) {
        ClassInfo currInfo = cinfo ;
        Class currClass = cls ;
        while (currClass != null) { 
            if (currClass.isEnum()) {
                break ;
            }

            currClass = currClass.getSuperclass() ;
            currInfo = currInfo.getSuper() ;
        }

        return currClass ;
    }
}
