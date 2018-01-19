



package com.sun.corba.ee.internal.io;

public class LibraryManager
{
    native private static int getMajorVersion();

    native private static int getMinorVersion();

    private static native boolean setEnableOverride(Class targetClass, Object instance);
}
