


package com.sun.corba.ee.impl.transport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.external.probe.provider.annotations.Probe ;
import org.glassfish.external.probe.provider.annotations.ProbeProvider ;
import org.glassfish.external.probe.provider.annotations.ProbeParam ;


@ProbeProvider(moduleProviderName="glassfish", moduleName="orb" , probeProviderName="outboundconnection")
public class OutboundConnectionCacheProbeProvider {
    @Probe(name="outboundConnectionOpened" )
    public void connectionOpenedEvent( 
        @ProbeParam( "contactInfo" ) String contactInfo, 
        @ProbeParam( "connection" ) String connection ) {}

    @Probe(name="outboundConnectionClosed" )
    public void connectionClosedEvent(  
        @ProbeParam( "contactInfo" ) String contactInfo, 
        @ProbeParam( "connection" ) String connection ) {}
}


