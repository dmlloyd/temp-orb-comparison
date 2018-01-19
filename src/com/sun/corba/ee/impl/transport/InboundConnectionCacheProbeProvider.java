


package com.sun.corba.ee.impl.transport;

import org.glassfish.external.probe.provider.annotations.Probe ;
import org.glassfish.external.probe.provider.annotations.ProbeProvider ;
import org.glassfish.external.probe.provider.annotations.ProbeParam ;


@ProbeProvider(moduleProviderName="glassfish", moduleName="orb" , probeProviderName="inboundconnection")
public class InboundConnectionCacheProbeProvider {
    @Probe(name="inboundConnectionOpened" )
    public void connectionOpenedEvent( 
        @ProbeParam( "acceptor" ) String acceptor, 
        @ProbeParam( "connection" ) String connection ) {}

    @Probe(name="inboundConnectionClosed" )
    public void connectionClosedEvent( 
        @ProbeParam( "connection" ) String connection ) {}
}


