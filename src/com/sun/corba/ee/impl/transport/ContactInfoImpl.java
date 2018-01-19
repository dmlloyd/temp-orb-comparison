


package com.sun.corba.ee.impl.transport;





public class ContactInfoImpl
    extends ContactInfoBase
    implements SocketInfo
{
    protected boolean isHashCodeCached = false;
    protected int cachedHashCode;

    protected String socketType;
    protected String hostname;
    protected int    port;

    
    
    
    
    protected ContactInfoImpl()
    {
    }

    protected ContactInfoImpl(
        ORB orb,
        ContactInfoList contactInfoList)
    {
        this.orb = orb;
        this.contactInfoList = contactInfoList;
    }

    public ContactInfoImpl(
        ORB orb,
        ContactInfoList contactInfoList,
        String socketType,
        String hostname,
        int port)
    {
        this(orb, contactInfoList);
        this.socketType = socketType;
        this.hostname = hostname;
        this.port     = port;
    }

    
    public ContactInfoImpl(
        ORB orb,
        ContactInfoList contactInfoList,
        IOR effectiveTargetIOR,
        short addressingDisposition,
        String socketType,
        String hostname,
        int port)
    {
        this(orb, contactInfoList, socketType, hostname, port);
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;
    }

    public boolean isConnectionBased()
    {
        return true;
    }

    public boolean shouldCacheConnection()
    {
        return true;
    }

    public String getConnectionCacheType()
    {
        return TransportManager.SOCKET_OR_CHANNEL_CONNECTION_CACHE;
    }

    public Connection createConnection()
    {
        Connection connection =
            new ConnectionImpl(orb, this,
                                              socketType, hostname, port);
        return connection;
    }

    
    
    
    

    public String getMonitoringName()
    {
        return "SocketConnections";
    }

    public String getType()
    {
        return socketType;
    }

    public String getHost()
    {
        return hostname;
    }

    public int getPort()
    {
        return port;
    }

    
    
    
    

    
    
    
    
    @Override
    public int hashCode() 
    {
        if (! isHashCodeCached) {
            cachedHashCode = socketType.hashCode() ^ hostname.hashCode() ^ port;
            isHashCodeCached = true;
        }
        return cachedHashCode;
    }

    
    
    
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof ContactInfoImpl)) {
            return false;
        }

        ContactInfoImpl other =
            (ContactInfoImpl) obj;

        if (port != other.port) {
            return false;
        }
        if (!hostname.equals(other.hostname)) {
            return false;
        }
        if (socketType == null) {
            if (other.socketType != null) {
                return false;
            }
        } else if (!socketType.equals(other.socketType)) {
            return false;
        }
        return true;
    }

    public String toString()
    {
        return
            "SocketOrChannelContactInfoImpl[" 
            + socketType + " "
            + hostname + " "
            + port
            + "]";
    }

    
    
    
    

    protected void dprint(String msg) 
    {
        ORBUtility.dprint("SocketOrChannelContactInfoImpl", msg);
    }
}


