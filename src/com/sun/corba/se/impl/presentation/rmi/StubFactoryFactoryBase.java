

package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

import com.sun.corba.se.impl.util.Utility ;

public abstract class StubFactoryFactoryBase implements
    PresentationManager.StubFactoryFactory
{
    
    public String getStubName(String fullName)
    {
        return Utility.stubName( fullName ) ;
    }
}
