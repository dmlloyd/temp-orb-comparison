

package com.sun.corba.se.pept.transport;



public interface ContactInfoListIterator
    extends
        Iterator
{
    
    public ContactInfoList getContactInfoList();

    
    public void reportSuccess(ContactInfo contactInfo);

    
    public boolean reportException(ContactInfo contactInfo,
                                   RuntimeException exception);

    
    public RuntimeException getFailureException();
}


