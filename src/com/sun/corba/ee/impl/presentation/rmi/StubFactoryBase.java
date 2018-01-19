


package com.sun.corba.ee.impl.presentation.rmi ;

import javax.rmi.CORBA.Tie ;

import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.Proxy ;

import com.sun.corba.ee.spi.presentation.rmi.PresentationManager ;
import com.sun.corba.ee.spi.presentation.rmi.DynamicStub ;
import com.sun.corba.ee.spi.presentation.rmi.StubAdapter ;

public abstract class StubFactoryBase implements PresentationManager.StubFactory 
{
    private String[] typeIds = null ;

    protected final PresentationManager.ClassData classData ;

    protected StubFactoryBase( PresentationManager.ClassData classData ) 
    {
        this.classData = classData ;
    }

    public synchronized String[] getTypeIds()
    {
        if (typeIds == null) {
            if (classData == null) {
                org.omg.CORBA.Object stub = makeStub() ;
                typeIds = StubAdapter.getTypeIds( stub ) ;
            } else {
                typeIds = classData.getTypeIds() ;
            }
        }

        return typeIds ;
    }
}
