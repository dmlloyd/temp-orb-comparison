


package com.sun.corba.ee.impl.plugin.hwlb ;


















@Transport
public class NoConnectionCacheImpl
    extends LocalObject
    implements ORBConfigurator
{
    @Transport
    private static class NCCConnectionCacheImpl extends ConnectionCacheBase
        implements OutboundConnectionCache {
        
        private Map store = new HashMap() ;

        
        private Connection connection = null ;

        public NCCConnectionCacheImpl( ORB orb ) {
            super( orb, "Dummy", "Dummy" ) ;
        }

        public Collection values() {
            return store.values() ;
        }

        protected Object backingStore() {
            return store ;
        }

        public Connection get(ContactInfo contactInfo) {
            return connection ;
        }

        @Transport
        public void put(ContactInfo contactInfo, Connection conn ) {
            remove( contactInfo ) ;
            connection = conn ;
        }

        @InfoMethod
        private void removeConnectionInfo( Connection conn ) { }

        @InfoMethod
        private void connectionIsNull() { }

        @Transport
        public void remove(ContactInfo contactInfo) {
            if (connection != null) {
                removeConnectionInfo(connection);
                connection.close() ;
                connection = null ;
            } else {
                connectionIsNull();
            }
        }
    }

    private static ThreadLocal connectionCache = new ThreadLocal() ;

    private static NCCConnectionCacheImpl getConnectionCache( ORB orb ) {
        NCCConnectionCacheImpl result = (NCCConnectionCacheImpl)connectionCache.get() ;
        if (result == null) {
            result = new NCCConnectionCacheImpl( orb ) ;
            connectionCache.set( result ) ;
        }

        return result ;
    }

    @Transport
    private static class NCCConnectionImpl extends ConnectionImpl {
        private static int count = 0 ;
        private int myCount ;

        @Transport
        private void constructedNCCConnectionImpl( String str ) {
        }

        public NCCConnectionImpl(ORB orb, ContactInfo contactInfo,
                String socketType, String hostname, int port) {

            super(orb,contactInfo, socketType, hostname, port);
            myCount = count++ ;
            constructedNCCConnectionImpl(toString());
        }

        @Override
        public String toString() {
            return "NCCConnectionImpl(" + myCount + ")["
                + super.toString() + "]" ;
        }

        @Transport
        @Override
        public synchronized void close() {  
            super.closeConnectionResources() ;
        }
    }

    @Transport
    private static class NCCContactInfoImpl extends ContactInfoImpl {
        public NCCContactInfoImpl( ORB orb,
            ContactInfoList contactInfoList, IOR effectiveTargetIOR,
            short addressingDisposition, String socketType, String hostname,
            int port) {

            super( orb, contactInfoList, effectiveTargetIOR, addressingDisposition,
                    socketType, hostname, port ) ;
        }

        @Transport
        @Override
        public boolean shouldCacheConnection() {
            return false ;
        }

        @InfoMethod
        private void createdConnection( Connection conn ) { }

        @Transport
        @Override
        public Connection createConnection() {
            Connection connection = new NCCConnectionImpl( orb, this,
                socketType, hostname, port ) ;
            createdConnection(connection);
            NCCConnectionCacheImpl cc = NoConnectionCacheImpl.getConnectionCache( orb ) ;
            cc.put( this, connection ) ;
            connection.setConnectionCache( cc ) ;

            return connection ;
        }
    }

    public static class NCCContactInfoListImpl extends ContactInfoListImpl {
        public NCCContactInfoListImpl( ORB orb, IOR ior ) {
            super( orb, ior ) ;
        }

        @Override
        public ContactInfo createContactInfo( String type, String hostname,
            int port ) {

            return new NCCContactInfoImpl( orb, this, effectiveTargetIOR,
                orb.getORBData().getGIOPAddressDisposition(), type, hostname, port ) ;
        }
    }

    private static class NCCClientRequestDispatcherImpl extends ClientRequestDispatcherImpl {
        @Override
        public void endRequest( ORB broker, Object self, CDRInputObject inputObject ) {
            super.endRequest( broker, self, inputObject) ;
            getConnectionCache( broker ).remove( null ) ;
        }
    }

    public void configure( DataCollector dc, final ORB orb ) {
        ContactInfoListFactory factory = new ContactInfoListFactory() {
            public void setORB(ORB orb) {} 
            public ContactInfoList create( IOR ior ) {
                return new NCCContactInfoListImpl( orb, ior ) ;
            }
        } ;

        
        
        
        
        
        
        
        
        orb.getORBData().alwaysEnterBlockingRead( false ) ;

        orb.setCorbaContactInfoListFactory( factory ) ;

        ClientRequestDispatcher crd = new NCCClientRequestDispatcherImpl() ;
        RequestDispatcherRegistry rdr = orb.getRequestDispatcherRegistry() ;
        
        for (int ctr=0; ctr<ORBConstants.MAX_POA_SCID; ctr++) {
            ClientRequestDispatcher disp = rdr.getClientRequestDispatcher( ctr ) ;
            if (disp != null) {
                rdr.registerClientRequestDispatcher( crd, ctr ) ;
            }
        }
    }
}
