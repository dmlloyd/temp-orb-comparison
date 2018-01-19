


package com.sun.corba.ee.impl.protocol;










@Subcontract
@IsLocal
public abstract class LocalClientRequestDispatcherBase implements LocalClientRequestDispatcher
{
    protected static final POASystemException poaWrapper =
        POASystemException.self ;
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    
    
    
    private static final int INITIAL_BACKOFF = 1 ;  
                                                    
                                                   
    private static final int MAX_BACKOFF   = 1000 ;   
    private static final int MAX_WAIT_TIME = 10 * 1000 ; 

    protected ORB orb;
    private int scid;

    
    protected boolean servantIsLocal ;
    protected ObjectAdapterFactory oaf ;
    protected ObjectAdapterId oaid ;
    protected byte[] objectId ;

    
    
    private static final ThreadLocal isNextCallValid = new ThreadLocal() {
        @Override
        protected synchronized Object initialValue() {
            return Boolean.TRUE;
        }
    };

    protected LocalClientRequestDispatcherBase(ORB orb, int scid, IOR ior)
    {
        this.orb = orb ;

        TaggedProfile prof = ior.getProfile() ;
        servantIsLocal = orb.getORBData().isLocalOptimizationAllowed() && 
            prof.isLocal();

        ObjectKeyTemplate oktemp = prof.getObjectKeyTemplate() ;
        this.scid = oktemp.getSubcontractId() ;
        RequestDispatcherRegistry sreg = orb.getRequestDispatcherRegistry() ;
        oaf = sreg.getObjectAdapterFactory( scid ) ;
        oaid = oktemp.getObjectAdapterId() ;
        ObjectId oid = prof.getObjectId() ;
        objectId = oid.getId() ;
    }

    public byte[] getObjectId() 
    {
        return objectId ;
    }

    @IsLocal
    public boolean is_local(org.omg.CORBA.Object self)
    {
        return false;
    }

    
    @IsLocal
    public boolean useLocalInvocation( org.omg.CORBA.Object self ) 
    {
        if (isNextCallValid.get() == Boolean.TRUE) {
            return servantIsLocal;
        } else {
            isNextCallValid.set(Boolean.TRUE);
        }

        return false ;    
    }

    @InfoMethod
    private void servantNotCompatible() {}

    
    @IsLocal
    protected boolean checkForCompatibleServant( ServantObject so, 
        Class expectedType )
    {
        if (so == null) {
            return false;
        }

        
        
        
        if (!expectedType.isInstance( so.servant )) {
            servantNotCompatible() ;
            isNextCallValid.set( Boolean.FALSE ) ;

            
            
            
            return false ;
        }

        return true ;
    }

    
    
    
    protected ServantObject internalPreinvoke( 
        org.omg.CORBA.Object self, String operation, 
        Class expectedType ) throws OADestroyed 
    {
        return null ;
    }

    
    
    protected void cleanupAfterOADestroyed() 
    {
        
    }

    @InfoMethod
    private void display( String msg ) { }

    @InfoMethod
    private void display( String msg, int value ) { }

    @InfoMethod
    private void display( String msg, Object value ) { }


    
    
    @Subcontract
    public ServantObject servant_preinvoke( org.omg.CORBA.Object self,
        String operation, Class expectedType ) {

        long startTime = -1 ;
        long backoff = INITIAL_BACKOFF ;
        long maxWait = MAX_WAIT_TIME ;

        while (true) {
            try {
                display( "Calling internalPreinvoke") ;
                return internalPreinvoke( self, operation, expectedType ) ;
            } catch (OADestroyed pdes) {
                display( "Caught OADestroyed: will retry") ;
                cleanupAfterOADestroyed() ;
            } catch (TRANSIENT exc) {
                display( "Caught transient") ;
                
                
                
                long currentTime = System.currentTimeMillis() ;

                
                
                if (startTime == -1) {
                    display( "backoff (first retry)", backoff ) ;
                    startTime = currentTime ;
                } else if ((currentTime-startTime) > MAX_WAIT_TIME) {
                    display( "Total time exceeded", MAX_WAIT_TIME ) ;
                    throw exc ;
                } else {
                    backoff *= 2 ;
                    if (backoff > MAX_BACKOFF) {
                        backoff = MAX_BACKOFF ;
                    }

                    display( "increasing backoff (will retry)", backoff ) ;
                }

                try {
                    Thread.sleep( backoff ) ;
                } catch (InterruptedException iexc) {
                    
                }

                display( "retry" ) ;
            } catch ( ForwardException ex ) {
                
                display( "Unsupported ForwardException" ) ;
                throw new RuntimeException("deal with this.", ex) ;
            } catch ( ThreadDeath ex ) {
                
                
                
                
                
                
                display( "Caught ThreadDeath") ;
                throw wrapper.runtimeexception( ex, ex.getClass().getName(), ex.getMessage() ) ;
            } catch ( Throwable t ) {
                display( "Caught Throwable") ;

                if (t instanceof SystemException)
                    throw (SystemException)t ;

                throw poaWrapper.localServantLookup( t ) ;
            }
        }
    }
}


