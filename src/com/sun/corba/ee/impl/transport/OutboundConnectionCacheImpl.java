


package xxxx;






@Transport
@ManagedObject
@Description( "Cache of connections originated by the ORB" ) 
@AMXMetadata( type="corba-outbound-connection-cache-mon", group="monitoring" )
public class OutboundConnectionCacheImpl
    extends
        ConnectionCacheBase
    implements
        OutboundConnectionCache
{
    protected Map<ContactInfo, Connection> connectionCache;
    private OutboundConnectionCacheProbeProvider pp =
        new OutboundConnectionCacheProbeProvider() ;

    public OutboundConnectionCacheImpl(ORB orb, ContactInfo contactInfo)
    {
        super(orb, contactInfo.getConnectionCacheType(),
              ((ContactInfo)contactInfo).getMonitoringName());
        this.connectionCache = new HashMap<ContactInfo,Connection>();
    }

    @Transport
    public Connection get(ContactInfo contactInfo)
    {
        synchronized (backingStore()) {
            cacheStatisticsInfo();
            return connectionCache.get(contactInfo);
        }
    }
    
    @Transport
    public void put(ContactInfo contactInfo, Connection connection)
    {
        synchronized (backingStore()) {
            connectionCache.put(contactInfo, connection);
            connection.setConnectionCache(this);
            pp.connectionOpenedEvent( contactInfo.toString(), connection.toString() ) ;
            cacheStatisticsInfo();
        }
    }

    @Transport
    public void remove(ContactInfo contactInfo)
    {
        synchronized (backingStore()) {
            if (contactInfo != null) {
                Connection connection = connectionCache.remove(contactInfo);
                pp.connectionClosedEvent( contactInfo.toString(), connection.toString() ) ;
            }
            cacheStatisticsInfo();
        }
    }

    
    
    
    

    public Collection values()
    {
        return connectionCache.values();
    }

    protected Object backingStore()
    {
        return connectionCache;
    }

    @Override
    public String toString()
    {
        return "CorbaOutboundConnectionCacheImpl["
            + connectionCache
            + "]";
    }
}


