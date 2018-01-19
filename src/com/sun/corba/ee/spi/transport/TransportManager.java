


package com.sun.corba.ee.spi.transport;

import java.util.Collection;

import com.sun.corba.ee.spi.ior.IORTemplate;
import com.sun.corba.ee.spi.ior.ObjectAdapterId;

import com.sun.corba.ee.impl.protocol.giopmsgheaders.Message ;


import com.sun.corba.ee.impl.oa.poa.Policies;

import com.sun.corba.ee.spi.orb.ORB;
import org.glassfish.gmbal.Description ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.ManagedObject ;
import org.glassfish.gmbal.AMXMetadata ;


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
    

