


package com.sun.corba.ee.impl.transport;








@Transport
public class ContactInfoListIteratorImpl
    implements
        ContactInfoListIterator
{
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    protected ORB orb;
    protected ContactInfoList contactInfoList;
    protected RuntimeException failureException;
    private boolean usePRLB ;
    protected TcpTimeouts tcpTimeouts ;

    
    protected Iterator<ContactInfo> effectiveTargetIORIterator;
    protected ContactInfo previousContactInfo;
    protected boolean isAddrDispositionRetry;
    protected boolean retryWithPreviousContactInfo;
    protected IIOPPrimaryToContactInfo primaryToContactInfo;
    protected ContactInfo primaryContactInfo;
    protected List<ContactInfo> listOfContactInfos;
    protected TcpTimeouts.Waiter waiter ;
    
    
    protected Set<ContactInfo> failedEndpoints ;
    

    public ContactInfoListIteratorImpl(
        ORB orb,
        ContactInfoList corbaContactInfoList,
        ContactInfo primaryContactInfo,
        List listOfContactInfos,
        boolean usePerRequestLoadBalancing )
    {
        this.orb = orb;
        this.tcpTimeouts = orb.getORBData().getTransportTcpConnectTimeouts() ;
        this.contactInfoList = corbaContactInfoList;
        this.primaryContactInfo = primaryContactInfo;
        if (listOfContactInfos != null) {
            
            
            
            this.effectiveTargetIORIterator = listOfContactInfos.iterator() ;
        }
        
        this.listOfContactInfos = listOfContactInfos;

        this.previousContactInfo = null;
        this.isAddrDispositionRetry = false;
        this.retryWithPreviousContactInfo = false;

        this.failureException = null;

        this.waiter = tcpTimeouts.waiter() ;
        this.failedEndpoints = new HashSet<ContactInfo>() ;

        this.usePRLB = usePerRequestLoadBalancing ;

        if (usePerRequestLoadBalancing) {
            
            primaryToContactInfo = null ;
        } else {
            primaryToContactInfo = orb.getORBData().getIIOPPrimaryToContactInfo();
        }
    }

    @InfoMethod
    private void display( String msg ) { }

    @InfoMethod
    private void display( String msg, Object value ) { }

    @InfoMethod
    private void display( String msg, long value ) { }

    
    
    
    

    @Transport
    public boolean hasNext() {
        boolean result = false;
        if (retryWithPreviousContactInfo) {
            display("backoff before retry previous");

            if (waiter.isExpired()) {
                display("time to wait for connection exceeded " ,
                   tcpTimeouts.get_max_time_to_wait());
                
                
                
                failureException = wrapper.communicationsRetryTimeout(
                    failureException, tcpTimeouts.get_max_time_to_wait());
                return false;
            }

            waiter.sleepTime() ;
            waiter.advance() ;
            return true;
        }

        if (isAddrDispositionRetry) {
            return true;
        }

        if (primaryToContactInfo != null) {
            result = primaryToContactInfo.hasNext( primaryContactInfo, 
                previousContactInfo, listOfContactInfos);
        } else {
            result = effectiveTargetIORIterator.hasNext();
        }

        if (!result && !waiter.isExpired()) {
            display("Reached end of ContactInfoList list. Starting at beginning");

            previousContactInfo = null;
            if (primaryToContactInfo != null) {
                primaryToContactInfo.reset(primaryContactInfo);
            } else {
                
                effectiveTargetIORIterator = listOfContactInfos.iterator() ;
            }

            result = hasNext();
            return result;
        }

        return result;
    }

    @Transport
    public ContactInfo next() {
        if (retryWithPreviousContactInfo) {
            retryWithPreviousContactInfo = false;
            return previousContactInfo;
        }

        if (isAddrDispositionRetry) {
            isAddrDispositionRetry = false;
            return previousContactInfo;
        }

        
        

        

        if (primaryToContactInfo != null) {
            previousContactInfo = (ContactInfo)
                primaryToContactInfo.next(primaryContactInfo,
                                          previousContactInfo,
                                          listOfContactInfos);
        } else {
            previousContactInfo = effectiveTargetIORIterator.next();
        }

        
        
        
        if (failedEndpoints.contains(previousContactInfo)) {
            failedEndpoints.clear() ;
            waiter.sleepTime() ;
            waiter.advance() ;
        }

        return previousContactInfo;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    public ContactInfoList getContactInfoList()
    {
        return contactInfoList;
    }

    @Transport
    public void reportSuccess(ContactInfo contactInfo)
    {
        display( "contactInfo", contactInfo) ;
        failedEndpoints.clear() ;
        waiter.reset() ; 
    }

    @Transport
    public boolean reportException(ContactInfo contactInfo,
                                   RuntimeException ex) {
        boolean result = false;
        display( "contactInfo", contactInfo) ;

        failedEndpoints.add( contactInfo ) ;
        this.failureException = ex;
        if (ex instanceof COMM_FAILURE) {
            SystemException se = (SystemException) ex;
            if (se.minor == ORBUtilSystemException.CONNECTION_REBIND) {
                display( "COMM_FAILURE(connection rebind): " 
                    + "retry with previous contact info", ex ) ;

                retryWithPreviousContactInfo = true;
                result = true;
                return result;
            } else {
                if (se.completed == CompletionStatus.COMPLETED_NO) {
                    if (hasNext()) {
                        display( "COMM_FAILURE(COMPLETED_NO, hasNext true): "
                            + "retry with next contact info", ex ) ;
                        result = true;
                        return result;
                    }
                    if (contactInfoList.getEffectiveTargetIOR() !=
                        contactInfoList.getTargetIOR()) {
                        display( "COMM_FAILURE(COMPLETED_NO, hasNext false, " +
                            "effective != target): "
                            + "retry with target", ex ) ;

                        
                        updateEffectiveTargetIOR(contactInfoList.getTargetIOR());
                        result = true;
                        return result;
                    }
                }
            }
        } else if (ex instanceof TRANSIENT) {
            display( "TRANSIENT: retry with previous contact info", ex ) ;
            retryWithPreviousContactInfo = true;
            result = true;
            return result;
        }
        result = false;
        waiter.reset() ; 
        return result;
    }

    public RuntimeException getFailureException()
    {
        if (failureException == null) {
            return wrapper.invalidContactInfoListIteratorFailureException() ;
        } else {
            return failureException;
        }
    }

    
    
    
    

    @Transport
    public void reportAddrDispositionRetry(ContactInfo contactInfo,
                                           short disposition)
    {
        previousContactInfo.setAddressingDisposition(disposition);
        isAddrDispositionRetry = true;
        waiter.reset() ; 
    }

    @Transport
    public void reportRedirect(ContactInfo contactInfo,
                               IOR forwardedIOR)
    {
        updateEffectiveTargetIOR(forwardedIOR);
        waiter.reset() ; 
    }

    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    

    public void updateEffectiveTargetIOR(IOR newIOR)
    {
        contactInfoList.setEffectiveTargetIOR(newIOR);
        
        
        
        
        
        
        
        

        
        
        
        
        
        ContactInfoListImpl.setSkipRotate() ;

        ((InvocationInfo)orb.getInvocationInfo())
            .setContactInfoListIterator(contactInfoList.iterator());
    }
}


