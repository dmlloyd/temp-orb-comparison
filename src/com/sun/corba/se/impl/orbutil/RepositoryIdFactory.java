

package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORB;

public abstract class RepositoryIdFactory
{
    private static final RepIdDelegator currentDelegator
        = new RepIdDelegator();

    
    public static RepositoryIdStrings getRepIdStringsFactory()
    {
        return currentDelegator;
    }

    
    public static RepositoryIdUtility getRepIdUtility()
    {
        return currentDelegator;
    }

}
