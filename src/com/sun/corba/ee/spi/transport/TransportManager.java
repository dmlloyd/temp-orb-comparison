


package xxxx;








@ManagedObject
@Description( "The Transport Manager for the ORB" )
@AMXMetadata( isSingleton=true ) 
public interface TransportManager {

    public ByteBufferPool getByteBufferPool(int id);

    @ManagedAttribute
    @Description( "The Selector, which listens for all I/O events" )
    public Selector getSelector();

    public Selector getSelector(int id);

    public void close();

    public static final String SOCKET_OR_CHANNEL_CONNECTION_CACHE =
        "SocketOrChannelConnectionCache";

    @ManagedAttribute
    @Description( "List of all Acceptors in this ORB" ) 
    public Collection<Acceptor> getAcceptors() ;

    public Collection<Acceptor> getAcceptors(String objectAdapterManagerId,
                                   ObjectAdapterId objectAdapterId);

    
    public void addToIORTemplate(IORTemplate iorTemplate, 
                                 Policies policies,
                                 String codebase,
                                 String objectAdapterManagerId,
                                 ObjectAdapterId objectAdapterId);

    

    
    MessageTraceManager getMessageTraceManager() ;

    public OutboundConnectionCache getOutboundConnectionCache(
        ContactInfo contactInfo);

    @ManagedAttribute
    @Description( "Outbound Connection Cache (client initiated connections)" )
    public Collection<OutboundConnectionCache> getOutboundConnectionCaches();

    public InboundConnectionCache getInboundConnectionCache(Acceptor acceptor);

    
    @ManagedAttribute
    @Description( "Inbound Connection Cache (server accepted connections)" )
    public Collection<InboundConnectionCache> getInboundConnectionCaches();

    public void registerAcceptor(Acceptor acceptor);

    public void unregisterAcceptor(Acceptor acceptor);

}
    

