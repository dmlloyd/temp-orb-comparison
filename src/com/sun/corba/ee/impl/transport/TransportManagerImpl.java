


package xxxx;












@Transport
public class TransportManagerImpl
    implements
        TransportManager
{
    protected ORB orb;
    protected List<Acceptor> acceptors;
    protected final Map<String,OutboundConnectionCache> outboundConnectionCaches;
    protected final Map<String,InboundConnectionCache> inboundConnectionCaches;
    protected Selector selector;
    
    public TransportManagerImpl(ORB orb)
    {
        this.orb = orb;
        acceptors = new ArrayList<Acceptor>();
        outboundConnectionCaches = new HashMap<String,OutboundConnectionCache>();
        inboundConnectionCaches = new HashMap<String,InboundConnectionCache>();
        selector = new SelectorImpl(orb);
        orb.mom().register( orb, this ) ;
    }

    public ByteBufferPool getByteBufferPool(int id)
    {
        throw new RuntimeException(); 
    }

    public OutboundConnectionCache getOutboundConnectionCache(
        ContactInfo contactInfo)
    {
        synchronized (contactInfo) {
            if (contactInfo.getConnectionCache() == null) {
                OutboundConnectionCache connectionCache = null;
                synchronized (outboundConnectionCaches) {
                    connectionCache = outboundConnectionCaches.get(
                        contactInfo.getConnectionCacheType());
                    if (connectionCache == null) {
                        
                        
                        connectionCache = 
                            new OutboundConnectionCacheImpl(orb,
                                                                 contactInfo);

                        
                        
                        orb.mom().register( this, connectionCache ) ;
                        StatsProviderManager.register( "orb", PluginPoint.SERVER,
                            "orb/transport/connectioncache/outbound", connectionCache ) ;

                        outboundConnectionCaches.put(
                            contactInfo.getConnectionCacheType(),
                            connectionCache);
                    }
                }
                contactInfo.setConnectionCache(connectionCache);
            }
            return contactInfo.getConnectionCache();
        }
    }

    public Collection<OutboundConnectionCache> getOutboundConnectionCaches()
    {
        return outboundConnectionCaches.values();
    }

    public Collection<InboundConnectionCache> getInboundConnectionCaches()
    {
        return inboundConnectionCaches.values();
    }

    public InboundConnectionCache getInboundConnectionCache(
        Acceptor acceptor)
    {
        synchronized (acceptor) {
            if (acceptor.getConnectionCache() == null) {
                InboundConnectionCache connectionCache = null;
                synchronized (inboundConnectionCaches) {
                    connectionCache = inboundConnectionCaches.get(
                            acceptor.getConnectionCacheType());
                    if (connectionCache == null) {
                        
                        
                        connectionCache = 
                            new InboundConnectionCacheImpl(orb,
                                                                acceptor);
                        orb.mom().register( this, connectionCache ) ;
                        StatsProviderManager.register( "orb", PluginPoint.SERVER,
                            "orb/transport/connectioncache/inbound", connectionCache ) ;

                        inboundConnectionCaches.put(
                            acceptor.getConnectionCacheType(),
                            connectionCache);
                    }
                }
                acceptor.setConnectionCache(connectionCache);
            }
            return acceptor.getConnectionCache();
        }
    }

    public Selector getSelector() {
        return selector ;
    }

    public Selector getSelector(int id) 
    {
        return selector;
    }

    @Transport
    public synchronized void registerAcceptor(Acceptor acceptor) {
        acceptors.add(acceptor);
    }

    @Transport
    public synchronized void unregisterAcceptor(Acceptor acceptor) {
        acceptors.remove(acceptor);
    }

    @Transport
    public void close()
    {
        for (OutboundConnectionCache cc : outboundConnectionCaches.values()) {
            StatsProviderManager.unregister( cc ) ;
            cc.close() ;
        }
        for (InboundConnectionCache cc : inboundConnectionCaches.values()) {
            StatsProviderManager.unregister( cc ) ;
            cc.close() ;
        }
        getSelector(0).close();
    }

    
    
    
    

    public Collection<Acceptor> getAcceptors() {
        return getAcceptors( null, null ) ;
    }

    @InfoMethod
    private void display( String msg ) { }

    @Transport
    public Collection<Acceptor> getAcceptors(String objectAdapterManagerId,
                                   ObjectAdapterId objectAdapterId)
    {
        

        
        
        for (Acceptor acc : acceptors) {
            if (acc.initialize()) {
                display( "initializing acceptors" ) ;
                if (acc.shouldRegisterAcceptEvent()) {
                    orb.getTransportManager().getSelector(0)
                        .registerForEvent(acc.getEventHandler());
                }
            }
        }
        return acceptors;
    }

    
    @Transport
    public void addToIORTemplate(IORTemplate iorTemplate, 
                                 Policies policies,
                                 String codebase,
                                 String objectAdapterManagerId,
                                 ObjectAdapterId objectAdapterId)
    {
        Iterator iterator = 
            getAcceptors(objectAdapterManagerId, objectAdapterId).iterator();
        while (iterator.hasNext()) {
            Acceptor acceptor = (Acceptor) iterator.next();
            acceptor.addToIORTemplate(iorTemplate, policies, codebase);
        }
    }

    private ThreadLocal currentMessageTraceManager =
        new ThreadLocal() {
            public Object initialValue() 
            {
                return new MessageTraceManagerImpl( ) ;
            }
        } ;

    public MessageTraceManager getMessageTraceManager() 
    {
        return (MessageTraceManager)(currentMessageTraceManager.get()) ;
    }
}


