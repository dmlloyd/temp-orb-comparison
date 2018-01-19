

package xxxx;






public class SharedSecrets {

    
    private static final Unsafe unsafe = AccessController.doPrivileged(
            (PrivilegedAction<Unsafe>)() -> {
                try {
                    Field field = Unsafe.class.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    return (Unsafe)field.get(null);

                } catch (NoSuchFieldException |IllegalAccessException ex) {
                    throw new InternalError("Unsafe.theUnsafe field not available", ex);
                }
            }
    );

    private static JavaCorbaAccess javaCorbaAccess;

    public static JavaCorbaAccess getJavaCorbaAccess() {
        if (javaCorbaAccess == null) {
            
            
            unsafe.ensureClassInitialized(ValueUtility.class);
        }
        return javaCorbaAccess;
    }

    public static void setJavaCorbaAccess(JavaCorbaAccess access) {
        javaCorbaAccess = access;
    }

}
