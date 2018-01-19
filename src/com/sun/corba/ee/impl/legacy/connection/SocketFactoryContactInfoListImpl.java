


package com.sun.corba.ee.impl.legacy.connection;

import java.util.Iterator;

import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.impl.transport.ContactInfoListImpl;
import com.sun.corba.ee.impl.transport.ContactInfoListIteratorImpl;


public class SocketFactoryContactInfoListImpl 
    extends
        ContactInfoListImpl
{
    
    public SocketFactoryContactInfoListImpl(ORB orb)
    {
        super(orb);
    }

    public SocketFactoryContactInfoListImpl(ORB orb, IOR targetIOR)
    {
        super(orb, targetIOR);
    }

    public Iterator iterator()
    {
        return new SocketFactoryContactInfoListIteratorImpl(orb, this);
    }
}


