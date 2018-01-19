


package xxxx;






@Transport
@ManagedObject
@Description( "Cache of connections accepted by the ORB" ) 
@AMXMetadata( type="corba-inbound-connection-cache-mon", group="monitoring" )
public class InboundConnectionCacheImpl
    extends
        ConnectionCacheBase
    implements
        InboundConnectionCache
{
    protected Collection<Connection> connectionCache;
    private InboundConnectionCacheProbeProvider pp =
        new InboundConnectionCacheProbeProvider() ;

    public InboundConnectionCacheImpl(ORB orb, Acceptor acceptor)
    {
        super(orb, acceptor.getConnectionCacheType(),
              ((Acceptor)acceptor).getMonitoringName());
        this.connectionCache = new ArrayList<Connection>();
    }

    public Connection get(Acceptor acceptor)
    {
        throw wrapper.methodShouldNotBeCalled();
    }
    
    @Transport
    public void put(Acceptor acceptor, Connection connection)
    {
        synchronized (backingStore()) {
            connectionCache.add(connection);
            connection.setConnectionCache(this);
            cacheStatisticsInfo();
            pp.connectionOpenedEvent( acceptor.toString(), connection.toString() ) ;
        }
    }

    @Transport
    public void remove(Connection connection)
    {
        synchronized (backingStore()) {
            connectionCache.remove(connection);
            cacheStatisticsInfo();
            pp.connectionClosedEvent( connection.toString() ) ;
        }
    }

    
    
    
    

    public Collection values()
    {
        return connectionCache;
    }

    protected Object backingStore()
    {
        return connectionCache;
    }
}


