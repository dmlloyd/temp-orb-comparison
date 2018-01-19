


package xxxx;








public abstract class StubFactoryFactoryDynamicBase extends 
    StubFactoryFactoryBase
{
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public StubFactoryFactoryDynamicBase() {
    }

    public PresentationManager.StubFactory createStubFactory(
        String className, boolean isIDLStub, String remoteCodeBase, 
        Class expectedClass, ClassLoader classLoader)
    {
        Class cls = null ;

        try {
            cls = Util.getInstance().loadClass( className, remoteCodeBase, 
                classLoader ) ;
        } catch (ClassNotFoundException exc) {
            throw wrapper.classNotFound3( exc, className ) ;
        }

        ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get(cls) ;
        PresentationManager pm = ORB.getPresentationManager() ;

        if (cinfo.isAIDLEntity(cls) && !cinfo.isARemote(cls)) {
            
            PresentationManager.StubFactoryFactory sff = pm.getStaticStubFactoryFactory();
            return sff.createStubFactory( className, true, remoteCodeBase, expectedClass, classLoader );
        } else {
            PresentationManager.ClassData classData = pm.getClassData( cls ) ;
            return makeDynamicStubFactory( pm, classData, classLoader ) ;
        }
    }

    public abstract PresentationManager.StubFactory makeDynamicStubFactory( 
        PresentationManager pm, PresentationManager.ClassData classData, 
        ClassLoader classLoader ) ;

    public Tie getTie( Class cls )
    {
        PresentationManager pm = ORB.getPresentationManager() ;
        return new ReflectiveTie( pm ) ;
    }

    public boolean createsDynamicStubs() 
    {
        return true ;
    }
}
