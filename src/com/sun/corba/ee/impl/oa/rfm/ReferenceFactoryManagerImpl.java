


package com.sun.corba.ee.impl.oa.rfm;









@Poa
@ManagedObject
@Description( "The ReferenceFactoryManager, used to handle dynamic cluster membership updates")
public class ReferenceFactoryManagerImpl 
    extends org.omg.CORBA.LocalObject
    implements ReferenceFactoryManager
{
    private static final POASystemException wrapper =
        POASystemException.self ;

    private static final long serialVersionUID = -6689846523143143228L;

    private static final String PARENT_POA_NAME = "#RFMBase#" ;

    
    private RFMState state ;
    private final ReentrantLock lock ;
    private final Condition suspendCondition ;
    private final ORB orb ;
    
    
    
    
    private final Map<String,Pair<ServantLocator,List<Policy>>> poatable ;
    private final Map<String,ReferenceFactory> factories ;
    private final Set<POAManager> managers ;
    private final AdapterActivator activator ;
    private volatile boolean isActive ;

    
    private POA rootPOA ;
    private List<Policy> standardPolicies ;
    private POA parentPOA ;
    private String[] parentPOAAdapterName ;
        
    public ReferenceFactoryManagerImpl( ORB orb )
    {
        lock = new ReentrantLock() ;
        suspendCondition = lock.newCondition() ;
        state = RFMState.READY ;
        this.orb = orb ;
        poatable = new HashMap<String,Pair<ServantLocator,List<Policy>>>() ;
        factories = new HashMap<String,ReferenceFactory>() ;
        managers = new HashSet<POAManager>() ;
        activator = new AdapterActivatorImpl() ;
        isActive = false ;
    }

    @Poa
    private class AdapterActivatorImpl 
        extends LocalObject 
        implements AdapterActivator 
    {
        private static final long serialVersionUID = 7922226881290146012L;

        @Poa
        public boolean unknown_adapter( POA parent, String name ) {
            Pair<ServantLocator,List<Policy>> data = null ;
            synchronized (poatable) {
                
                
                data = poatable.get( name ) ;
            }

            if (data == null) {
                return false ;
            } else {
                try {
                    List<Policy> policies = new ArrayList<Policy>() ;
                    
                    
                    if (data.second() != null) {
                        policies.addAll(data.second());
                    }
                    policies.addAll( standardPolicies ) ;
                    Policy[] arr = policies.toArray( new Policy[policies.size()] ) ;

                    POA child = parentPOA.create_POA( name, null, arr ) ;
                    POAManager pm = child.the_POAManager() ;

                    lock.lock() ;
                    try {
                        managers.add(pm) ;
                    } finally {
                        lock.unlock() ;
                    }

                    child.set_servant_manager( data.first() ) ;
                    pm.activate() ;
                    return true ;
                } catch (Exception exc) {
                    wrapper.rfmAdapterActivatorFailed( exc ) ;
                    return false ;
                }
            }
        }
    } ;

    
    
    
    private static class ReferenceManagerPolicy 
        extends LocalObject 
        implements Policy 
    {
        private static Policy thisPolicy = new ReferenceManagerPolicy() ;
        private static final long serialVersionUID = -4780983694679451387L;

        public static Policy getPolicy() {
            return thisPolicy ;
        }

        private ReferenceManagerPolicy() {
        }

        public int policy_type() {
            return ORBConstants.REFERENCE_MANAGER_POLICY ;
        }

        public Policy copy() {
            return this ;
        }

        public void destroy() {
        }
    }

    public RFMState getState()
    {
        lock.lock() ;
        try {
            return state ;
        } finally {
            lock.unlock();
        }
    }

    @Poa
    public void activate() 
    {
        lock.lock() ;
        try {
            if (isActive) {
                throw wrapper.rfmAlreadyActive();
            }

            rootPOA = (POA)orb.resolve_initial_references( 
                ORBConstants.ROOT_POA_NAME ) ;

            standardPolicies = Arrays.asList( 
                ReferenceManagerPolicy.getPolicy(), 
                rootPOA.create_servant_retention_policy( 
                    ServantRetentionPolicyValue.NON_RETAIN ),
                rootPOA.create_request_processing_policy(
                    RequestProcessingPolicyValue.USE_SERVANT_MANAGER ),
                rootPOA.create_lifespan_policy( 
                    LifespanPolicyValue.PERSISTENT ) 
            ) ;

            Policy[] policies = { ReferenceManagerPolicy.getPolicy() } ;
            parentPOA = rootPOA.create_POA( PARENT_POA_NAME,
                null, policies ) ;
            parentPOAAdapterName = ObjectAdapter.class.cast( parentPOA )
                .getIORTemplate().getObjectKeyTemplate().getObjectAdapterId()
                .getAdapterName() ;

            POAManager pm = parentPOA.the_POAManager() ;
            parentPOA.the_activator( activator ) ;
            pm.activate() ;

            
            isActive = true ;
        } catch (Exception exc) {
            throw wrapper.rfmActivateFailed( exc ) ;
        } finally {
            lock.unlock() ;
        }
    }

    
    
    
    
    
    
    @Poa
    public ReferenceFactory create( final String name, 
                                    final String repositoryId,
                                    final List<Policy> policies,
                                    final ServantLocator locator ) 
    {
        lock.lock() ;
        try {
            if (state == RFMState.SUSPENDED) {
                throw wrapper.rfmMightDeadlock();
            }

            if (!isActive) {
                throw wrapper.rfmNotActive();
            }

            List<Policy> newPolicies = null ;
            if (policies != null) {
                newPolicies = new ArrayList<Policy>(policies);
            }

            
            
            synchronized (poatable) {
                poatable.put( name, new Pair<ServantLocator,List<Policy>>(
                    locator, newPolicies ) ) ;
            }

            ReferenceFactory factory = new ReferenceFactoryImpl( this, name,
                repositoryId ) ;
            factories.put( name, factory ) ;
            return factory ;
        } finally {
            lock.unlock() ;
        }
    }

    @Poa
    public ReferenceFactory find( String[] adapterName ) 
    {
        lock.lock() ;
        try {
            if (state == RFMState.SUSPENDED) {
                throw wrapper.rfmMightDeadlock();
            }

            if (!isActive) {
                return null;
            }
            
            int expectedLength = parentPOAAdapterName.length + 1 ;

            if (expectedLength != adapterName.length) {
                return null;
            }

            for (int ctr=0; ctr<expectedLength-1; ctr++) {
                if (!adapterName[ctr].equals(parentPOAAdapterName[ctr])) {
                    return null;
                }
            }

            return factories.get( adapterName[expectedLength-1] ) ;
        } finally {
            lock.unlock() ;
        }
    }

    public ReferenceFactory find( String name ) {
        lock.lock() ;
        try {
            if (state == RFMState.SUSPENDED) {
                throw wrapper.rfmMightDeadlock();
            }

            if (!isActive) {
                return null;
            }

            return factories.get( name ) ;
        } finally {
            lock.unlock() ;
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    @Poa
    public void suspend() 
    {
        lock.lock() ;

        
        final Set<POAManager> pms = new HashSet<POAManager>( managers ) ;

        
        try {
            if (!isActive) {
                throw wrapper.rfmNotActive() ;
            }

            while (state == RFMState.SUSPENDED) {
                try {
                    suspendCondition.await();
                } catch (InterruptedException exc) {
                    throw wrapper.rfmSuspendConditionWaitInterrupted();
                }
            }

            
            
            
            

            state = RFMState.SUSPENDED ;
        } finally {
            lock.unlock() ;
        }

        
        
        
        
        try {
            for (POAManager pm : pms) {
                pm.hold_requests( true ) ;
            }
        } catch (AdapterInactive ai) {
            
            throw wrapper.rfmManagerInactive( ai ) ;
        }
    }

    @Poa
    public void resume() 
    {
        lock.lock() ;

        
        final Set<POAManager> pms = new HashSet<POAManager>( managers ) ;

        try {
            if (!isActive) {
                throw wrapper.rfmNotActive();
            }

            state = RFMState.READY ;
            suspendCondition.signalAll() ;
        } finally {
            lock.unlock() ;
        }

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        try {
            for (POAManager pm : pms) {
                pm.activate() ;
            }
        } catch (AdapterInactive ai) {
            
            throw wrapper.rfmManagerInactive( ai ) ;
        }

        lock.lock() ;
        try {
            
            
            
            
            managers.removeAll( pms ) ;
        } finally {
            lock.unlock();
        }
    }

    @Poa
    public void restartFactories(
        Map<String,Pair<ServantLocator,List<Policy>>> updates )
    {
        lock.lock() ;
        try {
            if (!isActive) {
                throw wrapper.rfmNotActive();
            }

            if (state != RFMState.SUSPENDED) {
                throw wrapper.rfmMethodRequiresSuspendedState("restartFactories");
            }
        } finally {
            lock.unlock() ;
        }

        if (updates == null) {
            throw wrapper.rfmNullArgRestart();
        }

        synchronized (poatable) {
            
            poatable.putAll( updates ) ;
        }

        try {
            
            
            for (POA poa : parentPOA.the_children()) {
                poa.destroy( false, true ) ;
            }
        } catch (Exception exc) {
            throw wrapper.rfmRestartFailed( exc ) ;
        }
    }

    public void restartFactories() {
        restartFactories( new HashMap<String,Pair<ServantLocator,List<Policy>>>() ) ;
    }

    
    @Poa
    public void restart( Map<String,Pair<ServantLocator,List<Policy>>> updates ) 
    {
        suspend() ;
        try {
            restartFactories( updates ) ;
        } finally {
            resume() ;
        }
    }

    
    public void restart() {
        restart( new HashMap<String,Pair<ServantLocator,List<Policy>>>() ) ;
    }

    
    
    @Poa
    org.omg.CORBA.Object createReference( String name, byte[] key,
        String repositoryId ) 
    {
        try {
            POA child = parentPOA.find_POA( name, true ) ;
            return child.create_reference_with_id( key, repositoryId ) ;
        } catch (Exception exc) {
            throw wrapper.rfmCreateReferenceFailed( exc ) ;
        }
    }

    
    @Poa
    void destroy( String name ) {
        try {
            POA child = parentPOA.find_POA( name, true ) ;
            synchronized (poatable) {
                poatable.remove( name ) ;
            }

            lock.lock() ;
            try {
                factories.remove( name ) ;
                POAManager pm = child.the_POAManager() ;
                managers.remove( pm ) ;
            } finally {
                lock.unlock() ;
            }

            
            
            child.destroy( false, true ) ;
        } catch (Exception exc) {
            throw wrapper.rfmDestroyFailed( exc ) ;
        }
    }

    
    void validatePOACreation( POA poa ) {
        
        
        
        if (!isActive) {
            return;
        }

        
        
        
        Policy policy = ObjectAdapter.class.cast(poa).getEffectivePolicy( 
            ORBConstants.REFERENCE_MANAGER_POLICY ) ;
        if (policy != null) {
            return;
        }

        
        
        
        POA parent = poa.the_parent() ;
        Policy parentPolicy = 
            ObjectAdapter.class.cast(parent).getEffectivePolicy(
            ORBConstants.REFERENCE_MANAGER_POLICY ) ;
        if (parentPolicy != null) {
            throw wrapper.rfmIllegalParentPoaUsage();
        }

        
        
        lock.lock() ;
        try {
            if (managers.contains( poa.the_POAManager())) {
                throw wrapper.rfmIllegalPoaManagerUsage() ;
            }
        } finally {
            lock.unlock();
        }
    }

    
    @Poa
    public boolean isRfmName( String[] adapterName ) 
    {
        if (!isActive) {
            return false ;
        }

        int expectedLength = parentPOAAdapterName.length + 1 ;

        if (expectedLength != adapterName.length) {
            return false;
        }

        for (int ctr=0; ctr<expectedLength-1; ctr++) {
            if (!adapterName[ctr].equals(parentPOAAdapterName[ctr])) {
                return false;
            }
        }

        return true ;
    }
}


