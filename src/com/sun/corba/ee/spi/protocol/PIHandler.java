


package com.sun.corba.ee.spi.protocol;









public interface PIHandler extends Closeable {
    
    public void initialize() ;
    
    public void destroyInterceptors() ;

    

    
    void objectAdapterCreated( ObjectAdapter oa )  ;

    
    void adapterManagerStateChanged( int managerId,
        short newState ) ;

    
    void adapterStateChanged( ObjectReferenceTemplate[] templates,
        short newState ) ;

    

    
    void disableInterceptorsThisThread() ;

    
    void enableInterceptorsThisThread() ;

    
    void invokeClientPIStartingPoint()
        throws RemarshalException ;

    
    Exception invokeClientPIEndingPoint(
        int replyStatus, Exception exception ) ;

    
    Exception makeCompletedClientRequest(
        int replyStatus, Exception exception ) ;

    
    void initiateClientPIRequest( boolean diiRequest ) ;

    
    void cleanupClientPIRequest() ;

    
    void setClientPIInfo( RequestImpl requestImpl ) ;

    
    void setClientPIInfo(MessageMediator messageMediator) ;

    

    
    void invokeServerPIStartingPoint() ;

    
    void invokeServerPIIntermediatePoint() ;

    
    void invokeServerPIEndingPoint( ReplyMessage replyMessage ) ;

    
    void initializeServerPIInfo( MessageMediator request,
        ObjectAdapter oa, byte[] objectId, ObjectKeyTemplate oktemp ) ;

    
    void setServerPIInfo( java.lang.Object servant,
                                    String targetMostDerivedInterface ) ;

    
    void setServerPIInfo( Exception exception ) ;

    
    void setServerPIInfo( NVList arguments ) ;

    
    void setServerPIExceptionInfo( Any exception ) ;

    
    void setServerPIInfo( Any result ) ;

    
    void cleanupServerPIRequest() ;

    Policy create_policy( int type, Any val ) throws PolicyError ;

    void register_interceptor( Interceptor interceptor, int type ) 
        throws DuplicateName ;

    Current getPICurrent() ;

    void registerPolicyFactory( int type, PolicyFactory factory ) ;

    int allocateServerRequestId() ;
}
