


package com.sun.corba.ee.impl.presentation.rmi.proxy;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.sun.corba.ee.impl.presentation.rmi.*;
import com.sun.corba.ee.impl.presentation.rmi.proxy.StubFactoryProxyImpl;
import com.sun.corba.ee.spi.presentation.rmi.PresentationManager ;
import com.sun.corba.ee.spi.presentation.rmi.PresentationManager.StubFactory;

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
