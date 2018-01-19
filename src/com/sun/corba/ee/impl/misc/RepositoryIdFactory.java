


package com.sun.corba.ee.impl.misc;


public abstract class RepositoryIdFactory
{
    private static final RepIdDelegator currentDelegator
        = new RepIdDelegator();

    
    public static RepositoryIdStrings getRepIdStringsFactory()
    {
        return currentDelegator;
    }

    
    public static RepositoryIdStrings getRepIdStringsFactory(ORB orb)
    {
        return currentDelegator;
    }

    
    public static RepositoryIdUtility getRepIdUtility()
    {
        return currentDelegator;
    }

    
    public static RepositoryIdUtility getRepIdUtility(ORB orb)
    {
        return currentDelegator;
    }
}
