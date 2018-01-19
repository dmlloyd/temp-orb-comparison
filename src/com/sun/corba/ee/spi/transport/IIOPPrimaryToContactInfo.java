


package com.sun.corba.ee.spi.transport;

import java.util.List;

import com.sun.corba.ee.spi.transport.ContactInfo;


public interface IIOPPrimaryToContactInfo
{
    
    public void reset(ContactInfo primary);

    
    public boolean hasNext(ContactInfo primary,
                           ContactInfo previous,
                           List contactInfos);

    
    public ContactInfo next(ContactInfo primary,
                            ContactInfo previous,
                            List contactInfos);

}



