


package com.sun.corba.ee.impl.presentation.rmi.proxy;



public class StubFactoryFactoryProxyImpl extends StubFactoryFactoryDynamicBase 
{
    public PresentationManager.StubFactory makeDynamicStubFactory( 
        PresentationManager pm, final PresentationManager.ClassData classData, 
        final ClassLoader classLoader ) 
    {
        return AccessController.doPrivileged(
        		new PrivilegedAction<PresentationManager.StubFactory>() {

					@Override
					public StubFactory run() {
						return new StubFactoryProxyImpl( classData, classLoader ) ;
					}
        	
        });
    }
}
