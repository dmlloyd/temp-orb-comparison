


package com.sun.corba.ee.impl.transport;

import com.sun.corba.ee.impl.encoding.CDROutputObject;
import com.sun.corba.ee.impl.encoding.OutputStreamFactory;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.impl.oa.poa.Policies;
import com.sun.corba.ee.spi.extension.RequestPartitioningPolicy;
import com.sun.corba.ee.spi.ior.IORTemplate;
import com.sun.corba.ee.spi.ior.TaggedProfileTemplate;
import com.sun.corba.ee.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.ior.iiop.IIOPAddress;
import com.sun.corba.ee.spi.ior.iiop.IIOPFactories;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.ee.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.misc.ORBConstants;
import com.sun.corba.ee.spi.threadpool.Work;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import com.sun.corba.ee.spi.transport.EventHandler;
import com.sun.corba.ee.spi.transport.Acceptor;
import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.transport.SocketInfo;
import com.sun.corba.ee.spi.transport.Selector;
import com.sun.corba.ee.spi.transport.InboundConnectionCache;
import com.sun.corba.ee.spi.transport.TransportManager;
import com.sun.corba.ee.spi.extension.LoadBalancingPolicy;
import com.sun.corba.ee.spi.trace.Transport;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.net.Socket ;
import org.omg.IOP.TAG_INTERNET_IOP;


@Transport
public abstract class AcceptorBase
    extends
        EventHandlerBase
    implements
        Acceptor,
        Work,
        
        SocketInfo,
        LegacyServerSocketEndPointInfo
        
{
    protected ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    protected int port ;
    protected long enqueueTime;
    protected boolean initialized = false ;

    
    protected String type = "";
    protected String name = "";
    protected String hostname ;
    protected int locatorPort;
    

    protected InboundConnectionCache connectionCache;

    public int getPort() {
        return port ;
    }

    public String getInterfaceName() {
        String result = name.equals(LegacyServerSocketEndPointInfo.NO_NAME) ? this.toString() : name;
        return result;
    }

    
    public String getName() {
        return getInterfaceName() ;
    }
    
    public String getType() {
        return type ;
    }

    public boolean isLazy() {
        return false ;
    }

    public AcceptorBase(ORB orb, int port,
                                       String name, String type)
    {
        this.orb = orb;

        setWork(this);

        
        this.hostname = orb.getORBData().getORBServerHost();
        this.name = LegacyServerSocketEndPointInfo.NO_NAME;
        this.locatorPort = -1;
        

        this.port = port;
        this.name = name;
        this.type = type;
    }

    @Transport
    public void processSocket( Socket socket ) {
        Connection connection =
            new ConnectionImpl(orb, this, socket);

        
        
        
        
        getConnectionCache().put(this, connection);

        if (connection.shouldRegisterServerReadEvent()) {
            Selector selector = orb.getTransportManager().getSelector(0);
            selector.registerForEvent(connection.getEventHandler());
        }

        getConnectionCache().reclaim();
    }

    @Transport
    public void addToIORTemplate(IORTemplate iorTemplate, Policies policies, String codebase) {
        Iterator iterator = iorTemplate.iteratorById(TAG_INTERNET_IOP.value);
        String hname = orb.getORBData().getORBServerHost();
        if (iterator.hasNext()) {
            
            if (!type.startsWith( SocketInfo.SSL_PREFIX )) {
                IIOPAddress iiopAddress = IIOPFactories.makeIIOPAddress(hname, port);
                AlternateIIOPAddressComponent iiopAddressComponent = 
                    IIOPFactories.makeAlternateIIOPAddressComponent(iiopAddress);
                while (iterator.hasNext()) {
                    TaggedProfileTemplate taggedProfileTemplate = 
                        (TaggedProfileTemplate)iterator.next();
                    taggedProfileTemplate.add(iiopAddressComponent);
                }
            }
        } else {
            IIOPProfileTemplate iiopProfile = makeIIOPProfileTemplate(policies, codebase);
            iorTemplate.add(iiopProfile);
        }
    }

    @Transport
    protected final IIOPProfileTemplate makeIIOPProfileTemplate(Policies policies, String codebase) {
        GIOPVersion version = orb.getORBData().getGIOPVersion();
        int templatePort;
        if (policies.forceZeroPort()) {
            templatePort = 0;
        } else if (policies.isTransient()) {
            templatePort = port;
        } else {
            templatePort = orb.getLegacyServerSocketManager()
                .legacyGetPersistentServerPort(SocketInfo.IIOP_CLEAR_TEXT);
        }
        IIOPAddress addr = IIOPFactories.makeIIOPAddress(hostname, 
            templatePort);
        IIOPProfileTemplate iiopProfile = IIOPFactories.makeIIOPProfileTemplate(orb, 
            version, addr);

        if (version.supportsIORIIOPProfileComponents()) {
            iiopProfile.add(IIOPFactories.makeCodeSetsComponent(orb));
            iiopProfile.add(IIOPFactories.makeMaxStreamFormatVersionComponent());
            RequestPartitioningPolicy rpPolicy = 
                (RequestPartitioningPolicy) policies.get_effective_policy(
                ORBConstants.REQUEST_PARTITIONING_POLICY);

            if (rpPolicy != null) {
                iiopProfile.add(
                    IIOPFactories.makeRequestPartitioningComponent(rpPolicy.getValue()));
            }

            LoadBalancingPolicy lbPolicy = (LoadBalancingPolicy)
                policies.get_effective_policy(
                                  ORBConstants.LOAD_BALANCING_POLICY);
            if (lbPolicy != null) {
                iiopProfile.add(
                     IIOPFactories.makeLoadBalancingComponent(
                         lbPolicy.getValue()));
            }

            if (codebase != null && !codebase.equals("")) {
                iiopProfile.add(
                    IIOPFactories.makeJavaCodebaseComponent(codebase));
            }
            if (orb.getORBData().isJavaSerializationEnabled()) {
                iiopProfile.add(
                    IIOPFactories.makeJavaSerializationComponent());
            }
        }
        return iiopProfile;
    }

    @Override
    public String toString() {
        return toStringName() + "[" + port + " " + type + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + "]";
    }

    protected String toStringName() {
        return "SocketOrChannelAcceptorImpl";
    }

    public String getHost() {
        return hostname;
    }

    public String getHostName() {
        return hostname;
    }

    public int getLocatorPort() {
        return locatorPort;
    }

    public void setLocatorPort(int port) {
        locatorPort = port;
    }

    public InboundConnectionCache getConnectionCache() {
        return connectionCache;
    }

    public String getConnectionCacheType() {
        return TransportManager.SOCKET_OR_CHANNEL_CONNECTION_CACHE;
    }

    public long getEnqueueTime() {
        return enqueueTime;
    }

    public String getMonitoringName() {
        return "AcceptedConnections";
    }

    public synchronized boolean initialized() {
        return initialized;
    }

    public void setConnectionCache(InboundConnectionCache connectionCache) {
        this.connectionCache = connectionCache;
    }

    public void setEnqueueTime(long timeInMillis) {
        enqueueTime = timeInMillis;
    }

    public EventHandler getEventHandler() {
        return this;
    }

    public Acceptor getAcceptor() {
        return this;
    }

    public Connection getConnection() {
        throw new RuntimeException("Should not happen.");
    }

    public CDROutputObject createOutputObject(ORB broker, MessageMediator messageMediator) {
        return OutputStreamFactory.newCDROutputObject(broker, messageMediator,
            messageMediator.getReplyHeader(), messageMediator.getStreamFormatVersion());
    }

    public boolean shouldRegisterAcceptEvent() {
        return true;
    }

    public int getInterestOps() {
        return SelectionKey.OP_ACCEPT;
    }

}
