

package com.sun.corba.ee.spi.protocol;


public abstract class ClientDelegate
    extends org.omg.CORBA_2_3.portable.Delegate 
{
    
    public abstract ORB getBroker();

    
    public abstract ContactInfoList getContactInfoList();
}


