

package com.sun.corba.ee.impl.interceptors;

import java.util.*;
             
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_POLICY;
import org.omg.CORBA.NVList;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;

import org.omg.IOP.CodecFactory;

import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.TRANSPORT_RETRY;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate ;

import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.ior.ObjectKeyTemplate;
import com.sun.corba.ee.spi.oa.ObjectAdapter;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import com.sun.corba.ee.spi.protocol.ForwardException;
import com.sun.corba.ee.spi.protocol.PIHandler;
import com.sun.corba.ee.spi.protocol.RetryType ;

import com.sun.corba.ee.spi.logging.InterceptorsSystemException;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.spi.logging.OMGSystemException;
import com.sun.corba.ee.impl.corba.RequestImpl;

import com.sun.corba.ee.spi.misc.ORBConstants;

import com.sun.corba.ee.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.ee.spi.trace.TraceInterceptor;
import org.glassfish.pfl.basic.func.NullaryFunction;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;


@TraceInterceptor
public class PIHandlerImpl implements PIHandler 
{
    private ORB orb ;

    static final InterceptorsSystemException wrapper =
        InterceptorsSystemException.self ;
    static final ORBUtilSystemException orbutilWrapper =
        ORBUtilSystemException.self ;
    static final OMGSystemException omgWrapper =
        OMGSystemException.self ;

    
    
    private int serverRequestIdCounter = 0;

    
    CodecFactory codecFactory = null;

    
    
    String[] arguments = null;

    
    private InterceptorList interceptorList;

    
    
    private boolean hasIORInterceptors;
    private boolean hasClientInterceptors;  
    private boolean hasServerInterceptors;

    
    private InterceptorInvoker interceptorInvoker;

    
    private PICurrent current;

    
    
    
    
    private Map<Integer,PolicyFactory> policyFactoryTable;
    
    
    
    
    private final static short REPLY_MESSAGE_TO_PI_REPLY_STATUS[] = {
        SUCCESSFUL.value,       
        USER_EXCEPTION.value,   
        SYSTEM_EXCEPTION.value, 
        LOCATION_FORWARD.value, 
        LOCATION_FORWARD.value, 
        TRANSPORT_RETRY.value   
    };
        
    static String getReplyStatus( int piReplyStatus ) {
        switch (piReplyStatus) {
            case SUCCESSFUL.value: return "SUCCESSFUL" ;
            case USER_EXCEPTION.value: return "USER_EXCEPTION" ;
            case SYSTEM_EXCEPTION.value: return "SYSTEM_EXCEPTION" ;
            case LOCATION_FORWARD.value: return "LOCATION_FORWARD" ;
            case TRANSPORT_RETRY.value: return "TRANSPORT_RETRY" ;
            default: return "UNINITIALIZED" ;
        }
    }

    
    
    private ThreadLocal<RequestInfoStack<ClientRequestInfoImpl>> 
        threadLocalClientRequestInfoStack = 
            new ThreadLocal<RequestInfoStack<ClientRequestInfoImpl>>() {
                @Override
                protected RequestInfoStack<ClientRequestInfoImpl> initialValue() {
                    return new RequestInfoStack<ClientRequestInfoImpl>();
                }
        };

    
    private ThreadLocal<RequestInfoStack<ServerRequestInfoImpl>>
        threadLocalServerRequestInfoStack =
        new ThreadLocal<RequestInfoStack<ServerRequestInfoImpl>>() {
            @Override
            protected RequestInfoStack<ServerRequestInfoImpl> initialValue() {
                return new RequestInfoStack<ServerRequestInfoImpl>();
            }
        };
    
    @TraceInterceptor
    public void close() {
        orb = null ;
        codecFactory = null ;
        arguments = null ;
        interceptorList = null ;
        interceptorInvoker = null ;
        current = null ;
        policyFactoryTable = null ;
        threadLocalClientRequestInfoStack = null ;
        threadLocalServerRequestInfoStack = null ;
    }
    
    
    
    
    private static final class RequestInfoStack<C extends RequestInfoImpl> extends Stack<C> {
        
        
        
        
        public int disableCount = 0;

        
        @Override
        public boolean equals( Object o ) {
            return super.equals( o ) ;
        }
        
        @Override
        public int hashCode() {
            return super.hashCode() ;
        }
    }
        
    public PIHandlerImpl( ORB orb, String[] args ) {
        this.orb = orb ;

        if (args == null) {
            arguments = null ;
        } else {
            arguments = args.clone() ;
        }

        
        codecFactory = new CodecFactoryImpl( orb );

        
        interceptorList = new InterceptorList();

        
        current = new PICurrent( orb );

        
        interceptorInvoker = new InterceptorInvoker( orb, interceptorList,
                                                     current );

        
        orb.getLocalResolver().register( ORBConstants.PI_CURRENT_NAME,
            NullaryFunction.Factory.makeConstant( 
                (org.omg.CORBA.Object)current ) ) ;
        orb.getLocalResolver().register( ORBConstants.CODEC_FACTORY_NAME,
            NullaryFunction.Factory.makeConstant(
                (org.omg.CORBA.Object)codecFactory ) ) ;
        hasClientInterceptors = true ;  
                                        
                                        
                                        
                                        
        hasServerInterceptors = true ;  
    }

    @TraceInterceptor
    public void initialize() {
        
        if( orb.getORBData().getORBInitializers() != null ) {
            
            ORBInitInfoImpl orbInitInfo = createORBInitInfo();

            
            
            current.setORBInitializing( true );

            
            preInitORBInitializers( orbInitInfo );

            
            postInitORBInitializers( orbInitInfo );

            
            interceptorList.sortInterceptors();

            
            
            current.setORBInitializing( false );

            
            orbInitInfo.setStage( ORBInitInfoImpl.STAGE_CLOSED );

            
            
            hasIORInterceptors = interceptorList.hasInterceptorsOfType(
                InterceptorList.INTERCEPTOR_TYPE_IOR );
            
            
            
            
            
            
            
                
            
            
                

            
            
            
            interceptorInvoker.setEnabled( true );
        }
    }

    
    @TraceInterceptor
    public void destroyInterceptors() {
        interceptorList.destroyAll();
    }

    @TraceInterceptor
    public void objectAdapterCreated( ObjectAdapter oa ) {
        if (!hasIORInterceptors)
            return ;

        interceptorInvoker.objectAdapterCreated( oa ) ;
    }

    @TraceInterceptor
    public void adapterManagerStateChanged( int managerId,
        short newState ) {

        if (!hasIORInterceptors)
            return ;

        interceptorInvoker.adapterManagerStateChanged( managerId, newState ) ;
    }

    @TraceInterceptor
    public void adapterStateChanged( ObjectReferenceTemplate[] 
        templates, short newState ) {

        if (!hasIORInterceptors)
            return ;

        interceptorInvoker.adapterStateChanged( templates, newState ) ;
    }

    

    @TraceInterceptor
    public void disableInterceptorsThisThread() {
        if( !hasClientInterceptors ) return;

        RequestInfoStack<ClientRequestInfoImpl> infoStack =
            threadLocalClientRequestInfoStack.get();
        infoStack.disableCount++;
    }
    
    @TraceInterceptor
    public void enableInterceptorsThisThread() {
        if( !hasClientInterceptors )
            return;

        RequestInfoStack<ClientRequestInfoImpl> infoStack =
            threadLocalClientRequestInfoStack.get();
        infoStack.disableCount--;
    }
    
    @TraceInterceptor
    public void invokeClientPIStartingPoint() 
        throws RemarshalException {

        if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        
        
        ClientRequestInfoImpl info = peekClientRequestInfoImplStack();
        interceptorInvoker.invokeClientInterceptorStartingPoint( info );

        
        
        short replyStatus = info.getReplyStatus();
        if( (replyStatus == SYSTEM_EXCEPTION.value) ||
            (replyStatus == LOCATION_FORWARD.value) ) {
            
            

            Exception exception = invokeClientPIEndingPoint(
                convertPIReplyStatusToReplyMessage( replyStatus ),
                info.getException() );
            if( exception == null ) {
                
                
            } if( exception instanceof SystemException ) {
                throw (SystemException)exception;
            } else if( exception instanceof RemarshalException ) {
                throw (RemarshalException)exception;
            } else if( (exception instanceof UserException) ||
                     (exception instanceof ApplicationException) ) {
                
                
                
                
                throw wrapper.exceptionInvalid() ;
            }
        } else if( replyStatus != ClientRequestInfoImpl.UNINITIALIZED ) {
            throw wrapper.replyStatusNotInit() ;
        }
    }

    
    
    public Exception makeCompletedClientRequest( int replyStatus,
        Exception exception ) {

        
        return handleClientPIEndingPoint( replyStatus, exception, false ) ;
    }

    public Exception invokeClientPIEndingPoint( int replyStatus,
        Exception exception ) {

        
        return handleClientPIEndingPoint( replyStatus, exception, true ) ;
    }

    @TraceInterceptor
    public Exception handleClientPIEndingPoint(
        int replyStatus, Exception exception, boolean invokeEndingPoint ) {
        if( !hasClientInterceptors ) return exception;
        if( !isClientPIEnabledForThisThread() ) return exception;

        
        
        
        short piReplyStatus = REPLY_MESSAGE_TO_PI_REPLY_STATUS[replyStatus];

        
        
        ClientRequestInfoImpl info = peekClientRequestInfoImplStack();
        info.setReplyStatus( piReplyStatus );
        info.setException( exception );

        if (invokeEndingPoint) {
            
            interceptorInvoker.invokeClientInterceptorEndingPoint( info );
            piReplyStatus = info.getReplyStatus();
        }

        
        if( (piReplyStatus == LOCATION_FORWARD.value) ||
            (piReplyStatus == TRANSPORT_RETRY.value) ) {
            
            
            info.reset();

            
            if (invokeEndingPoint) {
                info.setRetryRequest( RetryType.AFTER_RESPONSE ) ;
            } else {
                info.setRetryRequest( RetryType.BEFORE_RESPONSE ) ;
            }

            
            exception = new RemarshalException();
        } else if( (piReplyStatus == SYSTEM_EXCEPTION.value) ||
                 (piReplyStatus == USER_EXCEPTION.value) ) {

            exception = info.getException();
        }

        return exception;
    }

    @InfoMethod
    private void secondCallForADIIRequest() { }

    @InfoMethod
    private void normalCall() { }

    @InfoMethod
    private void clientInfoStackWasPushed() { }

    @TraceInterceptor
    public void initiateClientPIRequest( boolean diiRequest ) {
        if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        
        
        RequestInfoStack<ClientRequestInfoImpl> infoStack =
            threadLocalClientRequestInfoStack.get();
        ClientRequestInfoImpl info = null;

        if (!infoStack.empty() ) {
            info = infoStack.peek();
        }

        if (!diiRequest && (info != null) && info.isDIIInitiate() ) {
            
            
            secondCallForADIIRequest();
            info.setDIIInitiate( false );
        } else {
            
            
            normalCall();

            
            if( (info == null) || !info.getRetryRequest().isRetry() ) {
                info = new ClientRequestInfoImpl( orb );
                infoStack.push( info );
                clientInfoStackWasPushed();
                
            }

            
            
            
            info.setRetryRequest( RetryType.NONE );
            info.incrementEntryCount();

            
            
            
            
            info.setReplyStatus( RequestInfoImpl.UNINITIALIZED ) ;

            
            if( diiRequest ) {
                info.setDIIInitiate( true );
            }
        }
    }

    @InfoMethod
    private void clientInfoStackWasPopped() { }
    
    @TraceInterceptor
    public void cleanupClientPIRequest() {
        if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        ClientRequestInfoImpl info = peekClientRequestInfoImplStack();
        RetryType rt = info.getRetryRequest() ;

        
        if (!rt.equals( RetryType.BEFORE_RESPONSE )) {
            
            
            
            
            
            
            
            
            short replyStatus = info.getReplyStatus();
            if (replyStatus == ClientRequestInfoImpl.UNINITIALIZED ) {
                invokeClientPIEndingPoint( ReplyMessage.SYSTEM_EXCEPTION,
                    wrapper.unknownRequestInvoke() ) ;
            }
        }

        
        info.decrementEntryCount();

        
        if (info.getEntryCount() == 0 && !info.getRetryRequest().isRetry()) {
            RequestInfoStack<ClientRequestInfoImpl> infoStack =
                threadLocalClientRequestInfoStack.get();
            infoStack.pop();
            clientInfoStackWasPopped();
        }
    }

    @TraceInterceptor
    public void setClientPIInfo(MessageMediator messageMediator)
    {
        if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        peekClientRequestInfoImplStack().setInfo(messageMediator);
    }
    
    @TraceInterceptor
    public void setClientPIInfo( RequestImpl requestImpl ) {
        if( !hasClientInterceptors ) return;
        if( !isClientPIEnabledForThisThread() ) return;

        peekClientRequestInfoImplStack().setDIIRequest( requestImpl );
    }
    
    
    
    @TraceInterceptor
    public void invokeServerPIStartingPoint() {
        if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        interceptorInvoker.invokeServerInterceptorStartingPoint( info );

        
        serverPIHandleExceptions( info );
    }

    @TraceInterceptor
    public void invokeServerPIIntermediatePoint() {
        if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        interceptorInvoker.invokeServerInterceptorIntermediatePoint( info );

        
        
        info.releaseServant();

        
        serverPIHandleExceptions( info );
    }
    
    @TraceInterceptor
    public void invokeServerPIEndingPoint( ReplyMessage replyMessage ) {
        if( !hasServerInterceptors ) return;
        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();

        
        info.setReplyMessage( replyMessage );

        
        
        info.setCurrentExecutionPoint( 
            ServerRequestInfoImpl.EXECUTION_POINT_ENDING );

        
        
        
        if( !info.getAlreadyExecuted() ) {
            int replyStatus = replyMessage.getReplyStatus();

            
            
            
            
            short piReplyStatus =
                REPLY_MESSAGE_TO_PI_REPLY_STATUS[replyStatus];

            
            if( ( piReplyStatus == LOCATION_FORWARD.value ) ||
                ( piReplyStatus == TRANSPORT_RETRY.value ) )
            {
                info.setForwardRequest( replyMessage.getIOR() );
            }

            
            
            

            
            Exception prevException = info.getException();

            
            
            
            if( !info.isDynamic() &&
                (piReplyStatus == USER_EXCEPTION.value) )
            {
                info.setException( omgWrapper.unknownUserException() ) ;
            }

            
            info.setReplyStatus( piReplyStatus );
            interceptorInvoker.invokeServerInterceptorEndingPoint( info );
            short newPIReplyStatus = info.getReplyStatus();
            Exception newException = info.getException();

            
            
            
            if( ( newPIReplyStatus == SYSTEM_EXCEPTION.value ) &&
                ( newException != prevException ) )
            {
                throw (SystemException)newException;
            }

            
            if( newPIReplyStatus == LOCATION_FORWARD.value ) {
                if( piReplyStatus != LOCATION_FORWARD.value ) {
                    
                    IOR ior = info.getForwardRequestIOR();
                    throw new ForwardException( orb, ior ) ;
                }
                else if( info.isForwardRequestRaisedInEnding() ) {
                    
                    replyMessage.setIOR( info.getForwardRequestIOR() );
                }
            }
        }
    }
    
    @TraceInterceptor
    public void setServerPIInfo( Exception exception ) {
        if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setException( exception );
    }

    @TraceInterceptor
    public void setServerPIInfo( NVList arguments ) {
        if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setDSIArguments( arguments );
    }

    @TraceInterceptor
    public void setServerPIExceptionInfo( Any exception ) {
        if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setDSIException( exception );
    }

    @TraceInterceptor
    public void setServerPIInfo( Any result ) {
        if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setDSIResult( result );
    }

    @InfoMethod
    private void serverInfoStackWasPushed() { }

    @InfoMethod
    private void serverInfoStackWasPopped() { }

    @TraceInterceptor
    public void initializeServerPIInfo( MessageMediator request,
        ObjectAdapter oa, byte[] objectId, ObjectKeyTemplate oktemp ) {

        if( !hasServerInterceptors ) return;

        RequestInfoStack<ServerRequestInfoImpl> infoStack =
            threadLocalServerRequestInfoStack.get();
        ServerRequestInfoImpl info = new ServerRequestInfoImpl( orb );
        infoStack.push( info );
        serverInfoStackWasPushed();

        
        
        request.setExecutePIInResponseConstructor( true );

        info.setInfo( request, oa, objectId, oktemp );
    }
    
    @TraceInterceptor
    public void setServerPIInfo( java.lang.Object servant, 
        String targetMostDerivedInterface ) {
        if( !hasServerInterceptors ) return;

        ServerRequestInfoImpl info = peekServerRequestInfoImplStack();
        info.setInfo( servant, targetMostDerivedInterface );
    }

    @TraceInterceptor
    public void cleanupServerPIRequest() {
        if( !hasServerInterceptors ) return;

        RequestInfoStack<ServerRequestInfoImpl> infoStack =
            threadLocalServerRequestInfoStack.get();
        infoStack.pop();

        serverInfoStackWasPopped();
    }
    
    

    
    @TraceInterceptor
    private void serverPIHandleExceptions( ServerRequestInfoImpl info ) {
        int endingPointCall = info.getEndingPointCall();
        if(endingPointCall == ServerRequestInfoImpl.CALL_SEND_EXCEPTION) {
            
            throw (SystemException)info.getException();
        }
        else if( (endingPointCall == ServerRequestInfoImpl.CALL_SEND_OTHER) &&
                 (info.getForwardRequestException() != null) )
        {
            
            
            IOR ior = info.getForwardRequestIOR();
            throw new ForwardException( orb, ior );
        }
    }

    
    @TraceInterceptor
    private int convertPIReplyStatusToReplyMessage( short replyStatus ) {
        int result = 0;
        for( int i = 0; i < REPLY_MESSAGE_TO_PI_REPLY_STATUS.length; i++ ) {
            if( REPLY_MESSAGE_TO_PI_REPLY_STATUS[i] == replyStatus ) {
                result = i;
                break;
            }
        }
        return result;
    }
    
    
    @TraceInterceptor
    private ClientRequestInfoImpl peekClientRequestInfoImplStack() {
        RequestInfoStack<ClientRequestInfoImpl> infoStack =
            threadLocalClientRequestInfoStack.get();
        ClientRequestInfoImpl info = null;
        if( !infoStack.empty() ) {
            info = infoStack.peek();
        } else {
            throw wrapper.clientInfoStackNull() ;
        }

        return info;
    }

    
    @TraceInterceptor
    private ServerRequestInfoImpl peekServerRequestInfoImplStack() {
        RequestInfoStack<ServerRequestInfoImpl> infoStack =
            threadLocalServerRequestInfoStack.get();
        ServerRequestInfoImpl info = null;

        if( !infoStack.empty() ) {
            info = infoStack.peek();
        } else {
            throw wrapper.serverInfoStackNull() ;
        }

        return info;
    }
    
    
    @TraceInterceptor
    private boolean isClientPIEnabledForThisThread() {
        RequestInfoStack<ClientRequestInfoImpl> infoStack =
            threadLocalClientRequestInfoStack.get();
        return (infoStack.disableCount == 0);
    }
    
    
    @TraceInterceptor
    private void preInitORBInitializers( ORBInitInfoImpl info ) {
        
        info.setStage( ORBInitInfoImpl.STAGE_PRE_INIT );

        
        
        for( int i = 0; i < orb.getORBData().getORBInitializers().length;
            i++ ) {
            ORBInitializer init = orb.getORBData().getORBInitializers()[i];
            if( init != null ) {
                try {
                    init.pre_init( info );
                }
                catch( Exception e ) {
                    
                    
                }
            }
        }
    }

    
    @TraceInterceptor
    private void postInitORBInitializers( ORBInitInfoImpl info ) {
        
        info.setStage( ORBInitInfoImpl.STAGE_POST_INIT );

        
        
        for( int i = 0; i < orb.getORBData().getORBInitializers().length;
            i++ ) {
            ORBInitializer init = orb.getORBData().getORBInitializers()[i];
            if( init != null ) {
                try {
                    init.post_init( info );
                }
                catch( Exception e ) {
                    
                    
                }
            }
        }
    }

    
    @TraceInterceptor
    private ORBInitInfoImpl createORBInitInfo() {
        ORBInitInfoImpl result = null;

        

        
        
        
        String orbId = orb.getORBData().getORBId() ;

        result = new ORBInitInfoImpl( orb, arguments, orbId, codecFactory );

        return result;
    }

    
    @TraceInterceptor
    public void register_interceptor( Interceptor interceptor, int type ) 
        throws DuplicateName {
        
        
        if( (type >= InterceptorList.NUM_INTERCEPTOR_TYPES) || (type < 0) ) {
            throw wrapper.typeOutOfRange( type ) ;
        }

        String interceptorName = interceptor.name();

        if( interceptorName == null ) {
            throw wrapper.nameNull() ;
        }

        
        interceptorList.register_interceptor( interceptor, type );
    }

    public Current getPICurrent( ) {
        return current;
    }

    
    private void nullParam() 
        throws BAD_PARAM 
    {
        throw orbutilWrapper.nullParam() ;
    }

    
    @TraceInterceptor
    public org.omg.CORBA.Policy create_policy(int type, org.omg.CORBA.Any val)
        throws org.omg.CORBA.PolicyError {
        if( val == null ) {
            nullParam( );
        }

        if( policyFactoryTable == null ) {
            throw new org.omg.CORBA.PolicyError(
                "There is no PolicyFactory Registered for type " + type,
                BAD_POLICY.value );
        }

        PolicyFactory factory = policyFactoryTable.get( type );
        if( factory == null ) {
            throw new org.omg.CORBA.PolicyError(
                " Could Not Find PolicyFactory for the Type " + type,
                BAD_POLICY.value);
        }

        org.omg.CORBA.Policy policy = factory.create_policy( type, val );
        return policy;
    }

    
    @TraceInterceptor
    public void registerPolicyFactory( int type, PolicyFactory factory ) {
        if( policyFactoryTable == null ) {
            policyFactoryTable = new HashMap<Integer,PolicyFactory>();
        }
        Integer key = Integer.valueOf( type );
        PolicyFactory val = policyFactoryTable.get( key );
        if (val == null) {
            policyFactoryTable.put( key, factory );
        } else {
            throw omgWrapper.policyFactoryRegFailed( type ) ;
        }
    }
    
    public synchronized int allocateServerRequestId ()
    {
        return serverRequestIdCounter++;
    }
}
