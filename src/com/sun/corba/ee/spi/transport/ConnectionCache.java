


package com.sun.corba.ee.spi.transport;

import org.glassfish.external.statistics.CountStatistic ;


public interface ConnectionCache
{
    public String getMonitoringName();

    public String getCacheType();

    public void stampTime(Connection connection);

    public long numberOfConnections();

    public long numberOfIdleConnections();

    public long numberOfBusyConnections();

    public boolean reclaim();

    
    public void close() ;
}


