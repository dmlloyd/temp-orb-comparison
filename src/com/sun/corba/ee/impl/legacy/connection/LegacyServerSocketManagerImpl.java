


package com.sun.corba.ee.impl.legacy.connection;






public class LegacyServerSocketManagerImpl 
    implements
        LegacyServerSocketManager
{
    protected ORB orb;
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;
    
    public LegacyServerSocketManagerImpl(ORB orb) {
        this.orb = orb;
    }

    
    
    
    

    
    public int legacyGetTransientServerPort(String type)
    {
        return legacyGetServerPort(type, false);
    }

    
    public synchronized int legacyGetPersistentServerPort(String socketType)
    {
        if (orb.getORBData().getServerIsORBActivated()) {
            
            return legacyGetServerPort(socketType, true);
        } else if (orb.getORBData().getPersistentPortInitialized()) {
            
            return orb.getORBData().getPersistentServerPort();
        } else {
            throw wrapper.persistentServerportNotSet();
        }
    }

    
    public synchronized int legacyGetTransientOrPersistentServerPort(
        String socketType)
    {
            return legacyGetServerPort(socketType, 
                                       orb.getORBData()
                                       .getServerIsORBActivated());
    }

    
    
    
    public synchronized LegacyServerSocketEndPointInfo legacyGetEndpoint(
        String name)
    {
        Iterator iterator = getAcceptorIterator();
        while (iterator.hasNext()) {
            LegacyServerSocketEndPointInfo endPoint = cast(iterator.next());
            if (endPoint != null && name.equals(endPoint.getName())) {
                return endPoint;
            }
        }
        throw new INTERNAL("No acceptor for: " + name);
    }

    
    
    public boolean legacyIsLocalServerPort(int port) 
    {
        
        
        
        if (port == 0) {
            return true ;
        }

        Iterator iterator = getAcceptorIterator();
        while (iterator.hasNext()) { 
            LegacyServerSocketEndPointInfo endPoint = cast(iterator.next());
            if (endPoint != null && endPoint.getPort() == port) {
                return true;
            }
        }
        return false;
    }

    
    
    
    

    private int legacyGetServerPort (String socketType, boolean isPersistent)
    {
        Iterator endpoints = getAcceptorIterator();
        while (endpoints.hasNext()) {
            LegacyServerSocketEndPointInfo ep = cast(endpoints.next());
            if (ep != null && ep.getType().equals(socketType)) {
                if (isPersistent) {
                    return ep.getLocatorPort();
                } else {
                    return ep.getPort();
                }
            }
        }
        return -1;
    }

    private Iterator getAcceptorIterator()
    {
        Collection acceptors = 
            orb.getCorbaTransportManager().getAcceptors(null, null);
        if (acceptors != null) {
            return acceptors.iterator();
        }

        throw wrapper.getServerPortCalledBeforeEndpointsInitialized() ;
    }

    private LegacyServerSocketEndPointInfo cast(Object o)
    {
        if (o instanceof LegacyServerSocketEndPointInfo) {
            return (LegacyServerSocketEndPointInfo) o;
        }
        return null;
    }

    protected void dprint(String msg)
    {
        ORBUtility.dprint("LegacyServerSocketManagerImpl", msg);
    }
}




