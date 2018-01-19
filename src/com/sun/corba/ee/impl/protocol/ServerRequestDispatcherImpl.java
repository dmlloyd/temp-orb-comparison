




package com.sun.corba.ee.impl.protocol;



import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Any;

import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.orb.ORBVersion;
import com.sun.corba.ee.spi.orb.ORBVersionFactory;
import com.sun.corba.ee.spi.orb.ObjectKeyCacheEntry;
import com.sun.corba.ee.spi.ior.IOR ;
import com.sun.corba.ee.spi.ior.ObjectKey;
import com.sun.corba.ee.spi.ior.ObjectKeyTemplate;
import com.sun.corba.ee.spi.ior.ObjectAdapterId;
import com.sun.corba.ee.spi.oa.ObjectAdapterFactory;
import com.sun.corba.ee.spi.oa.ObjectAdapter;
import com.sun.corba.ee.spi.oa.OAInvocationInfo;
import com.sun.corba.ee.spi.oa.OADestroyed;
import com.sun.corba.ee.spi.oa.NullServant;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import com.sun.corba.ee.spi.protocol.ServerRequestDispatcher;
import com.sun.corba.ee.spi.protocol.ForwardException ;
import com.sun.corba.ee.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;

import com.sun.corba.ee.spi.servicecontext.ServiceContextDefaults;
import com.sun.corba.ee.spi.servicecontext.ServiceContext;
import com.sun.corba.ee.spi.servicecontext.ServiceContexts;
import com.sun.corba.ee.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.ee.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.ee.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.ee.spi.servicecontext.ORBVersionServiceContext;

import com.sun.corba.ee.impl.corba.ServerRequestImpl ;
import com.sun.corba.ee.impl.encoding.CDROutputObject;
import com.sun.corba.ee.impl.encoding.MarshalInputStream;
import com.sun.corba.ee.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.ee.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.ee.impl.misc.ORBUtility;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.spi.logging.POASystemException;
import com.sun.corba.ee.spi.trace.Subcontract;
import org.glassfish.pfl.basic.logex.OperationTracer;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;

@Subcontract
public class ServerRequestDispatcherImpl
    implements ServerRequestDispatcher
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;
    private static final POASystemException poaWrapper =
        POASystemException.self ;

    protected ORB orb; 

    public ServerRequestDispatcherImpl(ORB orb)
    {
        this.orb = orb;
    }

    
    @Subcontract
    public IOR locate(ObjectKey okey) {
        ObjectKeyTemplate oktemp = okey.getTemplate() ;

        try {
            checkServerId(okey);
        } catch (ForwardException fex) {
            return fex.getIOR() ;
        }

        
        findObjectAdapter(oktemp);

        return null ;
    }           

    @InfoMethod
    private void generalMessage( String msg ) { }

    @InfoMethod
    private void exceptionMessage( String msg, Throwable thr ) { }

    @Subcontract
    public void dispatch(MessageMediator request) {
        
        
        consumeServiceContexts(request);

        
        
        
        ((MarshalInputStream)request.getInputObject())
            .performORBVersionSpecificInit();

        ObjectKeyCacheEntry entry = request.getObjectKeyCacheEntry() ;
        ObjectKey okey = entry.getObjectKey();

        
        try {
            checkServerId(okey);
        } catch (ForwardException fex) {
            operationAndId(request.getOperationName(),
                request.getRequestId());

            request.getProtocolHandler()
                .createLocationForward(request, fex.getIOR(), null);
            return;
        }

        String operation = request.getOperationName();
        ObjectAdapter objectAdapter = entry.getObjectAdapter() ;

        try {
            byte[] objectId = okey.getId().getId() ;
            ObjectKeyTemplate oktemp = okey.getTemplate() ;

            if (objectAdapter == null) {
                
                
                objectAdapter = findObjectAdapter(oktemp);
                entry.setObjectAdapter( objectAdapter ) ;
            }

            java.lang.Object servant = getServantWithPI(request, objectAdapter,
                objectId, oktemp, operation);

            dispatchToServant(servant, request, objectId, objectAdapter);
        } catch (ForwardException ex) {
            generalMessage( "Caught ForwardException") ;
            
            
            request.getProtocolHandler()
                .createLocationForward(request, ex.getIOR(), null);
        } catch (OADestroyed ex) {
            generalMessage( "Caught OADestroyed" ) ;

            
            
            entry.clearObjectAdapter() ;

            
            
            
            

            
            
            dispatch(request);
        } catch (RequestCanceledException ex) {
            generalMessage( "Caught RequestCanceledException") ;

            
            
            
            
            throw ex;
        } catch (UnknownException ex) {
            generalMessage( "Caught UnknownException" ) ;

            
            
            
            
            
            
            if (ex.originalEx instanceof RequestCanceledException) {
                throw (RequestCanceledException) ex.originalEx;
            }

            ServiceContexts contexts =
                ServiceContextDefaults.makeServiceContexts(orb);
            UEInfoServiceContext usc =
                ServiceContextDefaults.makeUEInfoServiceContext(ex.originalEx);

            contexts.put( usc ) ;

            SystemException sysex = wrapper.unknownExceptionInDispatch( ex ) ;
            request.getProtocolHandler()
                .createSystemExceptionResponse(request, sysex,
                    contexts);
        } catch (Throwable ex) {
            generalMessage( "Caught other exception" ) ;
            request.getProtocolHandler()
                .handleThrowableDuringServerDispatch(
                    request, ex, CompletionStatus.COMPLETED_MAYBE);
        }

        return;
    }

    @Subcontract
    private void releaseServant(ObjectAdapter objectAdapter) {
        if (objectAdapter == null) {
            generalMessage( "Null object adapter" ) ;
            return ;
        }

        try {
            objectAdapter.returnServant();
        } finally {
            objectAdapter.exit();
            orb.popInvocationInfo() ;
        }
    }

    
    @Subcontract
    private java.lang.Object getServant(ObjectAdapter objectAdapter, 
        byte[] objectId, String operation) throws OADestroyed {

        OAInvocationInfo info = objectAdapter.makeInvocationInfo(objectId);
        info.setOperation(operation);
        orb.pushInvocationInfo(info);
        objectAdapter.getInvocationServant(info);
        return info.getServantContainer() ;
    }

    @Subcontract
    protected java.lang.Object getServantWithPI(MessageMediator request,
        ObjectAdapter objectAdapter, byte[] objectId, ObjectKeyTemplate oktemp,
        String operation) throws OADestroyed {

        
        
        
        orb.getPIHandler().initializeServerPIInfo(request, objectAdapter,
            objectId, oktemp);
        orb.getPIHandler().invokeServerPIStartingPoint();

        objectAdapter.enter() ;

        
        
        
        if (request != null) {
            request.setExecuteReturnServantInResponseConstructor(true);
        }

        java.lang.Object servant = getServant(objectAdapter, objectId,
            operation);

        
        
        
        
        String mdi = "unknown" ;

        if (servant instanceof NullServant) {
            handleNullServant(operation,
                (NullServant) servant);
        } else {
            mdi = objectAdapter.getInterfaces(servant, objectId)[0];
        }

        orb.getPIHandler().setServerPIInfo(servant, mdi);

        if (((servant != null) &&
            !(servant instanceof org.omg.CORBA.DynamicImplementation) &&
            !(servant instanceof org.omg.PortableServer.DynamicImplementation)) ||
            (SpecialMethod.getSpecialMethod(operation) != null)) {
            orb.getPIHandler().invokeServerPIIntermediatePoint();
        }

        return servant ;
    }

    @Subcontract
    protected void checkServerId(ObjectKey okey) {
        ObjectKeyTemplate oktemp = okey.getTemplate() ;
        int sId = oktemp.getServerId() ;
        int scid = oktemp.getSubcontractId() ;

        if (!orb.isLocalServerId(scid, sId)) {
            generalMessage("bad server ID");
            orb.handleBadServerId(okey);
        }
    }

    @Subcontract
    private ObjectAdapter findObjectAdapter(ObjectKeyTemplate oktemp) {
        RequestDispatcherRegistry scr = orb.getRequestDispatcherRegistry() ;
        int scid = oktemp.getSubcontractId() ;
        ObjectAdapterFactory oaf = scr.getObjectAdapterFactory(scid);
        if (oaf == null) {
            throw wrapper.noObjectAdapterFactory() ;
        }

        ObjectAdapterId oaid = oktemp.getObjectAdapterId() ;
        ObjectAdapter oa = oaf.find(oaid);

        if (oa == null) {
            throw wrapper.badAdapterId() ;
        }

        return oa ;
    }

    
    @Subcontract
    protected void handleNullServant(String operation, NullServant nserv ) {
        SpecialMethod specialMethod = SpecialMethod.getSpecialMethod(operation);

        if ((specialMethod == null) ||
            !specialMethod.isNonExistentMethod()) {
            throw nserv.getException() ;
        }
    }

    @InfoMethod
    private void objectInfo( String msg, Object obj ) { }

    @Subcontract
    protected void consumeServiceContexts(MessageMediator request) {
        operationAndId(request.getOperationName(), request.getRequestId());

            ServiceContexts ctxts = request.getRequestServiceContexts();
            ServiceContext sc ;
            GIOPVersion giopVersion = request.getGIOPVersion();

            
            
            

            boolean hasCodeSetContext = processCodeSetContext(request, ctxts);

            objectInfo( "GIOP version", giopVersion ) ;
            objectInfo( "Has code set context?" , hasCodeSetContext ) ;

            sc = ctxts.get(
                SendingContextServiceContext.SERVICE_CONTEXT_ID ) ;

            if (sc != null) {
                SendingContextServiceContext scsc =
                    (SendingContextServiceContext)sc ;
                IOR ior = scsc.getIOR() ;

                try {
                    request.getConnection().setCodeBaseIOR(ior);
                } catch (ThreadDeath td) {
                    throw td ;
                } catch (Throwable t) {
                    throw wrapper.badStringifiedIor( t ) ;
                }
            }

            
            
            
            
            

            
            
            
            

            
            
            
            
            
            boolean isForeignORB = false;

            if (giopVersion.equals(GIOPVersion.V1_0) && hasCodeSetContext) {
                generalMessage("Old Sun ORB");
                orb.setORBVersion(ORBVersionFactory.getOLD()) ;
                
            } else {
                
                
                isForeignORB = true;
            }

            
            
            sc = ctxts.get( ORBVersionServiceContext.SERVICE_CONTEXT_ID ) ;
            if (sc != null) {
                ORBVersionServiceContext ovsc =
                   (ORBVersionServiceContext) sc;

                ORBVersion version = ovsc.getVersion();
                orb.setORBVersion(version);

                isForeignORB = false;
            }

            if (isForeignORB) {
                generalMessage("Foreign ORB" ) ;
                orb.setORBVersion(ORBVersionFactory.getFOREIGN());
            }
    }
    
    @Subcontract
    protected MessageMediator dispatchToServant(
        java.lang.Object servant, 
        MessageMediator req,
        byte[] objectId, ObjectAdapter objectAdapter) 
    {
        try {
            if (orb.operationTraceDebugFlag) {
                OperationTracer.enable() ;
            }

            OperationTracer.begin( "Dispatch to servant" ) ;

            operationAndId( req.getOperationName(), req.getRequestId());
            objectInfo( "Servant info", servant ) ;

            MessageMediator response = null ;

            String operation = req.getOperationName() ;

            SpecialMethod method = SpecialMethod.getSpecialMethod(operation) ;
            if (method != null) {
                objectInfo( "Handling special method", method.getName() ) ;

                response = method.invoke(servant, req, objectId, objectAdapter);
                return response ;
            } 
            
            
            if (servant instanceof org.omg.CORBA.DynamicImplementation) {
                generalMessage( "Handling old style DSI type servant") ;

                org.omg.CORBA.DynamicImplementation dynimpl = 
                    (org.omg.CORBA.DynamicImplementation)servant;
                ServerRequestImpl sreq = new ServerRequestImpl(req, orb);

                
                
                dynimpl.invoke(sreq);
                
                response = handleDynamicResult(sreq, req);
            } else if (servant instanceof org.omg.PortableServer.DynamicImplementation) {
                generalMessage( "Handling POA DSI type servant" ) ;
                org.omg.PortableServer.DynamicImplementation dynimpl = 
                    (org.omg.PortableServer.DynamicImplementation)servant;
                ServerRequestImpl sreq = new ServerRequestImpl(req, orb);

                
                
                dynimpl.invoke(sreq);
                
                response = handleDynamicResult(sreq, req);
            } else {
                generalMessage( "Handling invoke handler type servant" ) ;
                InvokeHandler invhandle = (InvokeHandler)servant ;

                OutputStream stream = null;
                try {
                    stream = invhandle._invoke(operation,
                        (org.omg.CORBA.portable.InputStream) req.getInputObject(),
                        req);
                } catch (BAD_OPERATION e) {
                    wrapper.badOperationFromInvoke(e, operation);
                    throw e;
                }
                response = ((CDROutputObject)stream).getMessageMediator();
            }

            return response ;
        } finally {
            OperationTracer.disable() ;
            OperationTracer.finish( ) ;
        }
    }

    @Subcontract
    protected MessageMediator handleDynamicResult( ServerRequestImpl sreq,
        MessageMediator req) {

        MessageMediator response = null ;

        
        Any excany = sreq.checkResultCalled();

        if (excany == null) { 
            generalMessage( "Handling normal result" ) ;

            
            response = sendingReply(req);
            OutputStream os = (OutputStream) response.getOutputObject();
            sreq.marshalReplyParams(os);
        }  else {
            generalMessage( "Handling error" ) ;

            response = sendingReply(req, excany);
        }

        return response ;
    }

    @Subcontract
    protected MessageMediator sendingReply(MessageMediator req) {
        ServiceContexts scs = ServiceContextDefaults.makeServiceContexts(orb);
        return req.getProtocolHandler().createResponse(req, scs);
    }

    
    @Subcontract
    protected MessageMediator sendingReply(MessageMediator req,
        Any excany) {

        ServiceContexts scs = ServiceContextDefaults.makeServiceContexts(orb);
        operationAndId( req.getOperationName(), req.getRequestId() ) ;

        
        
        MessageMediator resp;
        String repId=null;
        try {
            repId = excany.type().id();
        } catch (org.omg.CORBA.TypeCodePackage.BadKind e) {
            throw wrapper.problemWithExceptionTypecode( e ) ;
        }

        if (ORBUtility.isSystemException(repId)) {
            generalMessage( "Handling system exception" ) ;

            
            InputStream in = excany.create_input_stream();
            SystemException ex = ORBUtility.readSystemException(in);
            
            resp = req.getProtocolHandler()
                .createSystemExceptionResponse(req, ex, scs);
        } else {
            generalMessage( "Handling user exception" ) ;

            resp = req.getProtocolHandler()
                .createUserExceptionResponse(req, scs);
            OutputStream os = (OutputStream)resp.getOutputObject();
            excany.write_value(os);
        }

        return resp;
    }

    @InfoMethod
    private void codeSetServiceContextInfo( CodeSetServiceContext csctx ) { }

    
    @Subcontract
    protected boolean processCodeSetContext(
        MessageMediator request, ServiceContexts contexts) {

        ServiceContext sc = contexts.get(
            CodeSetServiceContext.SERVICE_CONTEXT_ID);

        if (sc != null) {
            
            if (request.getConnection() == null) {
                return true;
            }

            
            
            
            if (request.getGIOPVersion().equals(GIOPVersion.V1_0)) {
                return true;
            }

            CodeSetServiceContext cssc = (CodeSetServiceContext)sc ;
            CodeSetComponentInfo.CodeSetContext csctx = cssc.getCodeSetContext();

            
            
            Connection connection = request.getConnection() ;

            synchronized (connection) {
                if (connection.getCodeSetContext() == null) {
                    operationAndId(request.getOperationName(), 
                        request.getRequestId() );
                    codeSetServiceContextInfo(cssc);

                    connection.setCodeSetContext(csctx);

                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    if (csctx.getCharCodeSet() !=
                        OSFCodeSetRegistry.ISO_8859_1.getNumber()) {
                        ((MarshalInputStream)request.getInputObject())
                            .resetCodeSetConverters();
                    }
                }
            }
        }

        
        
        
        
        
        
        
        return sc != null ;
    }

    @InfoMethod
    private void operationAndId( String operation, int rid ) { }
}



