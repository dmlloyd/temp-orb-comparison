


package xxxx;







@Transport
public class SocketFactoryConnectionImpl
    extends
        ConnectionImpl
{
    @Transport
    private void connectionCreated( Socket socket ) { }

    
    public SocketFactoryConnectionImpl(ORB orb,
                                       ContactInfo contactInfo,
                                       boolean useSelectThreadToWait,
                                       boolean useWorkerThread)
    {
        super(orb, useSelectThreadToWait, useWorkerThread);

        
        
        this.contactInfo = contactInfo;

        SocketInfo socketInfo =
            
            ((SocketFactoryContactInfoImpl)contactInfo).socketInfo;
        try {
            defineSocket(useSelectThreadToWait, orb.getORBData().getLegacySocketFactory().createSocket(socketInfo));
            connectionCreated( socket ) ;
        } catch (GetEndPointInfoAgainException ex) {
            throw wrapper.connectFailure(
                ex, socketInfo.getType(), socketInfo.getHost(),
                Integer.toString(socketInfo.getPort())) ;
        } catch (Exception ex) {
            throw wrapper.connectFailure(
                ex, socketInfo.getType(), socketInfo.getHost(),
                Integer.toString(socketInfo.getPort())) ;
        }
        setState(OPENING);
    }

    public String toString()
    {
        synchronized ( stateEvent ){
            return 
                "SocketFactoryConnectionImpl[" + " "
                + (socketChannel == null ?
                   socket.toString() : socketChannel.toString()) + " "
                + getStateString(getState()) + " "
                + shouldUseSelectThreadToWait() + " "
                + shouldUseWorkerThreadForEvent()
                + "]" ;
        }
    }
}


