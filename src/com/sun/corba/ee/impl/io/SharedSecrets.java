

package com.sun.corba.ee.impl.io;


public class SharedSecrets {
    private static JavaCorbaAccess javaCorbaAccess;

    public static JavaCorbaAccess getJavaCorbaAccess() {
        if (javaCorbaAccess == null) {
            
            
        	try {
				Class.forName(ValueUtility.class.getName(), true, ValueUtility.class.getClassLoader());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
        }

        return javaCorbaAccess;
    }

    public static void setJavaCorbaAccess(JavaCorbaAccess access) {
        javaCorbaAccess = access;
    }

}
