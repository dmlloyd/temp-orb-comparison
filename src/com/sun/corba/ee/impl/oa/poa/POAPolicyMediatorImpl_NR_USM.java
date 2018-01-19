


package xxxx;






public class POAPolicyMediatorImpl_NR_USM extends POAPolicyMediatorBase {
    
    private ServantLocator locator ;

    POAPolicyMediatorImpl_NR_USM( Policies policies, POAImpl poa ) 
    {
        super( policies, poa ) ;

        
        if (policies.retainServants()) {
            throw wrapper.policyMediatorBadPolicyInFactory();
        }

        if (!policies.useServantManager()) {
            throw wrapper.policyMediatorBadPolicyInFactory();
        }

        locator = null ;
    }
    
    protected java.lang.Object internalGetServant( byte[] id, 
        String operation ) throws ForwardRequest
    { 
        if (locator == null) {
            throw wrapper.poaNoServantManager();
        }
    
        CookieHolder cookieHolder = orb.peekInvocationInfo().getCookieHolder() ;

        java.lang.Object servant = locator.preinvoke(id, poa, operation,
            cookieHolder);

        if (servant == null) {
            servant = new NullServantImpl(omgWrapper.nullServantReturned());
        } else {
            setDelegate((Servant) servant, id);
        }


        return servant;
    }

    public void returnServant() 
    {
        OAInvocationInfo info = orb.peekInvocationInfo();

        
        if (locator == null || info == null) {
            return;
        }

        locator.postinvoke(info.id(), (POA)(info.oa()),
            info.getOperation(), info.getCookieHolder().value,
            (Servant)(info.getServantContainer()) );
    }

    public void etherealizeAll() 
    {   
        
    }

    public void clearAOM() 
    {
        
    }

    public ServantManager getServantManager() throws WrongPolicy
    {
        return locator ;
    }

    public void setServantManager( ServantManager servantManager ) throws WrongPolicy
    {
        if (locator != null) {
            throw wrapper.servantManagerAlreadySet();
        }

        if (servantManager instanceof ServantLocator) {
            locator = (ServantLocator) servantManager;
        } else {
            throw wrapper.servantManagerBadType();
        }
    }

    public Servant getDefaultServant() throws NoServant, WrongPolicy 
    {
        throw new WrongPolicy();
    }

    public void setDefaultServant( Servant servant ) throws WrongPolicy
    {
        throw new WrongPolicy();
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
        throw new WrongPolicy();
    }
}
