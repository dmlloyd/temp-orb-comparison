


package com.sun.corba.ee.impl.interceptors;

import java.util.Map ;
import java.util.HashMap ;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.ApplicationException;
import com.sun.corba.ee.spi.servicecontext.ServiceContexts;

import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedComponent;
import org.omg.Dynamic.Parameter;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.TRANSPORT_RETRY;
import org.omg.PortableInterceptor.USER_EXCEPTION;


import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.protocol.MessageMediator;

import com.sun.corba.ee.spi.protocol.RetryType;
import com.sun.corba.ee.spi.transport.ContactInfo;
import com.sun.corba.ee.spi.transport.ContactInfoList;
import com.sun.corba.ee.spi.transport.ContactInfoListIterator;

import com.sun.corba.ee.impl.misc.ORBUtility;
import com.sun.corba.ee.impl.protocol.InvocationInfo;
import com.sun.corba.ee.spi.trace.TraceInterceptor;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;


@TraceInterceptor
public final class ClientRequestInfoImpl 
    extends RequestInfoImpl 
    implements ClientRequestInfo 
{

    
    static final int CALL_SEND_REQUEST = 0;
    static final int CALL_SEND_POLL = 1;
    
    
    static final int CALL_RECEIVE_REPLY = 0;
    static final int CALL_RECEIVE_EXCEPTION = 1;
    static final int CALL_RECEIVE_OTHER = 2;

    
    
    
    
    
    
    
    
    private RetryType retryRequest;
    
    
    
    
    
    private int entryCount = 0;

    
    
    
    private org.omg.CORBA.Request request;

    
    private boolean diiInitiate;
    private MessageMediator messageMediator;

    
    private org.omg.CORBA.Object cachedTargetObject;
    private org.omg.CORBA.Object cachedEffectiveTargetObject;
    private Parameter[] cachedArguments;
    private TypeCode[] cachedExceptions;
    private String[] cachedContexts;
    private String[] cachedOperationContext;
    private String cachedReceivedExceptionId;
    private Any cachedResult;
    private Any cachedReceivedException;
    private TaggedProfile cachedEffectiveProfile;
    
    private Map<Integer, org.omg.IOP.ServiceContext> cachedRequestServiceContexts;
    private Map<Integer,org.omg.IOP.ServiceContext> cachedReplyServiceContexts;
    private Map<Integer,TaggedComponent[]> cachedEffectiveComponents;


    private boolean piCurrentPushed;
    
    
    
    
    
    
    
    
    @TraceInterceptor
    @Override
    void reset() {
        super.reset();

        

        
        retryRequest = RetryType.NONE;

        
        

        request = null;
        diiInitiate = false;
        messageMediator = null;

        
        cachedTargetObject = null;
        cachedEffectiveTargetObject = null;
        cachedArguments = null;
        cachedExceptions = null;
        cachedContexts = null;
        cachedOperationContext = null;
        cachedReceivedExceptionId = null;
        cachedResult = null;
        cachedReceivedException = null;
        cachedEffectiveProfile = null;
        cachedRequestServiceContexts = null;
        cachedReplyServiceContexts = null;
        cachedEffectiveComponents = null;

        piCurrentPushed = false;

        startingPointCall = CALL_SEND_REQUEST;
        endingPointCall = CALL_RECEIVE_REPLY;
    }
    
    
    
    
    
    private static final int MID_TARGET                      = MID_RI_LAST + 1;
    private static final int MID_EFFECTIVE_TARGET            = MID_RI_LAST + 2;
    private static final int MID_EFFECTIVE_PROFILE           = MID_RI_LAST + 3;
    private static final int MID_RECEIVED_EXCEPTION          = MID_RI_LAST + 4;
    private static final int MID_RECEIVED_EXCEPTION_ID       = MID_RI_LAST + 5;
    private static final int MID_GET_EFFECTIVE_COMPONENT     = MID_RI_LAST + 6;
    private static final int MID_GET_EFFECTIVE_COMPONENTS    = MID_RI_LAST + 7;
    private static final int MID_GET_REQUEST_POLICY          = MID_RI_LAST + 8;
    private static final int MID_ADD_REQUEST_SERVICE_CONTEXT = MID_RI_LAST + 9;
    
    
    
    private static final boolean validCall[][] = {
        
        
        
        
        
        
        
        
        
        
        
        
        
        
                          { true , true , true , true , true  },
                           { true , true , true , true , true  },
                           { true , false, true , false, false },
                          { true , false, true , true , true  },
                            { true , false, true , true , true  },
                   { true , false, true , true , true  },
                              { false, false, true , false, false },
                   { true , true , true , true , true  },
                          { true , false, true , true , true  },
                        { false, false, true , true , true  },
                   { false, false, false, false, true  },
                            { true , true , true , true , true  },
         { true , false, true , true , true  },
           { false, false, true , true , true  },
        
        
                              { true , true , true , true , true  },
                    { true , true , true , true , true  },
                   { true , true , true , true , true  },
                  { false, false, false, true , false },
               { false, false, false, true , false },
             { true , false, true , true , true  },
            { true , false, true , true , true  },
                  { true , false, true , true , true  },
         { true , false, false, false, false }
    };
    

    
    
    
    protected ClientRequestInfoImpl( ORB myORB ) { 
        super( myORB ); 
        startingPointCall = CALL_SEND_REQUEST;
        endingPointCall = CALL_RECEIVE_REPLY;
    }
    
    
    @TraceInterceptor
    public org.omg.CORBA.Object target (){
        
        
        if (messageMediator != null && cachedTargetObject == null) {
            ContactInfo corbaContactInfo = (ContactInfo)
                messageMediator.getContactInfo();
            cachedTargetObject =
                iorToObject(corbaContactInfo.getTargetIOR());
        }
        return cachedTargetObject;
    }
    
    
    @TraceInterceptor
    public org.omg.CORBA.Object effective_target() {
        
        

        
        
        

        if (messageMediator != null && cachedEffectiveTargetObject == null) {
            ContactInfo corbaContactInfo = messageMediator.getContactInfo();
            
            cachedEffectiveTargetObject =
                iorToObject(corbaContactInfo.getEffectiveTargetIOR());
        }
        return cachedEffectiveTargetObject;
    }
    
    
    @TraceInterceptor
    public TaggedProfile effective_profile (){
        
        

        if(messageMediator != null && cachedEffectiveProfile == null ) {
            ContactInfo corbaContactInfo = messageMediator.getContactInfo();
            cachedEffectiveProfile =
                corbaContactInfo.getEffectiveProfile().getIOPProfile();
        }

        
        
        

        return cachedEffectiveProfile;
    }
    
    
    @TraceInterceptor
    public Any received_exception (){
        checkAccess( MID_RECEIVED_EXCEPTION );

        if( cachedReceivedException == null ) {
            cachedReceivedException = exceptionToAny( exception );
        }

        
        
        

        return cachedReceivedException;
    }
    
    
    @TraceInterceptor
    public String received_exception_id (){
        checkAccess( MID_RECEIVED_EXCEPTION_ID );

        if( cachedReceivedExceptionId == null ) {
            String result = null;

            if( exception == null ) {
                
                
                
                throw wrapper.exceptionWasNull() ;
            } else if( exception instanceof SystemException ) {
                String name = exception.getClass().getName();
                result = ORBUtility.repositoryIdOf(name);
            } else if( exception instanceof ApplicationException ) {
                result = ((ApplicationException)exception).getId();
            }

            
            

            cachedReceivedExceptionId = result;
        }

        return cachedReceivedExceptionId;
    }
    
    
    @TraceInterceptor
    public TaggedComponent get_effective_component (int id){
        checkAccess( MID_GET_EFFECTIVE_COMPONENT );
            
        TaggedComponent[] comps = get_effective_components( id ) ;
        if ((comps != null) && (comps.length > 0)) {
            return comps[0];
        } else {
            return null ;
        }
    }
    
    
    @TraceInterceptor
    public TaggedComponent[] get_effective_components (int id){
        checkAccess( MID_GET_EFFECTIVE_COMPONENTS );
        TaggedComponent[] result = null;
        boolean justCreatedCache = false;

        if( cachedEffectiveComponents == null ) {
            cachedEffectiveComponents = new HashMap<Integer,TaggedComponent[]>();
            justCreatedCache = true;
        } else {
            
            result = cachedEffectiveComponents.get( id );
        }

        
        if( (messageMediator != null) && (result == null) &&
            (justCreatedCache ||
            !cachedEffectiveComponents.containsKey( id ) ) )
        {
            
            ContactInfo corbaContactInfo = messageMediator.getContactInfo();
            IIOPProfileTemplate ptemp =
                (IIOPProfileTemplate)corbaContactInfo.getEffectiveProfile().
                getTaggedProfileTemplate();
            result = ptemp.getIOPComponents(myORB, id);
            cachedEffectiveComponents.put( id, result );
        }

        
        
        if( (result == null) || (result.length == 0) ) {
            if (!myORB.getORBData().isAppServerMode()) {
                
                throw stdWrapper.invalidComponentId( id ) ;
            }
        }

        
        
        
        

        return result;
    }
    
    
    @TraceInterceptor
    public Policy get_request_policy (int type){
        checkAccess( MID_GET_REQUEST_POLICY );
        
        throw wrapper.piOrbNotPolicyBased() ;
    }
    
    
    @TraceInterceptor
    public void add_request_service_context (ServiceContext service_context, 
                                             boolean replace) {

        checkAccess( MID_ADD_REQUEST_SERVICE_CONTEXT );

        if( cachedRequestServiceContexts == null ) {
            cachedRequestServiceContexts =
                new HashMap<Integer,org.omg.IOP.ServiceContext>();
        }

        addServiceContext( cachedRequestServiceContexts,
                           messageMediator.getRequestServiceContexts(),
                           service_context, replace );
    }
    
    
    
    
    

    
   
    
    public int request_id (){
        
        
        
        return messageMediator.getRequestId();
    }

    
    public String operation(){
        
        
        if (messageMediator != null) {
            return messageMediator.getOperationName();
        } else {
            return "<special operation>";
        }
    }

    @Override
    public String toString() {
        return "ClientRequestInfoImpl[operation=" 
            + operation() + "]" ;
    }

    
    @TraceInterceptor
    public Parameter[] arguments (){
        checkAccess( MID_ARGUMENTS );

        if( cachedArguments == null ) {
            if( request == null ) {
                throw stdWrapper.piOperationNotSupported1() ;
            }

            
            
            cachedArguments = nvListToParameterArray( request.arguments() );
        }

        
        
        
        

        return cachedArguments;
    }

    
    @TraceInterceptor
    public TypeCode[] exceptions (){
        checkAccess( MID_EXCEPTIONS );

        if( cachedExceptions == null ) {
            if( request == null ) {
               throw stdWrapper.piOperationNotSupported2() ;
            }

            
            
            ExceptionList excList = request.exceptions( );
            int count = excList.count();
            TypeCode[] excTCList = new TypeCode[count];
            try {
                for( int i = 0; i < count; i++ ) {
                    excTCList[i] = excList.item( i );
                }
            } catch( Exception e ) {
                throw wrapper.exceptionInExceptions( e ) ;
            }

            cachedExceptions = excTCList;
        }

        
        
        
        

        return cachedExceptions;
    }

    
    @TraceInterceptor
    public String[] contexts (){
        checkAccess( MID_CONTEXTS );

        if( cachedContexts == null ) {
            if( request == null ) {
                throw stdWrapper.piOperationNotSupported3() ;
            }

            
            
            ContextList ctxList = request.contexts( );
            int count = ctxList.count();
            String[] ctxListToReturn = new String[count];
            try {
                for( int i = 0; i < count; i++ ) {
                    ctxListToReturn[i] = ctxList.item( i );
                }
            } catch( Exception e ) {
                throw wrapper.exceptionInContexts( e ) ;
            }

            cachedContexts = ctxListToReturn;
        }

        
        
        

        return cachedContexts;
    }

    
    @TraceInterceptor
    public String[] operation_context (){
        checkAccess( MID_OPERATION_CONTEXT );

        if( cachedOperationContext == null ) {
            if( request == null ) {
                throw stdWrapper.piOperationNotSupported4() ;
            }

            
            
            Context ctx = request.ctx( );
            
            
            
            
            
            
            NVList nvList = ctx.get_values( "", CTX_RESTRICT_SCOPE.value,"*" );
            String[] context = new String[(nvList.count() * 2) ];
            if( nvList.count() != 0 ) {
                
                
                int index = 0;
                for( int i = 0; i < nvList.count(); i++ ) {
                    NamedValue nv;
                    try {
                        nv = nvList.item( i );
                    }
                    catch (Exception e ) {
                        return (String[]) null;
                    }
                    context[index] = nv.name();
                    index++;
                    context[index] = nv.value().extract_string();
                    index++;
                }
            }

            cachedOperationContext = context;
        }

        
        
        

        return cachedOperationContext;
    }

    
    @TraceInterceptor
    public Any result (){
        checkAccess( MID_RESULT );

        if( cachedResult == null ) {
            if( request == null ) {
                throw stdWrapper.piOperationNotSupported5() ;
            }
            
            NamedValue nvResult = request.result( );

            if( nvResult == null ) {
                throw wrapper.piDiiResultIsNull() ;
            }

            cachedResult = nvResult.value();
        }

        
        
        

        return cachedResult;
    }

    
    public boolean response_expected (){
        
        
        return ! messageMediator.isOneWay();
    }

    
    @TraceInterceptor
    public Object forward_reference (){
        checkAccess( MID_FORWARD_REFERENCE );
        
        
        
        if( replyStatus != LOCATION_FORWARD.value ) {
            throw stdWrapper.invalidPiCall1() ;
        }

        
        
        
        IOR ior = getLocatedIOR();
        return iorToObject(ior);
    }

    @TraceInterceptor
    private IOR getLocatedIOR() {
        IOR ior;
        ContactInfoList contactInfoList = messageMediator.getContactInfo().
            getContactInfoList();
        ior = contactInfoList.getEffectiveTargetIOR();
        return ior;
    }

    
    @TraceInterceptor
    public void setLocatedIOR(IOR ior) {
        ORB orb = messageMediator.getBroker();

        ContactInfoListIterator iterator = (ContactInfoListIterator)
            ((InvocationInfo)orb.getInvocationInfo())
            .getContactInfoListIterator();

        
        
        iterator.reportRedirect( messageMediator.getContactInfo(), ior);
    }

    
    @TraceInterceptor
    public org.omg.IOP.ServiceContext get_request_service_context( int id ) {
        checkAccess( MID_GET_REQUEST_SERVICE_CONTEXT );

        if( cachedRequestServiceContexts == null ) {
            cachedRequestServiceContexts =
                new HashMap<Integer,org.omg.IOP.ServiceContext>();
        }

        return  getServiceContext(cachedRequestServiceContexts,
                                  messageMediator.getRequestServiceContexts(),
                                  id);
    }

    
    @TraceInterceptor
    public org.omg.IOP.ServiceContext get_reply_service_context( int id ) {
        checkAccess( MID_GET_REPLY_SERVICE_CONTEXT );

        if( cachedReplyServiceContexts == null ) {
            cachedReplyServiceContexts =
                new HashMap<Integer,org.omg.IOP.ServiceContext>();
        }

        
        
        
        
        
        
        
        

        
        if (messageMediator.getReplyHeader() != null) {
            ServiceContexts sctxs =
                messageMediator.getReplyServiceContexts();
            if (sctxs != null) {
                return getServiceContext(cachedReplyServiceContexts,
                                         sctxs, id);
            }
        }
        
        
        
        throw stdWrapper.invalidServiceContextId() ;
    }

    
    
    
    

    @Override
    public com.sun.corba.ee.spi.legacy.connection.Connection connection()
    {
        return (com.sun.corba.ee.spi.legacy.connection.Connection) 
            messageMediator.getConnection();
    }
    


    

    protected void setInfo(MessageMediator messageMediator)
    {
        this.messageMediator = messageMediator;
        
        this.messageMediator.setDIIInfo(request);
    }
    
    
    void setRetryRequest( RetryType retryRequest ) {
        
        this.retryRequest = retryRequest;
    }
    
    
    RetryType getRetryRequest() {
        
        return this.retryRequest;
    }
    
    
    @TraceInterceptor
    void incrementEntryCount() {
        this.entryCount++;
        entryCount( this.entryCount ) ;
    }

    @InfoMethod
    private void entryCount( int count ) { }
    
    
    @TraceInterceptor
    void decrementEntryCount() {
        this.entryCount--;
        entryCount( this.entryCount ) ;
    }
    
    
    @TraceInterceptor
    int getEntryCount() {
        return this.entryCount;
    }
    
    
    @TraceInterceptor
    @Override
    protected void setReplyStatus( short replyStatus ) {
        super.setReplyStatus( replyStatus );
        switch( replyStatus ) {
            case SUCCESSFUL.value:
                endingPointCall = CALL_RECEIVE_REPLY;
                break;
            case SYSTEM_EXCEPTION.value:
            case USER_EXCEPTION.value:
                endingPointCall = CALL_RECEIVE_EXCEPTION;
                break;
            case LOCATION_FORWARD.value:
            case TRANSPORT_RETRY.value:
                endingPointCall = CALL_RECEIVE_OTHER;
                break;
        }
    }

    @TraceInterceptor
    @Override
    protected short getReplyStatus() {
        return super.getReplyStatus() ;
    }

    
    protected void setDIIRequest(org.omg.CORBA.Request req) {
         request = req;
    }

    
    protected void setDIIInitiate( boolean diiInitiate ) {
        this.diiInitiate = diiInitiate;
    }

    
    protected boolean isDIIInitiate() {
        return this.diiInitiate;
    }

    
    protected void setPICurrentPushed( boolean piCurrentPushed ) {
        this.piCurrentPushed = piCurrentPushed;
    }

    protected boolean isPICurrentPushed() {
        return this.piCurrentPushed;
    }

    
    @Override
    protected void setException( Exception exception ) {
        super.setException( exception );

        
        cachedReceivedException = null;
        cachedReceivedExceptionId = null;
    }

    protected boolean getIsOneWay() {
        return ! response_expected();
    }

    
    protected void checkAccess( int methodID ) 
        throws BAD_INV_ORDER 
    {
        
        
        int validCallIndex = 0;
        switch( currentExecutionPoint ) {
        case EXECUTION_POINT_STARTING:
            switch( startingPointCall ) {
            case CALL_SEND_REQUEST:
                validCallIndex = 0;
                break;
            case CALL_SEND_POLL:
                validCallIndex = 1;
                break;
            }
            break;
        case EXECUTION_POINT_ENDING:
            switch( endingPointCall ) {
            case CALL_RECEIVE_REPLY:
                validCallIndex = 2;
                break;
            case CALL_RECEIVE_EXCEPTION:
                validCallIndex = 3;
                break;
            case CALL_RECEIVE_OTHER:
                validCallIndex = 4;
                break;
            }
            break;
        }
        
        
        if( !validCall[methodID][validCallIndex] ) {
            throw stdWrapper.invalidPiCall2() ;
        }
    }
    
}


