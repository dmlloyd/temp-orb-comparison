

package com.sun.corba.se.impl.presentation.rmi;

import java.security.*;



public final class DynamicAccessPermission extends BasicPermission {
    

    
    public DynamicAccessPermission(String name)
    {
        super(name);
    }

    
    public DynamicAccessPermission(String name, String actions)
    {
        super(name, actions);
    }
}
