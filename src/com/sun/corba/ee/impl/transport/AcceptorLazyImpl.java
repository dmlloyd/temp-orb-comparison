


package xxxx;



@Transport
public class AcceptorLazyImpl extends AcceptorBase {

    public AcceptorLazyImpl( ORB orb, int port, String name, String type ) {
        super( orb, port, name, type ) ;
    }

    @Override
    public boolean isLazy() {
        return true ;
    }

    public Socket getAcceptedSocket() {
        throw wrapper.notSupportedOnLazyAcceptor() ;
    }

    public SelectableChannel getChannel() {
        throw wrapper.notSupportedOnLazyAcceptor() ;
    }

    @Transport
    public synchronized boolean initialize() {
        if (initialized) {
            return false;
        }

        orb.getCorbaTransportManager().getInboundConnectionCache(this);

        initialized = true ;

        return true ;
    }

    public void close() {
        
    }

    public ServerSocket getServerSocket() {
        throw wrapper.notSupportedOnLazyAcceptor() ;
    }

    public void doWork() {
        throw wrapper.notSupportedOnLazyAcceptor() ;
    }

    @Override
    public boolean shouldRegisterAcceptEvent() {
        return false;
    }
}
