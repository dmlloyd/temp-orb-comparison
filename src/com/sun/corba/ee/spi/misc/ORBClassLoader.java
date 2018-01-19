

package xxxx;


public class ORBClassLoader
{
    public static Class loadClass(String className) 
        throws ClassNotFoundException
    {
        return getClassLoader().loadClass(className);
    }

    public static ClassLoader getClassLoader() 
    {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader() ;
        if (ccl != null)
            return ccl; 
        else
            return ClassLoader.getSystemClassLoader();
    }
}
