


package xxxx;



public class StubFactoryFactoryCodegenImpl extends StubFactoryFactoryDynamicBase 
{
    public StubFactoryFactoryCodegenImpl()
    {
        super() ;
    }

    public PresentationManager.StubFactory makeDynamicStubFactory( 
        PresentationManager pm, PresentationManager.ClassData classData, 
        ClassLoader classLoader ) 
    {
        return new StubFactoryCodegenImpl( pm, classData, classLoader ) ;
    }
}
