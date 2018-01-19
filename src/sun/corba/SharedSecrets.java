

package sun.corba;

import com.sun.corba.se.impl.io.ValueUtility;




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
