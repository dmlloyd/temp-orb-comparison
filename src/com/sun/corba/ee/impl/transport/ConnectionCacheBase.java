


package com.sun.corba.ee.impl.transport;







    
    
    
    

@Transport
public abstract class ConnectionCacheBase
    implements
        ConnectionCache
{
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;
    private static final String STAT_UNIT = "count" ;

    private static final String TOTAL_ID_STD    = "TotalConnections" ;
    private static final String TOTAL_ID        = "totalconnections" ;
    private static final String IDLE_ID_STD     = "ConnectionsIdle" ;
    private static final String IDLE_ID         = "connectionsidle" ;
    private static final String BUSY_ID_STD     = "ConnectionsBusy" ;
    private static final String BUSY_ID         = "connectionsbusy" ;

    private static final String TOTAL_DESC = 
        "Total number of connections in the connection cache" ; 
    private static final String IDLE_DESC = 
        "Number of connections in the connection cache that are idle" ; 
    private static final String BUSY_DESC =
        "Number of connections in the connection cache that are in use" ; 

    protected ORB orb;
    protected long timestamp = 0;
    protected String cacheType;
    protected String monitoringName;

    protected ConnectionCacheBase(ORB orb, String cacheType,
                                       String monitoringName)
    {
        this.orb = orb;
        this.cacheType = cacheType;
        this.monitoringName = monitoringName;
        dprintCreation();
    }
    
    @NameValue
    public String getCacheType()
    {
        return cacheType;
    }

    public synchronized void stampTime(Connection c)
    {
        
        c.setTimeStamp(timestamp++);
    }

    private CountStatistic  makeCountStat( String name, String desc, 
        long value ) {

        CountStatisticImpl result = new CountStatisticImpl( name,
            STAT_UNIT, desc ) ;
        result.setCount( value ) ;
        return result ;
    }

    public void close() {
        synchronized (backingStore()) {
            for (Object obj : values()) {
                ((Connection)obj).closeConnectionResources() ;
            }
        }
    }

    @ManagedAttribute( id=TOTAL_ID ) 
    @Description( TOTAL_DESC ) 
    private CountStatistic numberOfConnectionsAttr()
    {
        return makeCountStat( TOTAL_ID_STD, TOTAL_DESC, 
            numberOfConnections() ) ;
    }

    public long numberOfConnections()
    {
        long count = 0 ;
        synchronized (backingStore()) {
            count = values().size();
        }

        return count ;
    }

    @ManagedAttribute( id=IDLE_ID ) 
    @Description( IDLE_DESC )
    private CountStatistic numberOfIdleConnectionsAttr()
    {
        return makeCountStat( IDLE_ID_STD, IDLE_DESC, 
            numberOfIdleConnections() ) ;
    }

    public long numberOfIdleConnections()
    {
        long count = 0;
        synchronized (backingStore()) {
            Iterator connections = values().iterator();
            while (connections.hasNext()) {
                if (! ((Connection)connections.next()).isBusy()) {
                    count++;
                }
            }
        }

        return count ;
    }

    @ManagedAttribute( id=BUSY_ID ) 
    @Description( BUSY_DESC )
    private CountStatistic numberOfBusyConnectionsAttr()
    {
        return makeCountStat( BUSY_ID_STD, BUSY_DESC, 
            numberOfBusyConnections() ) ;
    }

    public long numberOfBusyConnections()
    {
        long count = 0;
        synchronized (backingStore()) {
            Iterator connections = values().iterator();
            while (connections.hasNext()) {
                if (((Connection)connections.next()).isBusy()) {
                    count++;
                }
            }
        }
        
        return count ;
    }


    
    @Transport
    synchronized public boolean reclaim() {
        long numberOfConnections = numberOfConnections() ;

        reclaimInfo( numberOfConnections,
            orb.getORBData().getHighWaterMark(),
            orb.getORBData().getNumberToReclaim() ) ;

        if (numberOfConnections <= orb.getORBData().getHighWaterMark()) {
            return false;
        }

        Object backingStore = backingStore();
        synchronized (backingStore) {

                
                

            for (int i=0; i < orb.getORBData().getNumberToReclaim(); i++) {
                Connection toClose = null;
                long lru = java.lang.Long.MAX_VALUE;
                Iterator iterator = values().iterator();

                
                while ( iterator.hasNext() ) {
                    Connection c = (Connection) iterator.next();
                    if ( !c.isBusy() && c.getTimeStamp() < lru ) {
                        toClose = c;
                        lru = c.getTimeStamp();
                    }
                }

                if ( toClose == null ) {
                    return false;
                }

                try {
                    closingInfo( toClose ) ;
                    toClose.close();
                } catch (Exception ex) {
                    
                }
            }

            connectionsReclaimedInfo(
                numberOfConnections - numberOfConnections() );
        }

        return true;
    }

    public String getMonitoringName()
    {
        return monitoringName;
    }

    
    
    
    

    
    public abstract Collection values();

    protected abstract Object backingStore();

    @InfoMethod
    private void creationInfo(String cacheType, String monitoringName) { }

    @Transport
    protected void dprintCreation() {
        creationInfo( getCacheType(), getMonitoringName() ) ;
    }

    @InfoMethod
    private void cacheStatsInfo( long numberOfConnections,
        long numberOfBusyConnections, long numberOfIdleConnections,
        int highWaterMark, int numberToReclaim) { }

    @Transport
    protected void cacheStatisticsInfo() {
        cacheStatsInfo( numberOfConnections(), numberOfBusyConnections(),
            numberOfIdleConnections(), orb.getORBData().getHighWaterMark(),
            orb.getORBData().getNumberToReclaim() ) ;
    }

    @InfoMethod
    private void reclaimInfo(long numberOfConnections, int highWaterMark,
        int numberToReclaim) { }

    @InfoMethod
    private void closingInfo(Connection toClose) { }

    @InfoMethod
    private void connectionsReclaimedInfo(long l) { }
}


