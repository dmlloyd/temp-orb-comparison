



package xxxx;

public class LibraryManager
{
    native private static int getMajorVersion();

    native private static int getMinorVersion();

    private static native boolean setEnableOverride(Class targetClass, Object instance);
}
