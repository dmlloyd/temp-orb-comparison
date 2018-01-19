

package xxxx;




public abstract class StubFactoryDynamicBase extends StubFactoryBase
{
    protected final ClassLoader loader ;

    private static Void checkPermission() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new SerializablePermission(
                    "enableSubclassImplementation"));
        }
        return null;
    }

    private StubFactoryDynamicBase(Void unused,
            PresentationManager.ClassData classData, ClassLoader loader) {
        super(classData);
        
        
        if (loader == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null)
                cl = ClassLoader.getSystemClassLoader();
            this.loader = cl ;
        } else {
            this.loader = loader ;
        }
    }

    public StubFactoryDynamicBase( PresentationManager.ClassData classData,
        ClassLoader loader )
    {
        this (checkPermission(), classData, loader);
    }

    public abstract org.omg.CORBA.Object makeStub() ;
}
