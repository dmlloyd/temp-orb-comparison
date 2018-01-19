


package com.sun.corba.ee.impl.presentation.rmi;

import com.sun.corba.ee.spi.presentation.rmi.PresentationManager;

import com.sun.corba.ee.impl.util.Utility ;

public abstract class StubFactoryFactoryBase implements
    PresentationManager.StubFactoryFactory
{
    
    public String getStubName(String fullName) 
    {
        return Utility.stubName( fullName ) ;
    }
}
