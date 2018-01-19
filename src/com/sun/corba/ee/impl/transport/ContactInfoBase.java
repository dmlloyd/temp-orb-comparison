


package com.sun.corba.ee.impl.transport;





@Transport
public abstract class ContactInfoBase
    implements
        ContactInfo
{
    protected ORB orb;
    protected ContactInfoList contactInfoList;
    
    protected IOR effectiveTargetIOR;
    protected short addressingDisposition;
    protected OutboundConnectionCache connectionCache;

    public ORB getBroker()
    {
        return orb;
    }

    public ContactInfoList getContactInfoList()
    {
        return contactInfoList;
    }

    public ClientRequestDispatcher getClientRequestDispatcher()
    {
        int scid =
            getEffectiveProfile().getObjectKeyTemplate().getSubcontractId() ;
        RequestDispatcherRegistry scr = orb.getRequestDispatcherRegistry() ;
        return scr.getClientRequestDispatcher( scid ) ;
    }

    
    
    public void setConnectionCache(OutboundConnectionCache connectionCache)
    {
        this.connectionCache = connectionCache;
    }

    public OutboundConnectionCache getConnectionCache()
    {
        return connectionCache;
    }

    
    @Transport
    public MessageMediator createMessageMediator(ORB broker,
                                                 ContactInfo contactInfo,
                                                 Connection connection,
                                                 String methodName,
                                                 boolean isOneWay)
    {
        
        
        
        
        
        MessageMediator messageMediator =
            new MessageMediatorImpl(
                (ORB) broker,
                (ContactInfo)contactInfo,
                connection,
                GIOPVersion.chooseRequestVersion( (ORB)broker,
                     effectiveTargetIOR),
                effectiveTargetIOR,
                ((Connection)connection).getNextRequestId(),
                getAddressingDisposition(),
                methodName,
                isOneWay);

        return messageMediator;
    }

    @Transport
    public CDROutputObject createOutputObject(MessageMediator messageMediator) {

        CDROutputObject outputObject =
            OutputStreamFactory.newCDROutputObject(orb, messageMediator, 
                                messageMediator.getRequestHeader(),
                                messageMediator.getStreamFormatVersion());

        messageMediator.setOutputObject(outputObject);
        return outputObject;
    }

    
    
    
    

    public short getAddressingDisposition() {
        return addressingDisposition;
    }

    public void setAddressingDisposition(short addressingDisposition) {
        this.addressingDisposition = addressingDisposition;
    }

    
    public IOR getTargetIOR() {
        return  contactInfoList.getTargetIOR();
    }

    public IOR getEffectiveTargetIOR() {
        return effectiveTargetIOR ;
    }

    public IIOPProfile getEffectiveProfile() {
        return effectiveTargetIOR.getProfile();
    }

    
    
    
    

    public String toString() {
        return "CorbaContactInfoBase[" + "]";
    }
}


