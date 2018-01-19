

package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.ContactInfoList;


public interface ClientDelegate
{
    
    public Broker getBroker();

    
    public ContactInfoList getContactInfoList();
}


