


package xxxx;




public abstract interface ContactInfo extends SocketInfo
{
    public ContactInfoList getContactInfoList() ;
    public IOR getTargetIOR();
    public IOR getEffectiveTargetIOR();
    public IIOPProfile getEffectiveProfile(); 
    public void setAddressingDisposition(short addressingDisposition);
    public short getAddressingDisposition();
    public String getMonitoringName();

    public ORB getBroker();

    public ClientRequestDispatcher getClientRequestDispatcher();

    
    public boolean isConnectionBased();

    
    public boolean shouldCacheConnection();

    public String getConnectionCacheType();

    public void setConnectionCache(OutboundConnectionCache connectionCache);

    public OutboundConnectionCache getConnectionCache();

    public Connection createConnection();

    public MessageMediator createMessageMediator(ORB broker,
        ContactInfo contactInfo, Connection connection,
        String methodName, boolean isOneWay);

    public CDROutputObject createOutputObject(MessageMediator messageMediator);

    
    public int hashCode();
}


