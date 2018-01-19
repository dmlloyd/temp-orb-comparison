

package com.sun.corba.se.spi.transport ;



public interface CorbaContactInfoListIterator extends ContactInfoListIterator
{
    
    public void reportAddrDispositionRetry(CorbaContactInfo contactInfo,
                                           short disposition);

    public void reportRedirect(CorbaContactInfo contactInfo,
                               IOR forwardedIOR);

}


