

package com.sun.corba.ee.impl.util;

import java.io.File;



public final class PackagePrefixChecker 
{
    private static final String PACKAGE_PREFIX = "org.omg.stub.";
    private static final int PACKAGE_PREFIX_LENGTH = PACKAGE_PREFIX.length() ;

    public static String packagePrefix(){ return PACKAGE_PREFIX;}

    public static String correctPackageName (String p)
    {
        if (isOffendingPackage(p))
            return PACKAGE_PREFIX+p;
        else 
            return p;
    }

    public static boolean isOffendingPackage(String p)
    {
        return p!=null && (p.equals("java") || p.equals("javax")) ;
    }

    public static boolean hasOffendingPrefix(String p)
    {
        return p.startsWith("java.") || p.startsWith("javax.") ;
    }

    public static boolean hasBeenPrefixed(String p)
    {
        return p.startsWith(PACKAGE_PREFIX) ;
    }

    public static String withoutPackagePrefix(String p)
    {
        if (hasBeenPrefixed(p)) 
            return p.substring(PACKAGE_PREFIX_LENGTH) ;
        else 
            return p;
    }
}
