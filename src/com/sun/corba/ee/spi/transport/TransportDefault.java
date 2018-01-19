



package com.sun.corba.ee.spi.transport ;





public abstract class TransportDefault {
    private TransportDefault() {}

    public static ContactInfoListFactory makeCorbaContactInfoListFactory(
        final ORB broker ) 
    {
        return new ContactInfoListFactory() {
            public void setORB(ORB orb) { }
            public ContactInfoList create( IOR ior ) {
                return new ContactInfoListImpl(
                    (com.sun.corba.ee.spi.orb.ORB)broker, ior ) ;
            }
        };
    }

    public static ClientDelegateFactory makeClientDelegateFactory(
        final ORB broker )
    {
        return new ClientDelegateFactory() {
            public ClientDelegate create( ContactInfoList info ) {
                return new ClientDelegateImpl(
                    (com.sun.corba.ee.spi.orb.ORB)broker, info ) ;
            }
        };
    }

    public static IORTransformer makeIORTransformer(
        final ORB broker )
    {
        return null ;
    }

    public static Acceptor makeStandardCorbaAcceptor(
        ORB orb, int port, String name, String type ) {

        return new AcceptorImpl( orb, port, name, type ) ;
    }

    public static Acceptor makeLazyCorbaAcceptor(
        ORB orb, int port, String name, String type ) {

        return new AcceptorLazyImpl( orb, port, name, type ) ;
    }

    public static Acceptor makeAcceptOnlyCorbaAcceptor(
        ORB orb, int port, String name, String type,
        UnaryVoidFunction<Socket> operation ) {

        return new AcceptorAcceptOnlyImpl( orb, port, name, type,
            operation ) ;
    }
}
    

