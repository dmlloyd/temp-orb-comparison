


package com.sun.corba.ee.spi.presentation.rmi;




public abstract class PresentationDefaults
{
    private static PresentationManager.StubFactoryFactory staticImpl = null;
    private static PresentationManager.StubFactoryFactory dynamicImpl = null;

    private PresentationDefaults() {}

    synchronized static PresentationManager.StubFactoryFactory getDynamicStubFactoryFactory() {
        if (dynamicImpl == null) dynamicImpl = new StubFactoryFactoryCodegenImpl();

        return dynamicImpl ;
    }

    public synchronized static PresentationManager.StubFactoryFactory getStaticStubFactoryFactory() {
        if (staticImpl == null) staticImpl = new StubFactoryFactoryStaticImpl();

        return staticImpl;
    }

    public static PresentationManager.StubFactory makeStaticStubFactory( 
        final Class stubClass )
    {
        return new StubFactoryStaticImpl( stubClass ) ;
    }

    private static InvocationInterceptor nullInvocationInterceptor = 
        new InvocationInterceptor() {
            public void preInvoke() {}
            public void postInvoke() {}
        } ;

    public static InvocationInterceptor getNullInvocationInterceptor() 
    {
        return nullInvocationInterceptor ;
    }
    
    public static boolean inAppServer() {
        final String thisClassRenamed = 
            "com.sun.corba.ee.spi.presentation.rmi.PresentationDefaults" ;
        final boolean inAppServer = 
            PresentationDefaults.class.getName().equals( thisClassRenamed ) ;
        return inAppServer ;
    }

    private static boolean getBooleanPropertyValue( final String propName, 
        final boolean def ) {

        final String defs = Boolean.toString( def ) ;
        final String value = AccessController.doPrivileged(
            new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty( propName, defs ) ;
                }
            }
        ) ;

        return Boolean.valueOf( value ) ;
    }

    public static PresentationManagerImpl makeOrbPresentationManager() {
        final boolean useDynamicStub = getBooleanPropertyValue( 
            ORBConstants.USE_DYNAMIC_STUB_PROPERTY, inAppServer() ) ;

        final boolean debug = getBooleanPropertyValue( 
            ORBConstants.DEBUG_DYNAMIC_STUB, false ) ;

        final PresentationManagerImpl result = new PresentationManagerImpl( useDynamicStub ) ;
        result.setStaticStubFactoryFactory(PresentationDefaults.getStaticStubFactoryFactory());
        result.setDynamicStubFactoryFactory(PresentationDefaults.getDynamicStubFactoryFactory());
        if (debug) {
            result.enableDebug( System.out ) ;
        }

        return result ;
    }
}
