


package com.sun.corba.ee.impl.presentation.rmi.proxy ;

import com.sun.corba.ee.impl.presentation.rmi.*;
import java.lang.reflect.Proxy ;

import com.sun.corba.ee.spi.presentation.rmi.PresentationManager ;
import com.sun.corba.ee.spi.presentation.rmi.DynamicStub ;
import org.glassfish.pfl.basic.proxy.InvocationHandlerFactory;
import org.glassfish.pfl.basic.proxy.LinkedInvocationHandler;

public class StubFactoryProxyImpl extends StubFactoryDynamicBase  
{
    public StubFactoryProxyImpl( PresentationManager.ClassData classData, 
        ClassLoader loader ) 
    {
        super( classData, loader ) ;
    }

    public org.omg.CORBA.Object makeStub() 
    {
        
        
        InvocationHandlerFactory factory = classData.getInvocationHandlerFactory() ;
        LinkedInvocationHandler handler = 
            (LinkedInvocationHandler)factory.getInvocationHandler() ;
        Class[] interfaces = factory.getProxyInterfaces() ;
        DynamicStub stub = (DynamicStub)Proxy.newProxyInstance( loader, interfaces, 
            handler ) ;
        handler.setProxy( (Proxy)stub ) ;
        return stub ;
    }
}
