


package com.sun.corba.ee.impl.oa ;

import org.omg.CORBA.SystemException ;

import com.sun.corba.ee.spi.oa.NullServant ;

public class NullServantImpl implements NullServant 
{
    private SystemException sysex ;

    public NullServantImpl( SystemException ex ) 
    {
        this.sysex = ex ;
    }

    public SystemException getException()
    {
        return sysex ;
    }
}
