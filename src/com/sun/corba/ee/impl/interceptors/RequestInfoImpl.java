

package xxxx;





public abstract class RequestInfoImpl 
    extends LocalObject 
    implements RequestInfo, RequestInfoExt
{
    protected static final InterceptorsSystemException wrapper =
        InterceptorsSystemException.self ;
    static final OMGSystemException stdWrapper =
        OMGSystemException.self ;

    
    
    
    
    

    
    protected ORB myORB;

    
    
    private int flowStackIndex = 0;

    
    
    
    int startingPointCall;
    
    
    
    
    
    int intermediatePointCall;
    
    
    
    
    int endingPointCall;
    
    
    
    protected short replyStatus = UNINITIALIZED;
    
    
    static final short UNINITIALIZED = -1;
    
    
    
    int currentExecutionPoint;
    static final int EXECUTION_POINT_STARTING = 0;
    static final int EXECUTION_POINT_INTERMEDIATE = 1;
    static final int EXECUTION_POINT_ENDING = 2;
    
    
    
    private boolean alreadyExecuted;
    
    
    protected Connection     connection;

    
    
    
    
    
    private ForwardRequest forwardRequest;
    private IOR forwardRequestIOR;

    
    SlotTable slotTable;

    
    
    protected Exception exception;
   
    boolean interceptorsEnabledForThisRequest ;

    
    
    
    
    

    @Override
    public org.omg.CORBA.ORB _orb() {
        return myORB;
    }

    
    void reset() {

        

        flowStackIndex = 0;
        startingPointCall = 0;
        intermediatePointCall = 0;
        endingPointCall = 0;
        
        setReplyStatus( UNINITIALIZED ) ;
        currentExecutionPoint = EXECUTION_POINT_STARTING;
        alreadyExecuted = false;
        connection = null;
        ServiceContexts serviceContexts = null;
        forwardRequest = null;
        forwardRequestIOR = null;
        exception = null;

        
        
        

        interceptorsEnabledForThisRequest = false ;
    }

    

    
    
    protected static final int MID_REQUEST_ID                   =  0;
    protected static final int MID_OPERATION                    =  1;
    protected static final int MID_ARGUMENTS                    =  2;
    protected static final int MID_EXCEPTIONS                   =  3;
    protected static final int MID_CONTEXTS                     =  4;
    protected static final int MID_OPERATION_CONTEXT            =  5;
    protected static final int MID_RESULT                       =  6;
    protected static final int MID_RESPONSE_EXPECTED            =  7;
    protected static final int MID_SYNC_SCOPE                   =  8;
    protected static final int MID_REPLY_STATUS                 =  9;
    protected static final int MID_FORWARD_REFERENCE            = 10;
    protected static final int MID_GET_SLOT                     = 11;
    protected static final int MID_GET_REQUEST_SERVICE_CONTEXT  = 12;
    protected static final int MID_GET_REPLY_SERVICE_CONTEXT    = 13;
    
    protected static final int MID_RI_LAST                      = 13;
    
    
         
    
    public RequestInfoImpl( ORB myORB ) { 
        super();
        
        this.myORB = myORB;

        
        PICurrent current = (PICurrent)(myORB.getPIHandler().getPICurrent());
        slotTable = current.getSlotTable( );

        interceptorsEnabledForThisRequest = false ;
    }

    
    abstract public int request_id ();

    
    abstract public String operation ();


    
    abstract public Parameter[] arguments ();
    
    
    abstract public TypeCode[] exceptions ();
    
    
    abstract public String[] contexts ();
    
    
    abstract public String[] operation_context ();

    
    abstract public Any result ();
    
    
    abstract public boolean response_expected ();
    
    
    public short sync_scope (){
        checkAccess( MID_SYNC_SCOPE );
        return SYNC_WITH_TRANSPORT.value; 
    }
    
    
    public short reply_status (){
        checkAccess( MID_REPLY_STATUS );
        return replyStatus;
    }
    
    
    abstract public Object forward_reference ();

    
    
    public Any get_slot (int id) 
        throws InvalidSlot
    {
        
        
        
        
        return slotTable.get_slot( id );
    }

    
    abstract public org.omg.IOP.ServiceContext 
        get_request_service_context(int id);

    
    abstract public org.omg.IOP.ServiceContext
        get_reply_service_context (int id);

    
    
    
    
    
    


    
    

    
    public com.sun.corba.ee.spi.legacy.connection.Connection connection()
    {
        return connection;
    }

    

    
    private void insertApplicationException( ApplicationException appException,
                                             Any result )
        throws UNKNOWN
    {
        try {
            
            
            RepositoryId repId = RepositoryId.cache.getId(
                appException.getId() );
            String className = repId.getClassName();
           
            
            String helperClassName = className + "Helper";
            Class helperClass = ORBClassLoader.loadClass( helperClassName );
            Class[] readParams = new Class[1];
            readParams[0] = org.omg.CORBA.portable.InputStream.class;
            Method readMethod = helperClass.getMethod( "read", readParams );
           
            
            
            
            InputStream ueInputStream = appException.getInputStream();
            ueInputStream.mark( 0 );
            UserException userException = null;
            try {
                java.lang.Object[] readArguments = new java.lang.Object[1];
                readArguments[0] = ueInputStream;
                userException = (UserException)readMethod.invoke(
                    null, readArguments );
            }
            finally {
                try {
                    ueInputStream.reset();
                }
                catch( IOException e ) {
                    throw wrapper.markAndResetFailed( e ) ;
                }
            }

            
            
            insertUserException( userException, result );
        } catch( ClassNotFoundException e ) {
            throw stdWrapper.unknownUserException( e ) ;
        } catch( NoSuchMethodException e ) {
            throw stdWrapper.unknownUserException( e ) ;
        } catch( SecurityException e ) {
            throw stdWrapper.unknownUserException( e ) ;
        } catch( IllegalAccessException e ) {
            throw stdWrapper.unknownUserException( e ) ;
        } catch( IllegalArgumentException e ) {
            throw stdWrapper.unknownUserException( e ) ;
        } catch( InvocationTargetException e ) {
            throw stdWrapper.unknownUserException( e ) ;
        }
    }

    
    private void insertUserException( UserException userException, Any result )
        throws UNKNOWN
    {
        try {
            
            
            if( userException != null ) {
                Class exceptionClass = userException.getClass();
                String className = exceptionClass.getName();
                String helperClassName = className + "Helper";
                Class helperClass = ORBClassLoader.loadClass( helperClassName );
           
                Method insertMethod = helperClass.getMethod(
                    "insert", org.omg.CORBA.Any.class, exceptionClass );

                insertMethod.invoke( null, result, userException );
            }
        } catch( ClassNotFoundException e ) {
            throw stdWrapper.unknownUserException( e );
        } catch( NoSuchMethodException e ) {
            throw stdWrapper.unknownUserException( e );
        } catch( SecurityException e ) {
            throw stdWrapper.unknownUserException( e );
        } catch( IllegalAccessException e ) {
            throw stdWrapper.unknownUserException( e );
        } catch( IllegalArgumentException e ) {
            throw stdWrapper.unknownUserException( e );
        } catch( InvocationTargetException e ) {
            throw stdWrapper.unknownUserException( e );
        } 
    }

    

    
    protected Parameter[] nvListToParameterArray( NVList parNVList ) {

        
        

        int count = parNVList.count();
        Parameter[] plist = new Parameter[count];
        try {
            for( int i = 0; i < count; i++ ) {
                Parameter p = new Parameter();
                plist[i] = p;
                NamedValue nv = parNVList.item( i );
                plist[i].argument = nv.value();
                
                
                
                
                
                
                
                plist[i].mode = ParameterMode.from_int( nv.flags() - 1 );
            }
        } catch ( Exception e ) {
            throw wrapper.exceptionInArguments( e ) ;
        }

        return plist;
    }

    
    protected Any exceptionToAny( Exception exception ){
        Any result = myORB.create_any();

        if( exception == null ) {
            
            
            throw wrapper.exceptionWasNull2() ;
        } else if( exception instanceof SystemException ) {
            ORBUtility.insertSystemException(
                (SystemException)exception, result );
        } else if( exception instanceof ApplicationException ) {
            
            
            try {
                
                
                ApplicationException appException =
                    (ApplicationException)exception;
                insertApplicationException( appException, result );
            } catch( UNKNOWN e ) {
                
                
                
                
                
                ORBUtility.insertSystemException( e, result );
            }
        } else if( exception instanceof UserException ) {
            try {
                UserException userException = (UserException)exception;
                insertUserException( userException, result );
            } catch( UNKNOWN e ) {
                ORBUtility.insertSystemException( e, result );
            }
        }


        return result;
    }

    
    protected org.omg.IOP.ServiceContext getServiceContext( 
        Map<Integer,org.omg.IOP.ServiceContext> cachedServiceContexts, 
        ServiceContexts serviceContexts, int id )
    {
        org.omg.IOP.ServiceContext result = null;

        
        result = cachedServiceContexts.get( id );

        
        
        
        if( result == null ) {
            
            
            com.sun.corba.ee.spi.servicecontext.ServiceContext context = 
                serviceContexts.get( id );

            if (context == null) {
                
                
                if (myORB.getORBData().getServiceContextReturnsNull()) {
                    return null ;
                } else {
                    throw stdWrapper.invalidServiceContextId() ;
                }
            }

            
            
            
            EncapsOutputStream out = OutputStreamFactory.newEncapsOutputStream(myORB);

            context.write( out, GIOPVersion.V1_2 );
            InputStream inputStream = out.create_input_stream();
            result = ServiceContextHelper.read( inputStream );

            cachedServiceContexts.put( id, result );
        }

        
        
        

        return result;
    }


    
    protected void addServiceContext( 
        Map<Integer, org.omg.IOP.ServiceContext> cachedServiceContexts, 
        ServiceContexts serviceContexts, 
        org.omg.IOP.ServiceContext service_context,
        boolean replace )
    {
        int id = 0 ;
        
        EncapsOutputStream outputStream = OutputStreamFactory.newEncapsOutputStream(
            myORB );
        InputStream inputStream = null;
        UnknownServiceContext coreServiceContext = null;
        ServiceContextHelper.write( outputStream, service_context );
        inputStream = outputStream.create_input_stream();

        
        coreServiceContext = ServiceContextDefaults.makeUnknownServiceContext(
            inputStream.read_long(),
            (org.omg.CORBA_2_3.portable.InputStream)inputStream );

        id = coreServiceContext.getId();

        if (serviceContexts.get(id) != null) {
            if (replace) {
                serviceContexts.delete(id);
            } else {
                throw stdWrapper.serviceContextAddFailed(id);
            }
        }

        serviceContexts.put( coreServiceContext );

        
        cachedServiceContexts.put( id, service_context );
    }

    
    protected void setFlowStackIndex(int num ) {
        this.flowStackIndex = num;
    }

    
    protected int getFlowStackIndex() {
        return this.flowStackIndex;
    }

    
    protected void setEndingPointCall( int call ) {
        this.endingPointCall = call;
    }

    
    protected int getEndingPointCall() {
        return this.endingPointCall;
    }

    
    protected void setIntermediatePointCall( int call ) {
        this.intermediatePointCall = call;
    }

    
    protected int getIntermediatePointCall() {
        return this.intermediatePointCall;
    }
    
    
    protected void setStartingPointCall( int call ) {
        this.startingPointCall = call;
    }

    
    protected int getStartingPointCall() {
        return this.startingPointCall;
    }
    
    
    protected boolean getAlreadyExecuted() {
        return this.alreadyExecuted;
    }
    
    
    protected void setAlreadyExecuted( boolean alreadyExecuted ) {
        this.alreadyExecuted = alreadyExecuted;
    }
    
    
    protected void setReplyStatus( short replyStatus ) {
        this.replyStatus = replyStatus;
    }
    
    
    protected short getReplyStatus() {
        return this.replyStatus;
    }
    
    
    protected void setForwardRequest( ForwardRequest forwardRequest ) {
        this.forwardRequest = forwardRequest;
        this.forwardRequestIOR = null;
    }

    
    protected void setForwardRequest( IOR ior ) {
        this.forwardRequestIOR = ior;
        this.forwardRequest = null;
    }
    
    
    protected ForwardRequest getForwardRequestException() {
        if( this.forwardRequest == null ) {
            if( this.forwardRequestIOR != null ) {
                
                
                org.omg.CORBA.Object obj = iorToObject(this.forwardRequestIOR);
                this.forwardRequest = new ForwardRequest( obj );
            }
        }

        return this.forwardRequest;
    }

    
    protected IOR getForwardRequestIOR() {
        if( this.forwardRequestIOR == null ) {
            if( this.forwardRequest != null ) {
                this.forwardRequestIOR = myORB.getIOR(
                    this.forwardRequest.forward, false ) ;
            }
        }

        return this.forwardRequestIOR;
    }
    
    
    protected void setException( Exception exception ) {
        this.exception = exception;
    }
    
    
    Exception getException() {
        return this.exception;
    }
   
    
    protected void setCurrentExecutionPoint( int executionPoint ) {
        this.currentExecutionPoint = executionPoint;
    }

    
    protected abstract void checkAccess( int methodID ) 
        throws BAD_INV_ORDER;

    
    void setSlotTable(SlotTable slotTable)
    {
        this.slotTable = slotTable;
    }

    protected org.omg.CORBA.Object iorToObject( IOR ior )
    {
        return ORBUtility.makeObjectReference( ior ) ;
    }
}
