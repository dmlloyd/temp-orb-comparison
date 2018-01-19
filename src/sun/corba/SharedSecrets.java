

package xxxx;





public class SharedSecrets {

    private static JavaCorbaAccess javaCorbaAccess;

    public static JavaCorbaAccess getJavaCorbaAccess() {
        if (javaCorbaAccess == null) {
            ValueUtility.initializeJavaCorbaAccess();
        }
        return javaCorbaAccess;
    }

    public static void setJavaCorbaAccess(JavaCorbaAccess access) {
        javaCorbaAccess = access;
    }

}
