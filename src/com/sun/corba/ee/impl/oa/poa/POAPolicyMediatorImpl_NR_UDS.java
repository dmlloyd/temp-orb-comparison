


package com.sun.corba.ee.impl.oa.poa ;



public class POAPolicyMediatorImpl_NR_UDS extends POAPolicyMediatorBase {
    private Servant defaultServant ;

    POAPolicyMediatorImpl_NR_UDS( Policies policies, POAImpl poa ) 
    {
        super( policies, poa ) ;

        
        if (policies.retainServants()) {
            throw wrapper.policyMediatorBadPolicyInFactory();
        }

        if (!policies.useDefaultServant()) {
            throw wrapper.policyMediatorBadPolicyInFactory();
        }

        defaultServant = null ;
    }
    
    protected java.lang.Object internalGetServant( byte[] id, 
        String operation ) throws ForwardRequest {

        poa.readLock() ;
        try {
            if (defaultServant == null) {
                throw wrapper.poaNoDefaultServant();
            }

            return defaultServant;
        } finally {
            poa.readUnlock() ;
        }
    }

    public void returnServant() 
    {
        
    }

    public void etherealizeAll() 
    {   
        
    }

    public void clearAOM() 
    {
        
    }

    public ServantManager getServantManager() throws WrongPolicy
    {
        throw new WrongPolicy();
    }

    public void setServantManager( ServantManager servantManager ) throws WrongPolicy
    {
        throw new WrongPolicy();
    }

    public Servant getDefaultServant() throws NoServant, WrongPolicy 
    {
        if (defaultServant == null) {
            throw new NoServant();
        }
        return defaultServant;
    }

    public void setDefaultServant( Servant servant ) throws WrongPolicy
    {
        this.defaultServant = servant;
        setDelegate(defaultServant, "DefaultServant".getBytes());
    }

    public final void activateObject(byte[] id, Servant servant) 
        throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive
    {
        throw new WrongPolicy();
    }

    public Servant deactivateObject( byte[] id ) throws ObjectNotActive, WrongPolicy 
    {
        throw new WrongPolicy();
    }

    public byte[] servantToId( Servant servant ) throws ServantNotActive, WrongPolicy
    {   
        throw new WrongPolicy();
    }

    public Servant idToServant( byte[] id ) 
        throws WrongPolicy, ObjectNotActive
    {
        if (defaultServant != null) {
            return defaultServant;
        }

        throw new ObjectNotActive() ;
    }
}
