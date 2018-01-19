

package xxxx;



public abstract interface ContactInfoListIterator
    extends Iterator<ContactInfo> {

    public void reportAddrDispositionRetry(ContactInfo contactInfo,
                                           short disposition);

    public void reportRedirect(ContactInfo contactInfo,
                               IOR forwardedIOR);

    public ContactInfoList getContactInfoList();

    public void reportSuccess(ContactInfo contactInfo);

    public boolean reportException(ContactInfo contactInfo, RuntimeException exception);

    public RuntimeException getFailureException();

}



