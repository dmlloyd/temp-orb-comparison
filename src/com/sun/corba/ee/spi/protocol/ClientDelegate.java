

package com.sun.corba.ee.spi.protocol;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.transport.ContactInfoList;

public abstract class ClientDelegate
    extends org.omg.CORBA_2_3.portable.Delegate 
{
    
    public abstract ORB getBroker();

    
    public abstract ContactInfoList getContactInfoList();
}


