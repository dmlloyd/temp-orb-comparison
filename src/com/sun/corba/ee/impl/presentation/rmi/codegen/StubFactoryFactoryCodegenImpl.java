


package com.sun.corba.ee.impl.presentation.rmi.codegen ;

import com.sun.corba.ee.spi.presentation.rmi.PresentationManager ;

import com.sun.corba.ee.impl.presentation.rmi.StubFactoryFactoryDynamicBase ;

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
