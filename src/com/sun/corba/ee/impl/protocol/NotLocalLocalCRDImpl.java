


package com.sun.corba.ee.impl.protocol;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.portable.ServantObject;

import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcher ;



public class NotLocalLocalCRDImpl implements LocalClientRequestDispatcher
{
    public boolean useLocalInvocation(org.omg.CORBA.Object self)
    {
        return false;
    }

    public boolean is_local(org.omg.CORBA.Object self)
    {
        return false;
    }

    public ServantObject servant_preinvoke(org.omg.CORBA.Object self,
                                           String operation,
                                           Class expectedType)
    {
        
        
        
        return null;
        
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servant)
    {
        
    }
}


