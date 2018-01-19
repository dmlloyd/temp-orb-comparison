




package com.sun.corba.ee.impl.protocol;

import com.sun.corba.ee.impl.encoding.CDRInputObject;
import com.sun.corba.ee.impl.encoding.CDROutputObject;
import com.sun.corba.ee.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.ee.impl.encoding.CodeSetConversion;
import com.sun.corba.ee.impl.encoding.EncapsInputStream;
import com.sun.corba.ee.impl.misc.ORBUtility;
import com.sun.corba.ee.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.spi.misc.ORBConstants;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.orb.ORBVersion;
import com.sun.corba.ee.spi.protocol.ClientRequestDispatcher;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import com.sun.corba.ee.spi.servicecontext.*;
import com.sun.corba.ee.spi.trace.Subcontract;
import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.transport.ContactInfo;
import com.sun.corba.ee.spi.transport.ContactInfoListIterator;
import com.sun.corba.ee.spi.transport.OutboundConnectionCache;
import org.glassfish.pfl.tf.spi.TimingPointType;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.ExceptionDetailMessage;
import org.omg.IOP.TAG_CODE_SETS;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Subcontract
public class ClientRequestDispatcherImpl
    implements
        ClientRequestDispatcher
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    
    private final Object lock = new Object();

    private ORBVersionServiceContext ovsc = 
                   ServiceContextDefaults.makeORBVersionServiceContext();


    private MaxStreamFormatVersionServiceContext msfvc = 
                            ServiceContextDefaults.getMaxStreamFormatVersionServiceContext();

    private ConcurrentMap<ContactInfo,Object> locks =
        new ConcurrentHashMap<ContactInfo,Object>() ;

    @InfoMethod
    private void usingCachedConnection( Connection conn ) { }

    @InfoMethod
    private void usingCreatedConnection( Connection conn ) { }

    @InfoMethod
    private void connectionCached( Connection conn ) { }

    @InfoMethod
    private void connectionRegistered( Connection conn ) { }

    @InfoMethod
    private void createdMessageMediator( MessageMediator med ) { }

    @InfoMethod
    private void createOutputObject( CDROutputObject out ) { }

    @InfoMethod
    private void generalMessage ( String msg ) { }

    @InfoMethod
    private void remarshalWithHasNextTrue( ContactInfo info ) { }

    @InfoMethod( tpName="totalRequest", tpType=TimingPointType.ENTER )
    private void enter_totalRequest() { }

    @InfoMethod( tpName="totalRequest", tpType=TimingPointType.EXIT )
    private void exit_totalRequest() { }

    @InfoMethod( tpName="connectionSetup", tpType=TimingPointType.ENTER )
    private void enter_connectionSetup() { }

    @InfoMethod( tpName="connectionSetup", tpType=TimingPointType.EXIT )
    private void exit_connectionSetup() { }

    @InfoMethod( tpName="clientDecoding", tpType=TimingPointType.ENTER )
    private void enter_clientDecoding() { }

    @InfoMethod( tpName="clientDecoding", tpType=TimingPointType.EXIT )
    private void exit_clientDecoding() { }

    @InfoMethod( tpName="clientEncoding", tpType=TimingPointType.ENTER )
    private void enter_clientEncoding() { }

    @InfoMethod( tpName="clientEncoding", tpType=TimingPointType.EXIT )
    private void exit_clientEncoding() { }

    @InfoMethod( tpName="clientTransportAndWait", tpType=TimingPointType.ENTER )
    private void enter_clientTransportAndWait() { }

    @InfoMethod( tpName="clientTransportAndWait", tpType=TimingPointType.EXIT )
    private void exit_clientTransportAndWait() { }

    @InfoMethod( tpName="processResponse", tpType=TimingPointType.ENTER )
    private void enter_processResponse() { }

    @InfoMethod( tpName="processResponse", tpType=TimingPointType.EXIT )
    private void exit_processResponse() { }

    @InfoMethod( tpName="requestAddServiceContexts", tpType=TimingPointType.ENTER )
    private void enter_requestAddServiceContexts() { }

    @InfoMethod( tpName="requestAddServiceContexts", tpType=TimingPointType.EXIT )
    private void exit_requestAddServiceContexts() { }

    @Subcontract
    public CDROutputObject beginRequest(Object self, String opName,
        boolean isOneWay, ContactInfo contactInfo) {

        final ORB orb = contactInfo.getBroker();

        enter_totalRequest() ;

        
        orb.getPIHandler().initiateClientPIRequest( false );

        Connection connection = null;

        Object lock = locks.get( contactInfo ) ;

        if (lock == null) {
            Object newLock = new Object() ;
            lock = locks.putIfAbsent( contactInfo, newLock ) ;
            if (lock == null) {
                lock = newLock ;
            }
        }

        
        
        
        
        
        
        
        
        
        synchronized (lock) {
            if (contactInfo.isConnectionBased()) {
                try {
                    enter_connectionSetup();

                    if (contactInfo.shouldCacheConnection()) {
                        connection = orb.getTransportManager()
                            .getOutboundConnectionCache(contactInfo)
                            .get(contactInfo);
                    }

                    if (connection != null) {
                        usingCachedConnection( connection ) ;
                    } else {
                        connection =
                            contactInfo.createConnection();
                        usingCreatedConnection( connection ) ;

                        if (connection.shouldRegisterReadEvent()) {
                            orb.getTransportManager().getSelector(0)
                                .registerForEvent(connection.getEventHandler());
                            connection.setState("ESTABLISHED");
                            connectionRegistered( connection ) ;
                        }

                        
                        
                        
                        
                        if (contactInfo.shouldCacheConnection()) {
                            OutboundConnectionCache connectionCache =
                                orb.getTransportManager()
                                        .getOutboundConnectionCache(contactInfo);
                            connectionCache.stampTime(connection);
                            connectionCache.put(contactInfo, connection);
                            connectionCached( connection ) ;
                        }
                    }
                } finally {
                    exit_connectionSetup();
                }
            }
        }

        MessageMediator messageMediator =
            contactInfo.createMessageMediator(orb, contactInfo, connection,
                opName, isOneWay);
        createdMessageMediator(messageMediator);

        
        
        
        
        
        
        
        
        
        orb.getInvocationInfo().setMessageMediator(messageMediator);

        performCodeSetNegotiation(messageMediator);

        enter_requestAddServiceContexts() ;
        try {
            addServiceContexts(messageMediator);
        } finally {
            exit_requestAddServiceContexts() ;
        }

        CDROutputObject outputObject = contactInfo.createOutputObject(messageMediator);

        createOutputObject(outputObject);

        
        
        
        registerWaiter(messageMediator);

        
        synchronized (lock) {
            if (contactInfo.isConnectionBased()) {
                if (contactInfo.shouldCacheConnection()) {
                    generalMessage( "reclaiming connections" );
                    OutboundConnectionCache connectionCache = orb.getTransportManager()
                        .getOutboundConnectionCache(contactInfo);
                    connectionCache.reclaim();
                }
            }
        }

        orb.getPIHandler().setClientPIInfo(messageMediator);
        try {
            
            
            
            orb.getPIHandler().invokeClientPIStartingPoint();
        } catch( RemarshalException e ) {
            generalMessage( "Remarshal" ) ;

            
            
            

            
            
            
            

            
            
            
            
            if (getContactInfoListIterator(orb).hasNext()) {
                contactInfo = getContactInfoListIterator(orb).next();
                remarshalWithHasNextTrue(contactInfo);

                
                orb.getPIHandler().makeCompletedClientRequest(
                    ReplyMessage.LOCATION_FORWARD, null ) ;
                unregisterWaiter( orb ) ;
                orb.getPIHandler().cleanupClientPIRequest() ;

                return beginRequest(self, opName, isOneWay, contactInfo);
            } else {
                retryMessage( "RemarshalException: hasNext false" ) ;
                throw wrapper.remarshalWithNowhereToGo();
            }
        }

        messageMediator.initializeMessage();
        generalMessage( "initialized message");

        enter_clientEncoding();

        return outputObject;
    }

    @InfoMethod
    private void operationAndId( String op, int rid ) { }

    @Subcontract
    public CDRInputObject marshalingComplete(java.lang.Object self,
                                          CDROutputObject outputObject)
        throws 
            ApplicationException, 
            org.omg.CORBA.portable.RemarshalException
    {
        MessageMediator messageMediator = outputObject.getMessageMediator();
        ORB orb = messageMediator.getBroker();
        operationAndId(messageMediator.getOperationName(), 
            messageMediator.getRequestId() );

        try {
            exit_clientEncoding();

            enter_clientTransportAndWait();

            CDRInputObject inputObject = null ;
            try {
                inputObject = marshalingComplete1(orb, messageMediator);
            } finally {
                exit_clientTransportAndWait();
            }

            return processResponse(orb, messageMediator, inputObject);
        } finally {
            
            
            enter_clientDecoding() ;
        }
    }

    @InfoMethod
    private void retryMessage( String msg ) { }

    @InfoMethod
    private void reportException( Throwable exc ) { }

    @InfoMethod
    private void reportException( String msg, Throwable exc ) { }

    @Subcontract
    public CDRInputObject marshalingComplete1(
            ORB orb, MessageMediator messageMediator)
        throws
            ApplicationException,
            org.omg.CORBA.portable.RemarshalException
    {
        operationAndId(messageMediator.getOperationName(),
            messageMediator.getRequestId() );

        try {
            messageMediator.finishSendingRequest();

            
            
            

            return messageMediator.waitForResponse();
        } catch (RuntimeException e) {
            reportException( e ) ;

            boolean retry  =
                getContactInfoListIterator(orb)
                    .reportException(messageMediator.getContactInfo(), e);

            
            
            Exception newException =
                orb.getPIHandler().invokeClientPIEndingPoint(
                    ReplyMessage.SYSTEM_EXCEPTION, e);

            if (retry) {
                if (newException == e) {
                    retryMessage( "Retry true; same exception" ) ;
                    continueOrThrowSystemOrRemarshal(messageMediator,
                                                     new RemarshalException());
                } else {
                    retryMessage( "Retry true; new exception" ) ;
                    continueOrThrowSystemOrRemarshal(messageMediator,
                                                     newException);
                }
            } else {
                
                if (newException instanceof RuntimeException) {
                    retryMessage( "Retry false; RuntimeException" ) ;
                    throw (RuntimeException)newException ;
                } else if (newException instanceof RemarshalException) {
                     throw (RemarshalException) newException;
                } else {
                    retryMessage( "Retry false; other exception" ) ;
                    throw e ;
                }
            }

            return null; 
        }
    }

    @InfoMethod
    private void receivedUserException( String repoid ) { }

    @InfoMethod
    private void receivedUserExceptionDII( Throwable exc, Throwable newExc ) { }

    @InfoMethod
    private void receivedUserExceptionNotDII( Throwable exc, Throwable newExc ) { }

    @Subcontract
    protected CDRInputObject processResponse(ORB orb, 
        MessageMediator messageMediator, CDRInputObject inputObject)
        throws ApplicationException, org.omg.CORBA.portable.RemarshalException {

        operationAndId(messageMediator.getOperationName(),
            messageMediator.getRequestId() );

        enter_processResponse() ;
        try {
            
            
            if (messageMediator.getConnection() != null) {
                generalMessage( "Non-null connection" ) ;
                messageMediator.getConnection().setPostInitialContexts();
            }

            
            

            

            Exception exception = null;

            if (messageMediator.isOneWay()) {
                generalMessage( "One way request" ) ;
                getContactInfoListIterator(orb)
                    .reportSuccess(messageMediator.getContactInfo());
                
                exception = orb.getPIHandler().invokeClientPIEndingPoint(
                    ReplyMessage.NO_EXCEPTION, exception );
                reportException(exception);
                continueOrThrowSystemOrRemarshal(messageMediator, exception);
                return null;
            }

            consumeServiceContexts(orb, messageMediator);

            
            
            
            inputObject.performORBVersionSpecificInit();

            if (messageMediator.isSystemExceptionReply()) {
                SystemException se = messageMediator.getSystemExceptionReply();
                reportException( "received system exception", se);

                boolean doRemarshal =
                    getContactInfoListIterator(orb)
                        .reportException(messageMediator.getContactInfo(), se);

                if (doRemarshal) {
                    reportException( "Do remarshal", se);
                        
                    
                    exception = orb.getPIHandler().invokeClientPIEndingPoint(
                        ReplyMessage.SYSTEM_EXCEPTION, se );

                    
                    
                    if( se == exception ) {
                        generalMessage( "Do remarshal: same exception");
                        
                        
                        exception = null;
                        continueOrThrowSystemOrRemarshal(messageMediator,
                                                         new RemarshalException());
                        throw wrapper.statementNotReachable1() ;
                    } else {
                        reportException( "Do remarshal: new exception", exception );
                        
                        continueOrThrowSystemOrRemarshal(messageMediator,
                                                         exception);
                        throw wrapper.statementNotReachable2() ;
                    }
                }

                
                reportException( "NO remarshal", se);

                ServiceContexts contexts = 
                    messageMediator.getReplyServiceContexts();
                if (contexts != null) {
                    UEInfoServiceContext usc =
                        (UEInfoServiceContext)
                        contexts.get(UEInfoServiceContext.SERVICE_CONTEXT_ID);

                    if (usc != null) {
                        Throwable unknown = usc.getUE() ;
                        UnknownException ue = new UnknownException(unknown);

                        reportException( "NO remarshal: UserException available",
                            unknown );

                        
                        exception = orb.getPIHandler().invokeClientPIEndingPoint(
                            ReplyMessage.SYSTEM_EXCEPTION, ue );

                        reportException( "NO remarshal: UserException available: PI exception ",
                            exception );

                        continueOrThrowSystemOrRemarshal(messageMediator, exception);
                        throw wrapper.statementNotReachable3() ;
                    }
                }

                
                
                reportException( "general exception", se);

                
                exception = orb.getPIHandler().invokeClientPIEndingPoint(
                    ReplyMessage.SYSTEM_EXCEPTION, se );

                reportException( "general exception: PI exception", exception );

                continueOrThrowSystemOrRemarshal(messageMediator, exception);

                
                
                throw wrapper.statementNotReachable4() ;
            } else if (messageMediator.isUserExceptionReply()) {
                getContactInfoListIterator(orb)
                    .reportSuccess(messageMediator.getContactInfo());

                String exceptionRepoId = peekUserExceptionId(inputObject);
                receivedUserException(exceptionRepoId);

                Exception newException = null;

                if (messageMediator.isDIIRequest()) {
                    exception = messageMediator.unmarshalDIIUserException(
                                    exceptionRepoId, (InputStream)inputObject);
                    newException = orb.getPIHandler().invokeClientPIEndingPoint(
                                       ReplyMessage.USER_EXCEPTION, exception );
                    messageMediator.setDIIException(newException);

                    receivedUserExceptionDII(exception, newException);
                } else {
                    ApplicationException appException = new ApplicationException(
                        exceptionRepoId, (org.omg.CORBA.portable.InputStream)inputObject);

                    exception = appException;

                    newException = orb.getPIHandler().invokeClientPIEndingPoint(
                                       ReplyMessage.USER_EXCEPTION, appException );

                    receivedUserExceptionNotDII(exception, newException);
                }

                if (newException != exception) {
                    continueOrThrowSystemOrRemarshal(messageMediator,newException);
                }

                if (newException instanceof ApplicationException) {
                    throw (ApplicationException)newException;
                }
                
                
                return inputObject;

            } else if (messageMediator.isLocationForwardReply()) {
                generalMessage( "received location forward");
                
                
                getContactInfoListIterator(orb).reportRedirect(
                    messageMediator.getContactInfo(),
                    messageMediator.getForwardedIOR());

                
                Exception newException = orb.getPIHandler().invokeClientPIEndingPoint(
                    ReplyMessage.LOCATION_FORWARD, null );

                if( !(newException instanceof RemarshalException) ) {
                    exception = newException;
                }

                
                
                
                if( exception != null ) {
                    continueOrThrowSystemOrRemarshal(messageMediator, exception);
                }
                continueOrThrowSystemOrRemarshal(messageMediator,
                                                 new RemarshalException());
                throw wrapper.statementNotReachable5() ;

            } else if (messageMediator.isDifferentAddrDispositionRequestedReply()){
                generalMessage( "received different addressing dispostion request");

                
                getContactInfoListIterator(orb).reportAddrDispositionRetry(
                    messageMediator.getContactInfo(),
                    messageMediator.getAddrDispositionReply());

                
                Exception newException = orb.getPIHandler().invokeClientPIEndingPoint(
                    ReplyMessage.NEEDS_ADDRESSING_MODE, null);

                
                if( !(newException instanceof RemarshalException) ) {
                    exception = newException;
                }

                
                
                
                if( exception != null ) {
                    continueOrThrowSystemOrRemarshal(messageMediator, exception);
                }
                continueOrThrowSystemOrRemarshal(messageMediator,
                                                 new RemarshalException());
                throw wrapper.statementNotReachable6() ;
            } else  {
                generalMessage( "received normal response");

                getContactInfoListIterator(orb)
                    .reportSuccess(messageMediator.getContactInfo());

                messageMediator.handleDIIReply((InputStream)inputObject);

                
                exception = orb.getPIHandler().invokeClientPIEndingPoint(
                    ReplyMessage.NO_EXCEPTION, null );

                
                continueOrThrowSystemOrRemarshal(messageMediator, exception);

                return inputObject;
            }
        } finally {
            exit_processResponse() ;
        }
    }

    
    
    
    
    
    
    @Subcontract
    protected void continueOrThrowSystemOrRemarshal(
        MessageMediator messageMediator, Exception exception)
        throws 
            SystemException, RemarshalException
    {
        final ORB orb = messageMediator.getBroker();

        if ( exception == null ) {
            
        } else if( exception instanceof RemarshalException ) {
            
            orb.getInvocationInfo().setIsRetryInvocation(true);

            
            
            
            
            unregisterWaiter(orb);
            throw (RemarshalException)exception;
        } else {
            throw (SystemException)exception;
        }
    }

    protected ContactInfoListIterator  getContactInfoListIterator(ORB orb) {
        return (ContactInfoListIterator) orb.getInvocationInfo().getContactInfoListIterator();
    }

    @Subcontract
    protected void registerWaiter(MessageMediator messageMediator) {
        if (messageMediator.getConnection() != null) {
            messageMediator.getConnection().registerWaiter(messageMediator);
        }
    }

    @Subcontract
    protected void unregisterWaiter(ORB orb) {
        MessageMediator messageMediator =
            orb.getInvocationInfo().getMessageMediator();
        if (messageMediator!=null && messageMediator.getConnection() != null) {
            
            
            
            messageMediator.getConnection().unregisterWaiter(messageMediator);
        }
    }

    @Subcontract
    protected void addServiceContexts(MessageMediator messageMediator) {
        ORB orb = messageMediator.getBroker();
        Connection c = messageMediator.getConnection();
        GIOPVersion giopVersion = messageMediator.getGIOPVersion();

        ServiceContexts contexts = null;

        
        if (ORBUtility.getEncodingVersion() != ORBConstants.CDR_ENC_VERSION) {    
            contexts = messageMediator.getRequestServiceContexts();
            ORBVersionServiceContext lsc =
                ServiceContextDefaults.getORBVersionServiceContext() ;
            contexts.put(lsc);
            return;
        }

        if (c != null &&            
            giopVersion.equals(GIOPVersion.V1_2) && 
            c.getBroker().getORBData().alwaysSendCodeSetServiceContext()) {
            if (!c.isPostInitialContexts()) {          
                contexts = (messageMediator.getBroker()).
                                                getServiceContextsCache().get(
                                                ServiceContextsCache.CASE.CLIENT_INITIAL);
            } else {
                contexts = messageMediator.getBroker()
                    .getServiceContextsCache().get(
                        ServiceContextsCache.CASE.CLIENT_SUBSEQUENT);
            }

            addCodeSetServiceContext(c, contexts, giopVersion);

            messageMediator.setRequestServiceContexts(contexts);

        } else {
            contexts = messageMediator.getRequestServiceContexts();
        
            addCodeSetServiceContext(c, contexts, giopVersion);

            
            
            
            
            
            contexts.put( msfvc );

            

            contexts.put( ovsc ) ;

            
            if ((c != null) && !c.isPostInitialContexts()) {
                
                
                
                
                SendingContextServiceContext scsc =
                  ServiceContextDefaults.makeSendingContextServiceContext(
                                                    orb.getFVDCodeBaseIOR() ) ; 
                contexts.put( scsc ) ;
            }
        }    
    }

    @Subcontract
    protected void consumeServiceContexts(ORB orb, 
                                        MessageMediator messageMediator)
    {
        ServiceContexts ctxts = messageMediator.getReplyServiceContexts();
        ServiceContext sc ;

        if (ctxts == null) {
            return; 
        }

        sc = ctxts.get( SendingContextServiceContext.SERVICE_CONTEXT_ID ) ;

        if (sc != null) {
            SendingContextServiceContext scsc =
                (SendingContextServiceContext)sc ;
            IOR ior = scsc.getIOR() ;

            try {
                
                if (messageMediator.getConnection() != null) {
                    messageMediator.getConnection().setCodeBaseIOR(ior);
                }
            } catch (ThreadDeath td) {
                throw td ;
            } catch (Throwable t) {
                throw wrapper.badStringifiedIor( t ) ;
            }
        } 

        
        
        sc = ctxts.get( ORBVersionServiceContext.SERVICE_CONTEXT_ID ) ;

        if (sc != null) {
            ORBVersionServiceContext lsc =
               (ORBVersionServiceContext) sc;

            ORBVersion version = lsc.getVersion();
            orb.setORBVersion( version ) ;
        }

        getExceptionDetailMessage(messageMediator, wrapper);
    }

    @Subcontract
    protected void getExceptionDetailMessage(
        MessageMediator  messageMediator,
        ORBUtilSystemException wrapper)
    {
        ServiceContext sc = messageMediator.getReplyServiceContexts()
            .get(ExceptionDetailMessage.value);
        if (sc == null) {
            return;
        }

        if (! (sc instanceof UnknownServiceContext)) {
            throw wrapper.badExceptionDetailMessageServiceContextType();
        }
        byte[] data = ((UnknownServiceContext)sc).getData();
        EncapsInputStream in = new EncapsInputStream(messageMediator.getBroker(), data, data.length);
        in.consumeEndian();

        String msg =
              "----------BEGIN server-side stack trace----------\n"
            + in.read_wstring() + "\n"
            + "----------END server-side stack trace----------";

        messageMediator.setReplyExceptionDetailMessage(msg);
    }

    @Subcontract
    public void endRequest(ORB orb, Object self, CDRInputObject inputObject)
    {
        try {
            exit_clientDecoding();

            
            
            

            MessageMediator messageMediator =
                orb.getInvocationInfo().getMessageMediator();
            if (messageMediator != null) {
                ORBUtility.popEncVersionFromThreadLocalState();

                if (messageMediator.getConnection() != null) {
                    messageMediator.sendCancelRequestIfFinalFragmentNotSent();
                }

                

                CDRInputObject inputObj = messageMediator.getInputObject();
                if (inputObj != null) {
                    inputObj.close();
                }

                CDROutputObject outputObj = messageMediator.getOutputObject();
                if (outputObj != null) {
                    outputObj.close();
                }

                
            }

            
            
            
            

            
            
            
            
            unregisterWaiter(orb);

            
            
            
            
            orb.getPIHandler().cleanupClientPIRequest();
            
            
        } catch (IOException ex) { 
            
            
            reportException("ignoring IOException", ex );
        } finally {
            exit_totalRequest() ;
        }
    }

    @Subcontract
    protected void performCodeSetNegotiation(MessageMediator messageMediator) {
        Connection conn = messageMediator.getConnection();
        if (conn == null) {
            return;
        }

        GIOPVersion giopVersion = messageMediator.getGIOPVersion();
        if (giopVersion.equals(GIOPVersion.V1_0)) {
            return;
        }

        IOR ior = (messageMediator.getContactInfo()).getEffectiveTargetIOR();

        synchronized( conn ) {
            if (conn.getCodeSetContext() != null) {
                return;
            }
            
            
            
            
            IIOPProfileTemplate temp = (IIOPProfileTemplate)ior.getProfile().
                getTaggedProfileTemplate();
            Iterator iter = temp.iteratorById(TAG_CODE_SETS.value);
            if (!iter.hasNext()) {
                
                
                
                return;
            }

            
            
            CodeSetComponentInfo serverCodeSets
                = ((CodeSetsComponent)iter.next()).getCodeSetComponentInfo();

            
            
            CodeSetComponentInfo.CodeSetContext result
                = CodeSetConversion.impl().negotiate(
                      conn.getBroker().getORBData().getCodeSetComponentInfo(),
                      serverCodeSets);
            
            conn.setCodeSetContext(result);
        }
    }

    @Subcontract
    protected void addCodeSetServiceContext(Connection conn,
        ServiceContexts ctxs, GIOPVersion giopVersion) {

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        if (giopVersion.equals(GIOPVersion.V1_0) || conn == null) {
            return;
        }
        
        CodeSetComponentInfo.CodeSetContext codeSetCtx = null;

        if (conn.getBroker().getORBData().alwaysSendCodeSetServiceContext() ||
            !conn.isPostInitialContexts()) {

            
            codeSetCtx = conn.getCodeSetContext();
        }
        
        
        
        
        
        if (codeSetCtx == null) {
            return;
        }

        CodeSetServiceContext cssc = 
            ServiceContextDefaults.makeCodeSetServiceContext(codeSetCtx);
        ctxs.put(cssc);
    }    

    @Subcontract
    protected String peekUserExceptionId(CDRInputObject inputObject) {
        CDRInputObject cdrInputObject = inputObject;
        
        cdrInputObject.mark(Integer.MAX_VALUE);
        String result = cdrInputObject.read_string();
        cdrInputObject.reset();
        return result;
    }                     
}


