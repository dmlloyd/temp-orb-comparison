


package com.sun.corba.ee.impl.oa.poa ;







@Poa
public class POAPolicyMediatorImpl_R_USM extends POAPolicyMediatorBase_R {
    protected ServantActivator activator ;

    POAPolicyMediatorImpl_R_USM( Policies policies, POAImpl poa ) 
    {
        
        super( policies, poa ) ;
        activator = null ;

        if (!policies.useServantManager()) {
            throw wrapper.policyMediatorBadPolicyInFactory();
        }
    }
   
    
    private AOMEntry enterEntry( ActiveObjectMap.Key key )
    {
        AOMEntry result = null ;
        boolean failed ;
        do {
            failed = false ;
            result = activeObjectMap.get(key) ;

            try {
                result.enter() ;
            } catch (Exception exc) {
                failed = true ;
            }
        } while (failed) ;

        return result ;
    }

    @InfoMethod
    private void servantAlreadyActivated() { }

    @InfoMethod
    private void upcallToIncarnate() { }

    @InfoMethod
    private void incarnateFailed() { }

    @InfoMethod
    private void incarnateComplete() { }

    @InfoMethod
    private void servantAlreadyAssignedToID() { }

    @Poa
    protected java.lang.Object internalGetServant( byte[] id, 
        String operation ) throws ForwardRequest {

        poa.lock() ;
        try {
            ActiveObjectMap.Key key = new ActiveObjectMap.Key( id ) ;
            AOMEntry entry = enterEntry(key) ;
            java.lang.Object servant = activeObjectMap.getServant( entry ) ;
            if (servant != null) {
                servantAlreadyActivated() ;
                return servant ;
            }

            if (activator == null) {
                entry.incarnateFailure() ;
                throw wrapper.poaNoServantManager() ;
            }

            
            
            
            
            try {
                upcallToIncarnate() ;

                poa.unlock() ;

                servant = activator.incarnate(id, poa);

                if (servant == null) {
                    servant = new NullServantImpl(
                        omgWrapper.nullServantReturned());
                }
            } catch (ForwardRequest freq) {
                throw freq ;
            } catch (SystemException exc) {
                throw exc ;
            } catch (Throwable exc) {
                throw wrapper.poaServantActivatorLookupFailed( exc ) ;
            } finally {
                poa.lock() ;

                
                
                
                
                if ((servant == null) || (servant instanceof NullServant)) {
                    incarnateFailed() ;

                    
                    
                    
                    
                    
                    
                    
                    
                    entry.incarnateFailure() ;
                } else {
                    
                    
                    
                    
                    if (isUnique) {
                        
                        if (activeObjectMap.contains((Servant)servant)) {
                            servantAlreadyAssignedToID() ;
                            entry.incarnateFailure() ;
                            throw wrapper.poaServantNotUnique() ;
                        }
                    }

                    incarnateComplete() ;

                    entry.incarnateComplete() ;
                    activateServant(key, entry, (Servant)servant);
                }
            }

            return servant ;
        } finally {
            poa.unlock() ;
        }
    }

    @Poa
    @Override
    public void returnServant() {
        poa.lock() ;
        try {
            OAInvocationInfo info = orb.peekInvocationInfo();
            
            if (info == null) {
                return ;
            }
            byte[] id = info.id() ;
            ActiveObjectMap.Key key = new ActiveObjectMap.Key( id ) ;
            AOMEntry entry = activeObjectMap.get( key ) ;
            entry.exit() ;
        } finally {
            poa.unlock();
        }
    }

    @Poa
    public void etherealizeAll() {      
        if (activator != null)  {
            Set<ActiveObjectMap.Key> keySet = activeObjectMap.keySet() ;

            
            
            @SuppressWarnings("unchecked")
            ActiveObjectMap.Key[] keys = 
                keySet.toArray(new ActiveObjectMap.Key[keySet.size()]) ;

            for (int ctr=0; ctr<keySet.size(); ctr++) {
                ActiveObjectMap.Key key = keys[ctr] ;
                AOMEntry entry = activeObjectMap.get( key ) ;
                Servant servant = activeObjectMap.getServant( entry ) ;
                if (servant != null) {
                    boolean remainingActivations = 
                        activeObjectMap.hasMultipleIDs(entry) ;

                    
                    
                    
                    
                    
                    entry.startEtherealize( null ) ;
                    try {
                        poa.unlock() ;
                        try {
                            activator.etherealize(key.id(), poa, servant, true,
                                remainingActivations);
                        } catch (Exception exc) {
                            
                        }
                    } finally {
                        poa.lock() ;
                        entry.etherealizeComplete() ;
                    }
                }
            }
        }
    }

    public ServantManager getServantManager() throws WrongPolicy {
        return activator;
    }

    @Poa
    public void setServantManager( 
        ServantManager servantManager ) throws WrongPolicy {

        if (activator != null) {
            throw wrapper.servantManagerAlreadySet();
        }

        if (servantManager instanceof ServantActivator) {
            activator = (ServantActivator) servantManager;
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

    @Poa
    private class Etherealizer extends Thread {
        private POAPolicyMediatorImpl_R_USM mediator ;
        private ActiveObjectMap.Key key ;
        private AOMEntry entry ;
        private Servant servant ;

        Etherealizer( POAPolicyMediatorImpl_R_USM mediator, 
            ActiveObjectMap.Key key, AOMEntry entry, Servant servant )
        {
            this.mediator = mediator ;
            this.key = key ;
            this.entry = entry;
            this.servant = servant;
        }

        @InfoMethod
        private void key( ActiveObjectMap.Key key ) { }

        @Poa
        @Override
        public void run() {
            key( key ) ;

            try {
                mediator.activator.etherealize( key.id(), mediator.poa, servant,
                    false, mediator.activeObjectMap.hasMultipleIDs( entry ) );
            } catch (Exception exc) {
                
            }

            try {
                mediator.poa.lock() ;

                entry.etherealizeComplete() ;
                mediator.activeObjectMap.remove( key ) ;

                POAManagerImpl pm = (POAManagerImpl)mediator.poa.the_POAManager() ;
                POAFactory factory = pm.getFactory() ;
                factory.unregisterPOAForServant( mediator.poa, servant);
            } finally {
                mediator.poa.unlock() ;
            }
        }
    } 

    @Poa
    @Override
    public void deactivateHelper( ActiveObjectMap.Key key, AOMEntry entry, 
        Servant servant ) throws ObjectNotActive, WrongPolicy 
    {
        if (activator == null) {
            throw wrapper.poaNoServantManager();
        }
            
        Etherealizer eth = new Etherealizer( this, key, entry, servant ) ;
        entry.startEtherealize( eth ) ;
    }

    @Poa
    public Servant idToServant( byte[] id ) 
        throws WrongPolicy, ObjectNotActive
    {
        ActiveObjectMap.Key key = new ActiveObjectMap.Key( id ) ;
        AOMEntry entry = activeObjectMap.get(key);

        Servant servant = activeObjectMap.getServant( entry ) ;
        if (servant != null) {
            return servant;
        } else {
            throw new ObjectNotActive();
        }
    }
}
