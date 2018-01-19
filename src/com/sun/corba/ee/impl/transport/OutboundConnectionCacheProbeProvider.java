


package xxxx;




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


