


package com.sun.corba.ee.impl.presentation.rmi;

import java.lang.reflect.InvocationHandler ;

import com.sun.corba.ee.spi.presentation.rmi.PresentationManager;

public class StubFactoryStaticImpl extends StubFactoryBase
{
    private Class stubClass ;

    public StubFactoryStaticImpl(Class cls)
    {
        super( null ) ;
        this.stubClass = cls;
    }
    
    public org.omg.CORBA.Object makeStub()
    {
        org.omg.CORBA.Object stub = null;
        try {
            stub = (org.omg.CORBA.Object) stubClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return stub ;
    }
}
