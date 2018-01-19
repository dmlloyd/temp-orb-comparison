


package xxxx;




@IsLocal
public class SocketFactoryContactInfoListIteratorImpl
    extends ContactInfoListIteratorImpl
{
    private SocketInfo socketInfoCookie;

    public SocketFactoryContactInfoListIteratorImpl(
        ORB orb,
        ContactInfoList corbaContactInfoList)
    {
        super(orb, corbaContactInfoList, null, null, false);
    }

    
    
    
    

    @Override
    @IsLocal
    public boolean hasNext()
    {
        return true;
    }

    @Override
    @IsLocal
    public ContactInfo next()
    {
        if (contactInfoList.getEffectiveTargetIOR().getProfile().isLocal()){
            return new SharedCDRContactInfoImpl(
                orb, contactInfoList,
                contactInfoList.getEffectiveTargetIOR(),
                orb.getORBData().getGIOPAddressDisposition());
        } else {
            
            
            return new SocketFactoryContactInfoImpl(
                orb, contactInfoList,
                contactInfoList.getEffectiveTargetIOR(),
                orb.getORBData().getGIOPAddressDisposition(),
                socketInfoCookie);
        }
    }

    @Override
    public boolean reportException(ContactInfo contactInfo,
                                   RuntimeException ex)
    {
        this.failureException = ex;
        if (ex instanceof org.omg.CORBA.COMM_FAILURE) {

            SystemException se = (SystemException) ex;

            if (se.minor == ORBUtilSystemException.CONNECTION_REBIND)
            {
                return true;
            } else {
                if (ex.getCause() instanceof GetEndPointInfoAgainException) {
                    socketInfoCookie = 
                        ((GetEndPointInfoAgainException) ex.getCause())
                        .getEndPointInfo();
                    return true;
                }

                if (se.completed == CompletionStatus.COMPLETED_NO) {
                    if (contactInfoList.getEffectiveTargetIOR() !=
                        contactInfoList.getTargetIOR()) 
                    {
                        
                        contactInfoList.setEffectiveTargetIOR(
                            contactInfoList.getTargetIOR());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}


