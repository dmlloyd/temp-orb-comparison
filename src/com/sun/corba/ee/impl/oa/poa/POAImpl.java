


package com.sun.corba.ee.impl.oa.poa;










@Poa
@ManagedObject
public class POAImpl extends ObjectAdapterBase implements POA 
{
    private static final POASystemException wrapper =
        POASystemException.self ;
    private static final OMGSystemException omgWrapper =
        OMGSystemException.self ;

    private static final long serialVersionUID = -1746388801294205323L;

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    private static final int STATE_START        = 0 ; 
    private static final int STATE_INIT         = 1 ; 
    private static final int STATE_INIT_DONE    = 2 ; 
    private static final int STATE_RUN          = 3 ; 
    private static final int STATE_DESTROYING   = 4 ; 
    private static final int STATE_DESTROYED    = 5 ; 

    private String stateToString()
    {
        switch (state) {
            case STATE_START :
                return "START" ;
            case STATE_INIT :
                return "INIT" ;
            case STATE_INIT_DONE :
                return "INIT_DONE" ;
            case STATE_RUN :
                return "RUN" ;
            case STATE_DESTROYING :
                return "DESTROYING" ;
            case STATE_DESTROYED :
                return "DESTROYED" ;
            default :
                return "UNKNOWN(" + state + ")" ;
        } 
    }

    
    private int state ;

    
    
    
    private POAPolicyMediator mediator;

    
    private final int numLevels;            
    private final ObjectAdapterId poaId ; 

    private final String poaName;           

    private POAManagerImpl manager; 
    private final int uniquePOAId ;         
                                    
                                    
    private POAImpl parent;         
    private final Map<String,POAImpl> children; 
                                          

    private AdapterActivator activator;
    private final AtomicInteger invocationCount ; 

    

    
    
    final ReadWriteLock poaMutex ;

    
    private final Condition adapterActivatorCV ;

    
    private final Condition invokeCV ;

    
    private final Condition beingDestroyedCV ;

    
    
    private final ThreadLocal<Boolean> isDestroying ;

    
    private static final Object momLock = new Object() ;

    
    
    @Override
    public String toString()
    {
        return "POA[" + poaId.toString() + 
            ", uniquePOAId=" + uniquePOAId + 
            ", state=" + stateToString() + 
            ", invocationCount=" + invocationCount.get() + "]" ;
    }

    @ManagedAttribute( id="POAState")
    @Description( "The current state of the POA")
    private String getDisplayState() {
        lock() ;
        try {
            return stateToString() ;
        } finally {
            unlock() ;
        }
    }

    @ManagedAttribute
    @Description( "The POA's mediator")
    POAPolicyMediator getMediator() {
        return mediator ;
    }

    @ManagedAttribute
    @Description( "The ObjectAdapterId for this POA")
    private ObjectAdapterId getObjectAdapterId() {
        return poaId ;
    }

    
    static POAFactory getPOAFactory( ORB orb )
    {
        return (POAFactory)orb.getRequestDispatcherRegistry().
            getObjectAdapterFactory( ORBConstants.TRANSIENT_SCID ) ;
    }

    @Poa
    private static void registerMBean( ORB orb, Object obj ) {
        orb.mom().register( getPOAFactory( orb ), obj ) ;
    }

    
    static POAImpl makeRootPOA( ORB orb )
    {
        POAManagerImpl poaManager = new POAManagerImpl( getPOAFactory( orb ), 
            orb.getPIHandler() ) ;
        registerMBean( orb, poaManager ) ;

        POAImpl result = new POAImpl( ORBConstants.ROOT_POA_NAME, 
            null, orb, STATE_START ) ;
        result.initialize( poaManager, Policies.rootPOAPolicies ) ;
        
        registerMBean( orb, result ) ;

        return result ;
    }

    
    @ManagedAttribute()
    @Description( "The unique ID for this POA")
    int getPOAId()
    {
        return uniquePOAId ;
    }

    @InfoMethod
    private void thisPoa( POAImpl p ) { }

    @InfoMethod
    private void acquireLockWaiting( int count )  {}

    @Poa
    
    
    
    private void acquireLock( Lock lock ) {
        lock.lock();
    }

    
    @Poa
    void lock()
    {
        acquireLock( poaMutex.writeLock() ) ;
        thisPoa( this ) ;
    }

    
    @Poa
    void unlock()
    {
        thisPoa( this ) ;
        poaMutex.writeLock().unlock() ;
    }

    @Poa
    void readLock()
    {
        acquireLock( poaMutex.readLock() ) ;
        thisPoa( this ) ;
    }

    
    @Poa
    void readUnlock()
    {
        thisPoa( this ) ;
        poaMutex.readLock().unlock() ;
    }

    @Poa
    final Condition makeCondition() {
        return poaMutex.writeLock().newCondition() ;
    }

    
    Policies getPolicies() 
    {
        return mediator.getPolicies() ;
    }

    @Poa
    private void newPOACreated( String name, String parentName ) { }

    
    
    private POAImpl( String name, POAImpl parent, ORB orb, int initialState ) {
        super( orb ) ;

        if (parent == null) {
            newPOACreated( name, "null parent for root POA" ) ;
        } else {
            newPOACreated( name, parent.poaName ) ;
        }

        this.state     = initialState ;
        this.poaName   = name ;
        this.parent    = parent;
        children = new HashMap<String,POAImpl>();
        activator = null ;

        
        
        uniquePOAId = getPOAFactory( orb ).newPOAId() ;

        if (parent == null) {
            
            numLevels = 1 ;
        } else {
            
            numLevels = parent.numLevels + 1 ;

            parent.children.put(name, this);
        }

        
        
        String[] names = new String[ numLevels ] ;
        POAImpl poaImpl = this ;
        int ctr = numLevels - 1 ;
        while (poaImpl != null) {
            names[ctr] = poaImpl.poaName ;
            ctr-- ;
            poaImpl = poaImpl.parent ;
        }

        poaId = new ObjectAdapterIdArray( names ) ;

        invocationCount = new AtomicInteger(0) ;

        poaMutex = new ReentrantReadWriteLock() ;
        adapterActivatorCV = makeCondition() ;
        invokeCV           = makeCondition() ;
        beingDestroyedCV   = makeCondition() ;

        isDestroying = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };
    }

    @NameValue
    private String getName() {
        StringBuilder sb = new StringBuilder() ;
        boolean first = true ;
        for (String str : poaId.getAdapterName() ) {
            if (first) {
                first = false ;
            } else {
                sb.append( '.' ) ;
            }
            sb.append( str ) ;
        }
        return sb.toString() ;
    }

    @InfoMethod
    private void initializingPoa( int scid, int serverid, String orbid,
        ObjectAdapterId poaId ) { }

    
    @Poa
    private void initialize( POAManagerImpl manager, Policies policies ) 
    {
        this.manager = manager;
        manager.addPOA(this);

        mediator = POAPolicyMediatorFactory.create( policies, this ) ;

        
        int serverid = mediator.getServerId() ;
        int scid = mediator.getScid() ;
        String orbId = getORB().getORBData().getORBId();

        ObjectKeyTemplate oktemp = new POAObjectKeyTemplate( getORB(), 
            scid, serverid, orbId, poaId ) ;


        initializingPoa( scid, serverid, orbId, poaId ) ;

        
        
        
        boolean objectAdapterCreated = true; 
    
        
        
        initializeTemplate( oktemp, objectAdapterCreated,
                            policies, 
                            null, 
                            null, 
                            oktemp.getObjectAdapterId()
                            ) ;

        if (state == STATE_START) {
            state = STATE_RUN;
        } else if (state == STATE_INIT) {
            state = STATE_INIT_DONE;
        } else {
            throw wrapper.illegalPoaStateTrans();
        }
    }

    @InfoMethod
    private void interruptedAwait( InterruptedException exc ) {}

    
    
    @Poa
    private boolean waitUntilRunning() 
    {
        while (state < STATE_RUN) {
            try {
                adapterActivatorCV.await( 1, TimeUnit.SECONDS ) ;
            } catch (InterruptedException exc) {
                interruptedAwait( exc ) ;
            }
        } 

        
        
        return (state == STATE_RUN) ;
    }

    
    
    
    
    
    
    
    
    
    
    
    private boolean destroyIfNotInitDone()
    {
        lock() ;

        try {
            boolean success = (state == STATE_INIT_DONE) ;

            if (success) {
                state = STATE_RUN ;
                unlock() ;
            } else {
                
                
                
                unlock() ;

                
                
                
                DestroyThread destroyer = new DestroyThread( false );
                destroyer.doIt( this, true ) ;
            }

            return success ;
        } finally {
            lock() ;

            try {
                adapterActivatorCV.signalAll() ;
            } finally {
                unlock() ;
            }
        }
    }

    private byte[] internalReferenceToId( 
        org.omg.CORBA.Object reference ) throws WrongAdapter 
    {
        IOR ior = getORB().getIOR( reference, false ) ;
        IORTemplateList thisTemplate = ior.getIORTemplates() ;

        ObjectReferenceFactory orf = getCurrentFactory() ;
        IORTemplateList poaTemplate = 
            IORFactories.getIORTemplateList( orf ) ;

        if (!poaTemplate.isEquivalent( thisTemplate )) {
            throw new WrongAdapter();
        }
            
        
        
        
        
        Iterator<TaggedProfile> iter = ior.iterator() ;
        if (!iter.hasNext()) {
            throw wrapper.noProfilesInIor();
        }
        TaggedProfile prof = (iter.next()) ;
        ObjectId oid = prof.getObjectId() ;

        return oid.getId();
    }

    
    
    @Poa
    private static class DestroyThread extends Thread {
        private boolean wait ;
        private boolean etherealize ;
        private POAImpl thePoa ;

        DestroyThread( boolean etherealize ) {
            this.etherealize = etherealize ;
        }

        @Poa
        public void doIt( POAImpl thePoa, boolean wait ) {
            this.thePoa = thePoa ;
            this.wait = wait ;
    
            if (wait) {
                run() ;
            } else {
                
                
                
                try { 
                    setDaemon(true); 
                } catch (Exception e) {
                    thePoa.wrapper.couldNotSetDaemon( e ) ;
                }

                start() ;
            }
        }

        @Poa
        @Override
        public void run() 
        {
            final Set<ObjectReferenceTemplate> destroyedPOATemplates =
                new HashSet<ObjectReferenceTemplate>() ;

            performDestroy( thePoa, destroyedPOATemplates );

            Iterator<ObjectReferenceTemplate> iter = destroyedPOATemplates.iterator() ;
            ObjectReferenceTemplate[] orts = new ObjectReferenceTemplate[ 
                destroyedPOATemplates.size() ] ;
            int index = 0 ;
            while (iter.hasNext()) {
                orts[index] = iter.next();
                index++ ;
            }

            ORB myORB = thePoa.getORB() ;

            if (destroyedPOATemplates.size() > 0) {
                myORB.getPIHandler().adapterStateChanged( orts,
                    NON_EXISTENT.value ) ;
            }
        }
    
        
        
        
        @Poa
        private boolean prepareForDestruction( POAImpl poa, 
            Set<ObjectReferenceTemplate> destroyedPOATemplates )
        {
            POAImpl[] childPoas = null ;

            
            
            poa.lock() ;

            try {
                if (poa.state <= STATE_RUN) {
                    poa.state = STATE_DESTROYING ;
                } else {
                    
                    
                    
                    
                    
                    
                    
                    
                    if (wait) {
                        while (poa.state != STATE_DESTROYED) {
                            try {
                                poa.beingDestroyedCV.await( 1, TimeUnit.SECONDS );
                            } catch (InterruptedException exc) {
                                interruptedAwait( exc ) ;
                            }
                        }
                    }

                    return false ;
                }

                poa.isDestroying.set(Boolean.TRUE);

                
                
                childPoas = poa.children.values().toArray( new POAImpl[0] );
            } finally {
                poa.unlock() ;
            }

            
            
            

            for (int ctr=0; ctr<childPoas.length; ctr++ ) {
                performDestroy( childPoas[ctr], destroyedPOATemplates ) ;
            }

            return true ;
        }

        @Poa
        public void performDestroy( POAImpl poa, 
            Set<ObjectReferenceTemplate> destroyedPOATemplates )
        {
            if (!prepareForDestruction( poa, destroyedPOATemplates )) {
                return;
            }

            
            
            
            
            

            POAImpl parent = poa.parent ;
            boolean isRoot = parent == null ;

            
            
            
            if (!isRoot) {
                parent.lock();
            }

            try {
                poa.lock() ;
                try {
                    completeDestruction( poa, parent, 
                        destroyedPOATemplates ) ;
                } finally {
                    poa.unlock() ;

                    if (isRoot) {
                        poa.manager.getFactory().registerRootPOA();
                    }
                }
            } finally {
                if (!isRoot) {
                    parent.unlock() ;
                    poa.parent = null ;          
                }
            }
        }

        @InfoMethod
        private void unregisteringMBean( ObjectName oname, POAImpl poa ) { }

        @InfoMethod
        private void noMBean( POAImpl poa ) { }

        @InfoMethod
        private void interruptedAwait( InterruptedException exc ) {}

        @Poa
        private void completeDestruction( POAImpl poa, POAImpl parent, 
            Set<ObjectReferenceTemplate> destroyedPOATemplates )
        {
            try {
                while (poa.invocationCount.get() != 0) {
                    try {
                        poa.invokeCV.await( 1, TimeUnit.SECONDS ) ;
                    } catch (InterruptedException ex) {
                        interruptedAwait( ex ) ;
                    } 
                }

                if (poa.mediator != null) {
                    if (etherealize) {
                        poa.mediator.etherealizeAll();
                    }
                        
                    poa.mediator.clearAOM() ;
                }

                if (poa.manager != null) {
                    poa.manager.removePOA(poa);
                }

                if (parent != null) {
                    parent.children.remove(poa.poaName);
                }

                destroyedPOATemplates.add( poa.getAdapterTemplate() ) ;

                synchronized (momLock) {
                    
                    
                    
                    ObjectName oname = poa.getORB().mom().getObjectName( poa ) ; 
                    if (oname != null) {
                        unregisteringMBean( oname, poa ) ;
                        poa.getORB().mom().unregister( poa );
                    } else {
                        noMBean( poa ) ;
                    }
                }
            } catch (Throwable thr) {
                if (thr instanceof ThreadDeath) {
                    throw (ThreadDeath) thr;
                }

                wrapper.unexpectedException( thr, poa.toString() ) ;
            } finally {
                poa.state = STATE_DESTROYED ;
                poa.beingDestroyedCV.signalAll();
                poa.isDestroying.set(Boolean.FALSE);
            }
        }
    }

    @Poa
    void etherealizeAll()
    {
        lock() ;

        try {
            mediator.etherealizeAll() ;
        } finally {
            unlock() ;
        }
    }

 
 
 
    @InfoMethod
    private void newPOA( POAImpl poa ) { }

    
    @Poa
    public POA create_POA(String name, POAManager 
        theManager, Policy[] policies) throws AdapterAlreadyExists, 
        InvalidPolicy 
    {
        lock() ;

        try {
            
            
            if (state > STATE_RUN) {
                throw omgWrapper.createPoaDestroy();
            }
                
            POAImpl poa = children.get(name) ;

            if (poa == null) {
                poa = new POAImpl( name, this, getORB(), STATE_START ) ;
            }

            try {
                poa.lock() ;
                newPOA( poa ) ;

                if ((poa.state != STATE_START) && (poa.state != STATE_INIT)) {
                    throw new AdapterAlreadyExists();
                }

                POAManagerImpl newManager = (POAManagerImpl)theManager ;
                if (newManager == null) {
                    newManager = new POAManagerImpl( manager.getFactory(),
                        getORB().getPIHandler() );
                    registerMBean( getORB(), newManager ) ;
                }

                int defaultCopierId = 
                    getORB().getCopierManager().getDefaultId() ;
                Policies POAPolicies = 
                    new Policies( policies, defaultCopierId ) ;

                poa.initialize( newManager, POAPolicies ) ;

                
                registerMBean( getORB(), poa ) ;

                return poa;
            } finally {
                poa.unlock() ;
            }
        } finally {
            unlock() ;
        }
    }

    @InfoMethod
    private void foundPOA( POAImpl poa ) { }

    @InfoMethod
    private void createdPOA( POAImpl poa ) { }

    @InfoMethod
    private void noPOA() { }

    @InfoMethod
    private void callingAdapterActivator() { }

    @InfoMethod
    private void adapterActivatorResult( boolean result ) { }

    
    @Poa
    public POA find_POA(String name, boolean activate) throws AdapterNonExistent {
        AdapterActivator act = null ;
        boolean readLocked = false ;
        boolean writeLocked = false ;
        boolean childReadLocked = false ;
        POAImpl child = null ;

        try {
            
            readLock() ; readLocked = true ;

            child = children.get(name);
            if (child != null) {
                child.readLock() ; childReadLocked = true ;
                foundPOA( child ) ;
                try {
                    
                    readUnlock() ; readLocked = false ;

                    if (child.state != STATE_RUN) {
                        child.readUnlock() ; childReadLocked = false ;
                        child.lockAndWaitUntilRunning() ;
                    }
                    
                    
                } finally {
                    if (childReadLocked) { child.readUnlock() ; childReadLocked = false ; }
                }
            } else {
                try {
                    noPOA() ;

                    if (activate && (activator != null)) {
                        readUnlock() ; readLocked = false ; 

                        
                        

                        lock() ; writeLocked = true ;
                        try {
                            child = children.get(name);
                            if (child == null) {
                                child = new POAImpl( name, this, getORB(), STATE_INIT ) ;
                                createdPOA( child ) ;
                                act = activator ; 
                            } else { 
                                unlock() ; writeLocked = false ;  
                                child.lockAndWaitUntilRunning() ;
                            }
                        } finally {
                            if (writeLocked) { unlock() ; writeLocked = false ; }
                        }
                    } else {
                        throw new AdapterNonExistent();
                    }
                } finally {
                    if (readLocked) { readUnlock() ; } 
                }
            }

            
            if (act != null) {
                doActivate( act, name, child ) ;
            }

            return child;
        } finally {
            cleanUpLocks( child, readLocked, writeLocked, childReadLocked ) ;
        }
    }

    @Poa
    private void lockAndWaitUntilRunning() {
        
        lock() ;
        try {
            
            if (!waitUntilRunning()) {
                
                throw omgWrapper.poaDestroyed();
            }
        } finally {
            unlock() ;
        }
    }

    @Poa
    private void doActivate( AdapterActivator act, 
        String name, POAImpl child ) throws AdapterNonExistent {

        boolean status = false ;
        boolean adapterResult = false ;
        callingAdapterActivator() ;

        try {
            
            
            synchronized (act) {
                status = act.unknown_adapter(this, name);
            }
        } catch (SystemException exc) {
            throw omgWrapper.adapterActivatorException( exc,
                poaName, poaId ) ;
        } catch (Throwable thr) {
            
            
            wrapper.unexpectedException( thr, this.toString() ) ;

            if (thr instanceof ThreadDeath) {
                throw (ThreadDeath) thr;
            }
        } finally {
            
            
            
            
            
            adapterResult = child.destroyIfNotInitDone() ;
        }

        adapterActivatorResult(status);

        if (status) {
            if (!adapterResult) {
                throw omgWrapper.adapterActivatorException(name, poaId);
            }
        } else {
            
            
            throw new AdapterNonExistent();
        }
    }

    @InfoMethod
    private void locksWereHeld() {}

    @Poa
    private void cleanUpLocks( POAImpl child, boolean readLocked, boolean writeLocked,
        boolean childReadLocked ) {
        
        if (readLocked || writeLocked || childReadLocked) {
            locksWereHeld();
            wrapper.findPOALocksNotReleased( readLocked, writeLocked,
                childReadLocked ) ;

            if (readLocked) {
                readUnlock() ;
            }

            if (writeLocked) {
                unlock() ;
            }

            if (childReadLocked && child != null) {
                child.readUnlock() ;
            }
        }
    }

    
    public void destroy(boolean etherealize, boolean wait_for_completion) 
    {
        
        if (wait_for_completion && getORB().isDuringDispatch()) {
            throw wrapper.destroyDeadlock() ;
        }

        DestroyThread destroyer = new DestroyThread( etherealize );
        destroyer.doIt( this, wait_for_completion ) ;
    }

    
    public ThreadPolicy create_thread_policy(
        ThreadPolicyValue value) 
    {
        return new ThreadPolicyImpl(value);
    }

    
    public LifespanPolicy create_lifespan_policy(
        LifespanPolicyValue value) 
    {
        return new LifespanPolicyImpl(value);
    }

    
    public IdUniquenessPolicy create_id_uniqueness_policy(
        IdUniquenessPolicyValue value) 
    {
        return new IdUniquenessPolicyImpl(value);
    }

    
    public IdAssignmentPolicy create_id_assignment_policy(
        IdAssignmentPolicyValue value) 
    {
        return new IdAssignmentPolicyImpl(value);
    }

    
    public ImplicitActivationPolicy create_implicit_activation_policy(
        ImplicitActivationPolicyValue value) 
    {
        return new ImplicitActivationPolicyImpl(value);
    }

    
    public ServantRetentionPolicy create_servant_retention_policy(
        ServantRetentionPolicyValue value) 
    {
        return new ServantRetentionPolicyImpl(value);
    }
    
    
    public RequestProcessingPolicy create_request_processing_policy(
        RequestProcessingPolicyValue value) 
    {
        return new RequestProcessingPolicyImpl(value);
    }
    
    
    @ManagedAttribute( id="POAName")
    @Description( "The name of this POA")
    public String the_name() 
    {
        try {
            lock() ;

            return poaName;
        } finally {
            unlock() ;
        }
    }

    
    @ManagedAttribute( id="POAParent")
    @Description( "The parent of this POA")
    public POA the_parent() 
    {
        try {
            lock() ;

            return parent;
        } finally {
            unlock() ;
        }
    }

    
    @ManagedAttribute( id="POAChildren")
    @Description( "The children of this POA")
    private List<POAImpl> children() {
        try {
            lock() ;
            return new ArrayList<POAImpl>( children.values() ) ;
        } finally {
            unlock() ;
        }
    }

    public org.omg.PortableServer.POA[] the_children() 
    {
        try {
            lock() ;

            Collection<POAImpl> coll = children.values() ;
            int size = coll.size() ;
            POA[] result = new POA[ size ] ;
            int index = 0 ;
            Iterator<POAImpl> iter = coll.iterator() ;
            while (iter.hasNext()) {
                POA poa = iter.next() ;
                result[ index ] = poa ;
                index++ ;
            }

            return result ;
        } finally {
            unlock() ;
        }
    }

    
    
    @ManagedAttribute( id="POAManager")
    @Description( "The POAManager of this POA")
    private POAManagerImpl getPOAManager() {
        try {
            lock() ;

            return manager;
        } finally {
            unlock() ;
        }
    }

    
    public POAManager the_POAManager() 
    {
        try {
            lock() ;

            return manager;
        } finally {
            unlock() ;
        }
    }

    
    @ManagedAttribute( id="Activator")
    @Description( "The AdapterActivator of this POA")
    public AdapterActivator the_activator() 
    {
        try {
            lock() ;

            return activator;
        } finally {
            unlock() ;
        }
    }
    
    
    @Poa
    public void the_activator(AdapterActivator activator) 
    {
        try {
            lock() ;

            this.activator = activator;
        } finally {
            unlock() ;
        }
    }

    
    public ServantManager get_servant_manager() throws WrongPolicy 
    {
        try {
            lock() ;

            return mediator.getServantManager() ;
        } finally {
            unlock() ;
        }
    }

    @ManagedAttribute
    @Description( "The servant manager of this POA (may be null)")
    private ServantManager servantManager() {
        try {
            return get_servant_manager();
        } catch (WrongPolicy ex) {
            return null ;
        }
    }

    
    @Poa
    public void set_servant_manager(ServantManager servantManager)
        throws WrongPolicy 
    {
        try {
            lock() ;

            mediator.setServantManager( servantManager ) ;
        } finally {
            unlock() ;
        }
    }
        
    
    public Servant get_servant() throws NoServant, WrongPolicy 
    {
        try {
            lock() ;

            return mediator.getDefaultServant() ;
        } finally {
            unlock() ;
        }
    }

    @ManagedAttribute
    @Description( "The default servant of this POA (may be null)")
    private Servant servant() {
        try {
            return get_servant();
        } catch (NoServant ex) {
            return null ;
        } catch (WrongPolicy ex) {
            return null ;
        }
    }

    
    @Poa
    public void set_servant(Servant defaultServant)
        throws WrongPolicy 
    {
        try {
            lock() ;

            mediator.setDefaultServant( defaultServant ) ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public byte[] activate_object(Servant servant)
        throws ServantAlreadyActive, WrongPolicy 
    {
        try {
            lock() ;

            
            
            
            byte[] id = mediator.newSystemId();

            try {
                mediator.activateObject( id, servant ) ;
            } catch (ObjectAlreadyActive oaa) {
                
                
                
            }

            return id ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public void activate_object_with_id(byte[] id,
                                                     Servant servant)
        throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy
    {
        try {
            lock() ;

            
            
            byte[] idClone = id.clone() ;

            mediator.activateObject( idClone, servant ) ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public void deactivate_object(byte[] id)
        throws ObjectNotActive, WrongPolicy 
    {
        try {
            lock() ;

            mediator.deactivateObject( id ) ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public org.omg.CORBA.Object create_reference(String repId)
        throws WrongPolicy 
    {
        try {
            lock() ;

            return makeObject( repId, mediator.newSystemId()) ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public org.omg.CORBA.Object
        create_reference_with_id(byte[] oid, String repId) 
    {
        try {
            lock() ;

            
            
            byte[] idClone = (oid.clone()) ;

            return makeObject( repId, idClone ) ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public byte[] servant_to_id(Servant servant)
        throws ServantNotActive, WrongPolicy 
    {
        try {
            lock() ;

            return mediator.servantToId( servant ) ;
        } finally {
            unlock() ;
        }
    }
                
    
    @Poa
    public org.omg.CORBA.Object servant_to_reference(Servant servant)
        throws ServantNotActive, WrongPolicy 
    {
        try {
            lock() ;

            byte[] oid = mediator.servantToId(servant);
            String repId = servant._all_interfaces( this, oid )[0] ;
            return create_reference_with_id(oid, repId);
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public Servant reference_to_servant(org.omg.CORBA.Object reference)
        throws ObjectNotActive, WrongPolicy, WrongAdapter 
    {
        try {
            lock() ;

            if ( state >= STATE_DESTROYING ) {
                throw wrapper.adapterDestroyed() ;
            }

            
            
            byte [] id = internalReferenceToId(reference);
            
            return mediator.idToServant( id ) ; 
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public byte[] reference_to_id(org.omg.CORBA.Object reference)
        throws WrongAdapter, WrongPolicy 
    {
        try {
            lock() ;
            
            if( state >= STATE_DESTROYING ) {
                throw wrapper.adapterDestroyed() ;
            }
            
            return internalReferenceToId( reference ) ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public Servant id_to_servant(byte[] id)
        throws ObjectNotActive, WrongPolicy 
    {
        try {
            lock() ;
            
            if( state >= STATE_DESTROYING ) {
                throw wrapper.adapterDestroyed() ;
            }
            return mediator.idToServant( id ) ;
        } finally {
            unlock() ;
        }
    }

    
    @Poa
    public org.omg.CORBA.Object id_to_reference(byte[] id)
        throws ObjectNotActive, WrongPolicy 

    {
        try {
            lock() ;
            
            if( state >= STATE_DESTROYING ) {
                throw wrapper.adapterDestroyed() ;
            }
            
            Servant s = mediator.idToServant( id ) ;
            String repId = s._all_interfaces( this, id )[0] ;
            return makeObject(repId, id );
        } finally {
            unlock() ;
        }
    }

    
    public byte[] id() 
    {
        try {
            lock() ;

            return getAdapterId() ;
        } finally {
            unlock() ;
        }
    }

    
    
    

    public Policy getEffectivePolicy( int type ) 
    {
        return mediator.getPolicies().get_effective_policy( type ) ;
    }

    public int getManagerId()
    {
        return manager.getManagerId() ;
    }

    public short getState()
    {
        return manager.getORTState() ;
    }

    public String[] getInterfaces( java.lang.Object servant, byte[] objectId )
    {
        Servant serv = (Servant)servant ;
        return serv._all_interfaces( this, objectId ) ;
    }

    protected ObjectCopierFactory getObjectCopierFactory()
    {
        int copierId = mediator.getPolicies().getCopierId() ;
        CopierManager cm = getORB().getCopierManager() ;
        return cm.getObjectCopierFactory( copierId ) ;
    }

    @Poa
    public void enter() throws OADestroyed
    {
        manager.enter();

        readLock() ;
        try {
            
            if (state == STATE_RUN) {
                
                invocationCount.incrementAndGet();
                return ;
            }
        } finally {
            readUnlock();
        }

        
        lock() ;

        try {
            
            
            
            
            
            while ((state == STATE_DESTROYING) &&
                (isDestroying.get() == Boolean.FALSE)) {
                try {
                    beingDestroyedCV.await( 1, TimeUnit.SECONDS );
                } catch (InterruptedException ex) {
                    interruptedAwait( ex ) ;
                }
            }

            if (!waitUntilRunning()) {
                manager.exit() ;
                throw new OADestroyed() ;
            }

            invocationCount.incrementAndGet();
        } finally {
            unlock() ;
        }
    }

    @Poa
    public void exit() 
    {
        try {
            readLock() ;
            try {
                
                if (state == STATE_RUN) {
                    
                    invocationCount.decrementAndGet();
                    return ;
                }
            } finally {
                readUnlock();
            }

            lock() ;
            try {
                if ((invocationCount.decrementAndGet() == 0)
                    && (state == STATE_DESTROYING)) {
                    invokeCV.signalAll();
                }
            } finally {
                unlock() ;
            }
        } finally {
            manager.exit();
        }

    }

    @ManagedAttribute
    @Description( "The current invocation count of this POA")
    @Poa
    private int getInvocationCount() {
        try {
            lock() ;
            return invocationCount.get() ;
        } finally {
            unlock() ;
        }
    }

    @Poa
    public void getInvocationServant( OAInvocationInfo info ) {
        
        if (info == null) {
            return ;
        }

        java.lang.Object servant = null ;

        try {
            servant = mediator.getInvocationServant( info.id(),
                info.getOperation() );
        } catch (ForwardRequest freq) {
            throw new ForwardException( getORB(), freq.forward_reference ) ;
        }

        info.setServant( servant ) ;
    }

    public org.omg.CORBA.Object getLocalServant( byte[] objectId ) 
    {
        return null ;
    }

    
    @Poa
    public void returnServant() {
        try {
            mediator.returnServant();
        } catch (Throwable thr) {
            if (thr instanceof Error) {
                throw (Error) thr;
            } else if (thr instanceof RuntimeException) {
                throw (RuntimeException)thr ;
            }
        } 
    }
}
