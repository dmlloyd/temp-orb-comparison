

package xxxx;


        


@TraceInterceptor
public class InterceptorInvoker {
    private ORB orb;

    private static final InterceptorsSystemException wrapper =
        InterceptorsSystemException.self ;

    
    private InterceptorList interceptorList;

    
    
    
    private boolean enabled = false;

    
    private PICurrent current;

    
    
    

    
    InterceptorInvoker( ORB orb, InterceptorList interceptorList, 
                        PICurrent piCurrent ) 
    {
        this.orb = orb;
        this.interceptorList = interceptorList;
        this.enabled = false;
        this.current = piCurrent;
    }

    
    synchronized void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    synchronized boolean getEnabled() {
        return this.enabled ;
    }
    
    

    
    @TraceInterceptor
    void objectAdapterCreated( ObjectAdapter oa ) {
        
        if( getEnabled() ) {
            
            IORInfoImpl info = new IORInfoImpl( oa );

            
            IORInterceptor[] iorInterceptors =
                (IORInterceptor[])interceptorList.getInterceptors(
                InterceptorList.INTERCEPTOR_TYPE_IOR );
            int size = iorInterceptors.length;

            
            
            
            
            
            

            for( int i = (size - 1); i >= 0; i-- ) {
                IORInterceptor interceptor = iorInterceptors[i];
                try {
                    interceptor.establish_components( info );
                } catch( Exception e ) {
                    
                    
                    
                    wrapper.ignoredExceptionInEstablishComponents( e, oa ) ;
                }
            }

            
            info.makeStateEstablished() ;

            for( int i = (size - 1); i >= 0; i-- ) {
                IORInterceptor interceptor = iorInterceptors[i];
                if (interceptor instanceof IORInterceptor_3_0) {
                    IORInterceptor_3_0 interceptor30 = (IORInterceptor_3_0)interceptor ;
                    
                    
                    try {
                        interceptor30.components_established( info );
                    } catch (Exception exc) {
                        wrapper.exceptionInComponentsEstablished( exc, oa ) ;
                    }
                }
            }

            
            
            
            
            info.makeStateDone() ;
        }
    }

    @TraceInterceptor
    void adapterManagerStateChanged( int managerId, short newState ) {
        if (getEnabled()) {
            IORInterceptor[] interceptors =
                (IORInterceptor[])interceptorList.getInterceptors(
                InterceptorList.INTERCEPTOR_TYPE_IOR );
            int size = interceptors.length;

            for( int i = (size - 1); i >= 0; i-- ) {
                try {
                    IORInterceptor interceptor = interceptors[i];
                    if (interceptor instanceof IORInterceptor_3_0) {
                        IORInterceptor_3_0 interceptor30 = (IORInterceptor_3_0)interceptor ;
                        interceptor30.adapter_manager_state_changed( managerId,
                            newState );
                    }
                } catch (Exception exc) {
                    
                    
                    wrapper.ignoredExceptionInAdapterManagerStateChanged(
                        exc, managerId, newState ) ;
                }
            }
        }
    }

    @TraceInterceptor
    void adapterStateChanged( ObjectReferenceTemplate[] templates,
        short newState ) {

        if (getEnabled()) {
            IORInterceptor[] interceptors =
                (IORInterceptor[])interceptorList.getInterceptors(
                InterceptorList.INTERCEPTOR_TYPE_IOR );
            int size = interceptors.length;

            for( int i = (size - 1); i >= 0; i-- ) {
                try {
                    IORInterceptor interceptor = interceptors[i];
                    if (interceptor instanceof IORInterceptor_3_0) {
                        IORInterceptor_3_0 interceptor30 = (IORInterceptor_3_0)interceptor ;
                        interceptor30.adapter_state_changed( templates, newState );
                    }
                } catch (Exception exc) {
                    
                    
                    wrapper.ignoredExceptionInAdapterStateChanged( exc,
                        Arrays.asList( templates ), newState ) ;
                }
            }
        }
    }

    

    @InfoMethod
    private void invokeClientStartingCall( String name ) { }

    @InfoMethod
    private void invokeClientStartingForwardRequest( String name ) { }

    @InfoMethod
    private void invokeClientStartingSystemException( String name,
        SystemException exc ) { }


    
    @TraceInterceptor
    void invokeClientInterceptorStartingPoint( ClientRequestInfoImpl info ) {
        info.interceptorsEnabledForThisRequest = getEnabled() ;

        
        if( info.interceptorsEnabledForThisRequest ) {
            try {
                
                
                
                current.pushSlotTable( );
                info.setPICurrentPushed( true );
                info.setCurrentExecutionPoint( 
                    ClientRequestInfoImpl.EXECUTION_POINT_STARTING );

                
                ClientRequestInterceptor[] clientInterceptors =
                    (ClientRequestInterceptor[])interceptorList.
                    getInterceptors( InterceptorList.INTERCEPTOR_TYPE_CLIENT );
                int size = clientInterceptors.length;

                
                
                
                int flowStackIndex = size;
                boolean continueProcessing = true;

                
                
                
                for( int i = 0; continueProcessing && (i < size); i++ ) {
                    ClientRequestInterceptor cri = clientInterceptors[i] ;

                    try {
                        invokeClientStartingCall( cri.name() );
                        cri.send_request( info );
                    } catch( ForwardRequest e ) {
                        invokeClientStartingForwardRequest( cri.name() );

                        
                        
                        
                        
                        flowStackIndex = i;
                        info.setForwardRequest( e );
                        info.setEndingPointCall(
                            ClientRequestInfoImpl.CALL_RECEIVE_OTHER );
                        info.setReplyStatus( LOCATION_FORWARD.value );

                        updateClientRequestDispatcherForward( info );

                        
                        
                        
                        
                        
                        continueProcessing = false;
                    } catch( SystemException e ) {
                        invokeClientStartingSystemException( cri.name(), e);

                        
                        
                        
                        
                        flowStackIndex = i;
                        info.setEndingPointCall(
                            ClientRequestInfoImpl.CALL_RECEIVE_EXCEPTION );
                        info.setReplyStatus( SYSTEM_EXCEPTION.value );
                        info.setException( e );

                        
                        
                        
                        
                        
                        continueProcessing = false;
                    }
                }

                
                info.setFlowStackIndex( flowStackIndex );
            } finally {
                
                current.resetSlotTable( );
            }
        } 
    }

    private String getClientEndMethodName( int endingPointCall ) {
        switch( endingPointCall ) {
        case ClientRequestInfoImpl.CALL_RECEIVE_REPLY:
            return "receive_reply" ;
        case ClientRequestInfoImpl.CALL_RECEIVE_EXCEPTION:
            return "receive_exception" ;
        case ClientRequestInfoImpl.CALL_RECEIVE_OTHER:
            return "receive_other" ;
        }
        return "" ;
    }

    @InfoMethod
    private void invokeClientEndingCall( String name, String call ) { }

    @InfoMethod
    private void invokeClientEndingForwardRequest( String name ) { }

    @InfoMethod
    private void invokeClientEndingSystemException( String name,
        SystemException exc ) { }

    
    @TraceInterceptor
    void invokeClientInterceptorEndingPoint( ClientRequestInfoImpl info ) {
        
        if( info.interceptorsEnabledForThisRequest ) {
            try {
                
                

                info.setCurrentExecutionPoint( 
                    ClientRequestInfoImpl.EXECUTION_POINT_ENDING );

                
                ClientRequestInterceptor[] clientInterceptors =
                    (ClientRequestInterceptor[])interceptorList.
                    getInterceptors( InterceptorList.INTERCEPTOR_TYPE_CLIENT );
                int flowStackIndex = info.getFlowStackIndex();

                
                
                int endingPointCall = info.getEndingPointCall();

                
                
                if( ( endingPointCall ==
                      ClientRequestInfoImpl.CALL_RECEIVE_REPLY ) &&
                    info.getIsOneWay() )
                {
                    endingPointCall = ClientRequestInfoImpl.CALL_RECEIVE_OTHER;
                    info.setEndingPointCall( endingPointCall );
                }

                
                
                
                
                
                for( int i = (flowStackIndex - 1); i >= 0; i-- ) {
                    ClientRequestInterceptor cri = clientInterceptors[i] ;

                    try {
                        invokeClientEndingCall( cri.name(),
                                getClientEndMethodName( endingPointCall ) ) ;

                        switch( endingPointCall ) {
                        case ClientRequestInfoImpl.CALL_RECEIVE_REPLY:
                            cri.receive_reply( info );
                            break;
                        case ClientRequestInfoImpl.CALL_RECEIVE_EXCEPTION:
                            cri.receive_exception( info );
                            break;
                        case ClientRequestInfoImpl.CALL_RECEIVE_OTHER:
                            cri.receive_other( info );
                            break;
                        }
                    } catch( ForwardRequest e ) {
                        invokeClientEndingForwardRequest( cri.name() );

                        
                        
                        
                        endingPointCall =
                            ClientRequestInfoImpl.CALL_RECEIVE_OTHER;
                        info.setEndingPointCall( endingPointCall );
                        info.setReplyStatus( LOCATION_FORWARD.value );
                        info.setForwardRequest( e );
                        updateClientRequestDispatcherForward( info );
                    } catch( SystemException e ) {
                        invokeClientEndingSystemException( cri.name(), e);

                        
                        
                        
                        endingPointCall =
                            ClientRequestInfoImpl.CALL_RECEIVE_EXCEPTION;
                        info.setEndingPointCall( endingPointCall );
                        info.setReplyStatus( SYSTEM_EXCEPTION.value );
                        info.setException( e );
                    }
                }
            } finally {
                
                
                if (info.isPICurrentPushed()) {
                    current.popSlotTable( );
                    
                    
                }
            }
        } 
    }

    

    @InfoMethod
    private void invokeServerStartingCall( String name ) { }

    @InfoMethod
    private void invokeServerStartingForwardRequest( String name ) { }

    @InfoMethod
    private void invokeServerStartingSystemException( String name,
        SystemException exc ) { }

    
    @TraceInterceptor
    void invokeServerInterceptorStartingPoint( ServerRequestInfoImpl info ) {
        info.interceptorsEnabledForThisRequest = getEnabled() ;

        
        if( info.interceptorsEnabledForThisRequest ) {
            try {
                
                current.pushSlotTable();
                info.setSlotTable(current.getSlotTable());

                
                
                current.pushSlotTable( );

                info.setCurrentExecutionPoint( 
                    ServerRequestInfoImpl.EXECUTION_POINT_STARTING );

                
                ServerRequestInterceptor[] serverInterceptors =
                    (ServerRequestInterceptor[])interceptorList.
                    getInterceptors( InterceptorList.INTERCEPTOR_TYPE_SERVER );
                int size = serverInterceptors.length;

                
                
                
                int flowStackIndex = size;
                boolean continueProcessing = true;

                
                
                for( int i = 0; continueProcessing && (i < size); i++ ) {
                    ServerRequestInterceptor sri = serverInterceptors[i] ;
                    try {
                        invokeServerStartingCall( sri.name() );
                        sri.receive_request_service_contexts( info );
                    } catch( ForwardRequest e ) {
                        invokeServerStartingForwardRequest( sri.name() );

                        
                        
                        
                        
                        flowStackIndex = i;
                        info.setForwardRequest( e );
                        info.setIntermediatePointCall(
                            ServerRequestInfoImpl.CALL_INTERMEDIATE_NONE );
                        info.setEndingPointCall(
                            ServerRequestInfoImpl.CALL_SEND_OTHER );
                        info.setReplyStatus( LOCATION_FORWARD.value );

                        
                        
                        
                        
                        
                        continueProcessing = false;
                    } catch( SystemException e ) {
                        invokeServerStartingSystemException( sri.name(), e);

                        
                        
                        
                        
                        flowStackIndex = i;
                        info.setException( e );
                        info.setIntermediatePointCall(
                            ServerRequestInfoImpl.CALL_INTERMEDIATE_NONE );
                        info.setEndingPointCall(
                            ServerRequestInfoImpl.CALL_SEND_EXCEPTION );
                        info.setReplyStatus( SYSTEM_EXCEPTION.value );

                        
                        
                        
                        
                        
                        continueProcessing = false;
                    }
                }

                
                info.setFlowStackIndex( flowStackIndex );
            } finally {
                
                
                current.popSlotTable( );
                
            }
        } 
    }

    @InfoMethod
    private void invokeServerIntermediateCall( String name ) { }

    @InfoMethod
    private void invokeServerIntermediateForwardRequest( String name ) { }

    @InfoMethod
    private void invokeServerIntermediateSystemException( String name,
        SystemException exc ) { }

    
    @TraceInterceptor
    void invokeServerInterceptorIntermediatePoint( 
        ServerRequestInfoImpl info ) {

        int intermediatePointCall = info.getIntermediatePointCall();
        
        if( info.interceptorsEnabledForThisRequest && ( intermediatePointCall !=
                         ServerRequestInfoImpl.CALL_INTERMEDIATE_NONE ) ) {

            
            

            info.setCurrentExecutionPoint( 
                ServerRequestInfoImpl.EXECUTION_POINT_INTERMEDIATE );

            
            ServerRequestInterceptor[] serverInterceptors =
                (ServerRequestInterceptor[])
                interceptorList.getInterceptors(
                InterceptorList.INTERCEPTOR_TYPE_SERVER );
            int size = serverInterceptors.length;

            
            
            for( int i = 0; i < size; i++ ) {
                ServerRequestInterceptor sri = serverInterceptors[i] ;
                try {
                    invokeServerIntermediateCall( sri.name() );
                    sri.receive_request( info );
                } catch( ForwardRequest e ) {
                    invokeServerIntermediateForwardRequest( sri.name() );

                    
                    
                    
                    
                    info.setForwardRequest( e );
                    info.setEndingPointCall(
                        ServerRequestInfoImpl.CALL_SEND_OTHER );
                    info.setReplyStatus( LOCATION_FORWARD.value );
                    break;
                } catch( SystemException e ) {
                    invokeServerIntermediateSystemException( sri.name(), e);

                    
                    
                    
                    
                    info.setException( e );
                    info.setEndingPointCall(
                        ServerRequestInfoImpl.CALL_SEND_EXCEPTION );
                    info.setReplyStatus( SYSTEM_EXCEPTION.value );
                    break;
                }
            }
        } 
    }

    private String getServerEndMethodName( int endingPointCall ) {
        switch( endingPointCall ) {
        case ServerRequestInfoImpl.CALL_SEND_REPLY:
            return "send_reply" ;
        case ServerRequestInfoImpl.CALL_SEND_EXCEPTION:
            return "send_exception" ;
        case ServerRequestInfoImpl.CALL_SEND_OTHER:
            return "send_other" ;
        }
        return "" ;
    }

    @InfoMethod
    private void serverInvokeEndingPoint( String name, String call ) { }

    @InfoMethod
    private void caughtForwardRequest( String name ) { }

    @InfoMethod
    private void caughtSystemException( String name, SystemException ex ) { }

    
    @TraceInterceptor
    void invokeServerInterceptorEndingPoint( ServerRequestInfoImpl info ) {
        
        if( info.interceptorsEnabledForThisRequest ) {
            try {
                
                

                
                
                
                
                

                
                ServerRequestInterceptor[] serverInterceptors =
                    (ServerRequestInterceptor[])interceptorList.
                    getInterceptors( InterceptorList.INTERCEPTOR_TYPE_SERVER );
                int flowStackIndex = info.getFlowStackIndex();

                
                
                int endingPointCall = info.getEndingPointCall();

                
                
                for( int i = (flowStackIndex - 1); i >= 0; i-- ) {
                    ServerRequestInterceptor sri = serverInterceptors[i] ;

                    try {
                        serverInvokeEndingPoint( sri.name(),
                            getServerEndMethodName( endingPointCall ) ) ;

                        switch( endingPointCall ) {
                        case ServerRequestInfoImpl.CALL_SEND_REPLY:
                            sri.send_reply( info );
                            break;
                        case ServerRequestInfoImpl.CALL_SEND_EXCEPTION:
                            sri.send_exception( info );
                            break;
                        case ServerRequestInfoImpl.CALL_SEND_OTHER:
                            sri.send_other( info );
                            break;
                        }
                    } catch( ForwardRequest e ) {
                        caughtForwardRequest( sri.name() ) ;

                        
                        
                        
                        endingPointCall =
                            ServerRequestInfoImpl.CALL_SEND_OTHER;
                        info.setEndingPointCall( endingPointCall );
                        info.setForwardRequest( e );
                        info.setReplyStatus( LOCATION_FORWARD.value );
                        info.setForwardRequestRaisedInEnding();
                    } catch( SystemException e ) {
                        caughtSystemException( sri.name(), e ) ;

                        
                        
                        
                        endingPointCall =
                            ServerRequestInfoImpl.CALL_SEND_EXCEPTION;
                        info.setEndingPointCall( endingPointCall );
                        info.setException( e );
                        info.setReplyStatus( SYSTEM_EXCEPTION.value );
                    }
                }

                
                
                info.setAlreadyExecuted( true );
            } finally {
                
                current.popSlotTable();
            }
        } 
    }
    
    
    
    
    @TraceInterceptor
    private void updateClientRequestDispatcherForward( 
        ClientRequestInfoImpl info ) {

        ForwardRequest forwardRequest = info.getForwardRequestException();

        
        
        
        
        if( forwardRequest != null ) {
            org.omg.CORBA.Object object = forwardRequest.forward;

            
            IOR ior = orb.getIOR( object, false ) ;
            info.setLocatedIOR( ior );
        }
    }
    
}
