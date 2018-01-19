


package xxxx;












@Poa
@ManagedObject
@Description( "A POAManager which controls invocations of its POAs")
public class POAManagerImpl extends org.omg.CORBA.LocalObject implements 
    POAManager
{
    private static final POASystemException wrapper =
        POASystemException.self ;

    private static final long serialVersionUID = -3308938242684343402L;

    
    private final POAFactory factory ;  
                                        
    private final PIHandler pihandler ; 
    private final int myId ;            

    
    
    
    
    
    private final ReentrantReadWriteLock stateLock =
        new ReentrantReadWriteLock( true ) ;

    
    
    private final Condition stateCV = stateLock.writeLock().newCondition() ;

    
    private State state;                

    private Set<POAImpl> poas =
        new HashSet<POAImpl>(4) ;       

    
    private AtomicInteger nInvocations=
        new AtomicInteger(0);           
    private AtomicInteger nWaiters =
        new AtomicInteger(0) ;          
                                        
    private volatile boolean explicitStateChange ; 
                                        
                                        

    
    private static ThreadLocal<MultiSet<POAManagerImpl>> activeManagers =
        new ThreadLocal<MultiSet<POAManagerImpl>>() {
        @Override
            public MultiSet<POAManagerImpl> initialValue() {
                return new MultiSet<POAManagerImpl>() ;
            }
        } ;

    private String stateToString( State state ) {
        switch (state.value()) {
            case State._HOLDING : return "HOLDING" ;
            case State._ACTIVE : return "ACTIVE" ;
            case State._DISCARDING : return "DISCARDING" ;
            case State._INACTIVE : return "INACTIVE" ;
        }

        return "State[UNKNOWN]" ;
    }

    @Override
    public int hashCode()
    {
        return myId ;
    }

    @Override
    public boolean equals( Object obj )
    {
        if (obj == this) {
            return true ;
        }

        if (!(obj instanceof POAManagerImpl)) {
            return false ;
        }

        POAManagerImpl other = (POAManagerImpl)obj ;

        return other.myId == myId ;
    }

    @Override
    public String toString() {
        stateLock.readLock().lock();
        try {
            return "POAManagerImpl[" + myId +
                "," + stateToString(state) +
                ",nInvocations=" + nInvocations +
                ",nWaiters=" + nWaiters + "]" ;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @ManagedAttribute
    @Description( "The set of POAs managed by this POAManager" )
    Set<POAImpl> getManagedPOAs() {
        return new HashSet<POAImpl>( poas ) ;
    }

    @ManagedAttribute
    @Description( "Number of active invocations executing in this POAManager" )
    public int numberOfInvocations() {
        return nInvocations.get() ;
    }

    @ManagedAttribute
    @Description( "Number of threads waiting for invocations to complete in this POAManager" )
    public int numberOfWaiters() {
        return nWaiters.get() ;
    }

    @ManagedAttribute
    @Description( "The current state of this POAManager" ) 
    public String displayState() {
        stateLock.readLock().lock();
        try {
            return stateToString( state ) ;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @ManagedAttribute
    @Description( "The POAFactory that manages this POAManager" )
    POAFactory getFactory()
    {
        return factory ;
    }

    PIHandler getPIHandler()
    {
        return pihandler ;
    }

    @InfoMethod
    private void numWaitersStart( int value ) {}

    @InfoMethod
    private void numWaitersEnd( int value ) {}

    @Poa
    
    private void countedWait()
    {
        try {
            int num = nWaiters.incrementAndGet() ;
            numWaitersStart( num ) ;

            
            stateCV.await(num*1000L, TimeUnit.MILLISECONDS);
        } catch ( java.lang.InterruptedException ex ) {
            
        } finally {
            int num = nWaiters.decrementAndGet() ;
            numWaitersEnd( num ) ;
        }
    }

    @InfoMethod
    private void nWaiters( int value ) { }

    @Poa
    
    private void notifyWaiters() 
    {
        int num = nWaiters.get() ;
        nWaiters( num ) ;

        if (num >0) {
            stateCV.signalAll() ;
        }
    }

    @ManagedAttribute
    @NameValue
    @Description( "The ID of this POAManager" )
    public int getManagerId() 
    {
        return myId ;
    }

    POAManagerImpl( POAFactory factory, PIHandler pih )
    {
        this.factory = factory ;
        factory.addPoaManager(this);
        pihandler = pih ;
        myId = factory.newPOAManagerId() ;
        state = State.HOLDING;
        explicitStateChange = false ;
    }

    void addPOA(POAImpl poa)
    {
        stateLock.writeLock().lock();
        try {
            if (state.value() == State._INACTIVE) {
                throw wrapper.addPoaInactive() ;
            }

            poas.add( poa);
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    void removePOA(POAImpl poa)
    {
        stateLock.writeLock().lock();
        try {
            poas.remove( poa);
            if ( poas.isEmpty() ) {
                factory.removePoaManager(this);
            }
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    @ManagedAttribute
    @Description( "The ObjectReferenceTemplate state of this POAManager" )
    public short getORTState() 
    {
        switch (state.value()) {
            case State._HOLDING    : return HOLDING.value ;
            case State._ACTIVE     : return ACTIVE.value ;
            case State._INACTIVE   : return INACTIVE.value ;
            case State._DISCARDING : return DISCARDING.value ;
            default                : return NON_EXISTENT.value ;
        }
    }



    
    @Poa
    @ManagedOperation
    @Description( "Make this POAManager active, so it can handle new requests" ) 
    public void activate()
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
        explicitStateChange = true ;

        stateLock.writeLock().lock() ;

        try {
            if ( state.value() == State._INACTIVE ) {
                throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();
            }

            
            state = State.ACTIVE;

            pihandler.adapterManagerStateChanged( myId, getORTState() ) ;

            
            
            
            notifyWaiters();
        } finally {
            stateLock.writeLock().unlock() ;
        }
    }

    
    @Poa
    @ManagedOperation
    @Description( "Hold all requests to this POAManager" ) 
    public void hold_requests(boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
        explicitStateChange = true ;

        stateLock.writeLock().lock() ;

        try {
            if ( state.value() == State._INACTIVE ) {
                throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();
            }
            
            state  = State.HOLDING;

            pihandler.adapterManagerStateChanged( myId, getORTState() ) ;

            
            
            
            notifyWaiters();

            if ( wait_for_completion ) {
                while ( state.value() == State._HOLDING
                    && nInvocations.get() > 0 ) {

                    countedWait() ;
                }
            }
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    
    @Poa
    @ManagedOperation
    @ParameterNames( { "waitForCompletion" } )
    @Description( "Make this POAManager discard all incoming requests" ) 
    public void discard_requests(boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
        explicitStateChange = true ;

        stateLock.writeLock().lock();

        try {
            if ( state.value() == State._INACTIVE ) {
                throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();
            }

            
            state = State.DISCARDING;

            pihandler.adapterManagerStateChanged( myId, getORTState() ) ;

            
            
            
            
            
            
            notifyWaiters();

            if ( wait_for_completion ) {
                while ( state.value() == State._DISCARDING
                    && nInvocations.get() > 0 ) {

                    
                    countedWait() ;
                }
            }
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    

    @Poa
    public void deactivate(boolean etherealize_objects, boolean wait_for_completion)
        throws org.omg.PortableServer.POAManagerPackage.AdapterInactive
    {
        stateLock.writeLock().lock();

        try {
            explicitStateChange = true ;

            if ( state.value() == State._INACTIVE ) {
                throw new org.omg.PortableServer.POAManagerPackage.AdapterInactive();
            }

            state = State.INACTIVE;

            pihandler.adapterManagerStateChanged( myId, getORTState() ) ;

            
            
            
            
            notifyWaiters();
        } finally {
            stateLock.writeLock().unlock();
        }

        POAManagerDeactivator deactivator = new POAManagerDeactivator( this,
            etherealize_objects ) ;

        if (wait_for_completion) {
            deactivator.run();
        } else {
            Thread thr = new Thread(deactivator) ;
            thr.start() ;
        }
    }

    @Poa
    private static class POAManagerDeactivator implements Runnable
    {
        private boolean etherealize_objects ;
        private final POAManagerImpl pmi ;

        @InfoMethod
        private void poaManagerDeactivatorCall(
            boolean etherealizeObjects, POAManagerImpl pmi ) { }

        @InfoMethod
        private void preparingToEtherealize( POAManagerImpl pmi ) { }

        @InfoMethod
        private void removeAndClear( POAManagerImpl pmi ) { }

        POAManagerDeactivator( POAManagerImpl pmi, boolean etherealize_objects )
        {
            this.etherealize_objects = etherealize_objects ;
            this.pmi = pmi ;
        }

        @Poa
        public void run() 
        {
            pmi.stateLock.writeLock().lock();
            try {
                poaManagerDeactivatorCall( etherealize_objects, pmi ) ;
                while ( pmi.nInvocations.get() > 0 ) {
                    pmi.countedWait() ;
                }
            } finally {
                pmi.stateLock.writeLock().unlock();
            }

            if (etherealize_objects) {
                Set<POAImpl> copyOfPOAs ;

                
                pmi.stateLock.readLock().lock();
                try {
                    preparingToEtherealize( pmi ) ;
                    copyOfPOAs = new HashSet<POAImpl>( pmi.poas ) ;
                } finally {
                    pmi.stateLock.readLock().unlock();
                }

                for (POAImpl poa : copyOfPOAs) {
                    
                    
                    poa.etherealizeAll();
                }

                pmi.stateLock.writeLock().lock();
                try {
                    removeAndClear( pmi ) ;

                    pmi.factory.removePoaManager(pmi);
                    pmi.poas.clear();
                } finally {
                    pmi.stateLock.writeLock().unlock();
                }
            }
        }
    }

    

    public org.omg.PortableServer.POAManagerPackage.State get_state () {
        return state;
    }



    @InfoMethod
    private void activeManagers( MultiSet<POAManagerImpl> am ) { }

    @InfoMethod
    private void alreadyActive( POAManagerImpl pm ) { }

    @InfoMethod
    private void activeInDifferentPoaManager() { }

    @Poa
    private void checkState()
    {
        MultiSet<POAManagerImpl> am = activeManagers.get() ;
        activeManagers( am ) ;

        stateLock.readLock().lock();
        try {
            while ( state.value() != State._ACTIVE ) {
                switch ( state.value() ) {
                    case State._HOLDING:
                        
                        if (am.contains( this )) {
                            alreadyActive( this ) ;

                            return ;
                        } else {
                            if (am.size() == 0) {
                                if (state.value() == State._HOLDING) {
                                    
                                    
                                    stateLock.readLock().unlock();
                                    stateLock.writeLock().lock();
                                }

                                try {
                                    while ( state.value() == State._HOLDING ) {
                                        countedWait() ;
                                    }
                                } finally {
                                    
                                    stateLock.writeLock().unlock();
                                    stateLock.readLock().lock();
                                }
                            } else {
                                activeInDifferentPoaManager() ;

                                
                                
                                
                                throw factory.getWrapper().poaManagerMightDeadlock() ;
                            }
                        }
                        break;

                    case State._DISCARDING:
                        throw factory.getWrapper().poaDiscarding() ;

                    case State._INACTIVE:
                        throw factory.getWrapper().poaInactive() ;
                }
            }
        } finally {
            stateLock.readLock().unlock();
        }
    }

    @InfoMethod
    private void addingThreadToActiveManagers( POAManagerImpl pmi ) { }

    @InfoMethod
    private void removingThreadFromActiveManagers( POAManagerImpl pmi ) { }

    @Poa
    void enter()
    {
        checkState();
        nInvocations.getAndIncrement() ;

        activeManagers.get().add( this ) ;
        addingThreadToActiveManagers( this ) ;
    }

    @Poa
    void exit()
    {
        try {
            activeManagers.get().remove( this ) ;
            removingThreadFromActiveManagers( this ) ;
        } finally {
            if ( nInvocations.decrementAndGet() == 0 ) {
                
                
                
                
                
                
                
                final int num = nWaiters.get() ;
                nWaiters( num ) ;

                if (num >0) {
                    stateLock.writeLock().lock();

                    try {
                        stateCV.signalAll() ;
                    } finally {
                        stateLock.writeLock().unlock();
                    }
                }
            }
        }
    }

    
    public void implicitActivation() 
    {
        if (!explicitStateChange) {
            try {
                activate();
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ai) {
            }
        }
    }
}
