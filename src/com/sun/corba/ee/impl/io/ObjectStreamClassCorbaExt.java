


package xxxx;

















class ObjectStreamClassCorbaExt {

    
    static final boolean isAbstractInterface(Class cl) {
        ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( cl ) ;
        if (!cinfo.isInterface() || cinfo.isARemote(cl)) {
            return false;
        }

        Method[] methods = cl.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Class exceptions[] = methods[i].getExceptionTypes();
            boolean exceptionMatch = false;
            for (int j = 0; (j < exceptions.length) && !exceptionMatch; j++) {
                if ((java.rmi.RemoteException.class == exceptions[j]) ||
                    (java.lang.Throwable.class == exceptions[j]) ||
                    (java.lang.Exception.class == exceptions[j]) ||
                    (java.io.IOException.class == exceptions[j])) {
                    exceptionMatch = true;
                }
            }
            if (!exceptionMatch) {
                return false;
            }
        }

        return true;
    }

    
    
    
    
    private static final String objectString         = "Ljava/lang/Object;" ;
    private static final String serializableString   = "Ljava/io/Serializable;" ;
    private static final String externalizableString = "Ljava/io/Externalizable;" ;

    
    private static final int objectLength = objectString.length() ;
    private static final int serializableLength = serializableString.length() ;
    private static final int externalizableLength = externalizableString.length() ;

    private static final boolean debugIsAny = false ;

    
    static final boolean isAny(String typeString) {
        if (debugIsAny) {
            ORBUtility.dprint( 
                ObjectStreamClassCorbaExt.class.getName(), 
                "IsAny: typeString = " + typeString ) ;
        }

        int length = typeString.length() ;

        if (length == objectLength) {
            
            
            if (typeString.charAt(length-2) == 't')
                return objectString.equals( typeString ) ;
            else
                return false ;
        }

        if (length == serializableLength) {
            
            
            if (typeString.charAt(length-2) == 'e')
                return serializableString.equals( typeString ) ;
            else 
                return false ;
        }

        if (length == externalizableLength)
            return externalizableString.equals( typeString ) ;

        return false ;
    }

    private static final Method[] getDeclaredMethods(final Class clz) {
        return AccessController.doPrivileged(
            new PrivilegedAction<Method[]>() {
                public Method[] run() {
                    return clz.getDeclaredMethods();
                }
            }
        );
    }

}
