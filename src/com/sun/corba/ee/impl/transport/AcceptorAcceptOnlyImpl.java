



package com.sun.corba.ee.impl.transport;




public class AcceptorAcceptOnlyImpl extends AcceptorImpl {
    private UnaryVoidFunction<Socket> operation ;

    public AcceptorAcceptOnlyImpl( ORB orb, int port,
        String name, String type, UnaryVoidFunction<Socket> operation ) {
        super( orb, port, name, type ) ;
        this.operation = operation  ;
    }

    @Override
    public void accept() {
        operation.evaluate( getAcceptedSocket() ) ;
    }

    @Override
    public void addToIORTemplate(IORTemplate iorTemplate, Policies policies, String codebase) {
        
    }
}
