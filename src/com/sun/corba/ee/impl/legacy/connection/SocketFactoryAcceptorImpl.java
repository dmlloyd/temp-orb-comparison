


package xxxx;





@Transport
public class SocketFactoryAcceptorImpl
    extends
        AcceptorImpl
{
    public SocketFactoryAcceptorImpl(ORB orb, int port, 
                                     String name, String type)
    {
        super(orb, port, name, type);
    }

    @Transport
    @Override
    public boolean initialize()
    {
        if (initialized) {
            return false;
        }
        try {
            serverSocket = orb.getORBData()
                .getLegacySocketFactory().createServerSocket(type, port);
            internalInitialize();
        } catch (Throwable t) {
            throw wrapper.createListenerFailed( t, "localhost", port ) ;
        }
        initialized = true;
        return true;
    }

    
    
    
    

    @Override
    protected String toStringName()
    {
        return "SocketFactoryAcceptorImpl";
    }

    
    
    
    
    
    
    
    @Override
    public void addToIORTemplate( IORTemplate iorTemplate,
        Policies policies, String codebase ) 
    {
        Iterator iterator = iorTemplate.iteratorById(
            org.omg.IOP.TAG_INTERNET_IOP.value);

        if (!iterator.hasNext()) {
            
            IIOPProfileTemplate iiopProfile = makeIIOPProfileTemplate(
                policies, codebase ) ;
            iorTemplate.add(iiopProfile);
        }
    }
}


