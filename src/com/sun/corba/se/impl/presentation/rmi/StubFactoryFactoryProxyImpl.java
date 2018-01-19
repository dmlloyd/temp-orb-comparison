

package xxxx;


public class StubFactoryFactoryProxyImpl extends StubFactoryFactoryDynamicBase
{
    public PresentationManager.StubFactory makeDynamicStubFactory(
        PresentationManager pm, final PresentationManager.ClassData classData,
        final ClassLoader classLoader )
    {
        return AccessController
                .doPrivileged(new PrivilegedAction<StubFactoryProxyImpl>() {
                    @Override
                    public StubFactoryProxyImpl run() {
                        return new StubFactoryProxyImpl(classData, classLoader);
                    }
                });
    }
}
