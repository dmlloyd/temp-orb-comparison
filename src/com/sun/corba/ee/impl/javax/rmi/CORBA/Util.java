



package com.sun.corba.ee.impl.javax.rmi.CORBA; 

import java.rmi.RemoteException;
import java.rmi.UnexpectedException;

import java.rmi.server.RMIClassLoader;

import java.util.WeakHashMap;

import java.io.Serializable;
import java.io.NotSerializableException;


import javax.rmi.CORBA.ValueHandler;
import javax.rmi.CORBA.Tie;

import java.security.AccessController;
import java.security.PrivilegedAction;

import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.AccessException;
import java.rmi.Remote;
import java.rmi.ServerError;
import java.rmi.ServerException;

import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionRolledbackException;
import javax.transaction.InvalidTransactionException;


import javax.activity.ActivityRequiredException ;
import javax.activity.ActivityCompletedException ;
import javax.activity.InvalidActivityException ;

import org.omg.CORBA.SystemException;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ACTIVITY_REQUIRED;
import org.omg.CORBA.ACTIVITY_COMPLETED;
import org.omg.CORBA.INVALID_ACTIVITY;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;





import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.orb.ORBVersionFactory;
import com.sun.corba.ee.spi.protocol.ClientDelegate;
import com.sun.corba.ee.spi.transport.ContactInfoList ;
import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcher ;
import com.sun.corba.ee.spi.copyobject.CopierManager ;
import com.sun.corba.ee.impl.io.SharedSecrets;
import com.sun.corba.ee.impl.io.ValueHandlerImpl;
import com.sun.corba.ee.spi.misc.ORBConstants;
import com.sun.corba.ee.impl.misc.ORBUtility;
import com.sun.corba.ee.spi.logging.OMGSystemException;
import com.sun.corba.ee.impl.util.Utility;
import com.sun.corba.ee.impl.util.JDKBridge;
import com.sun.corba.ee.spi.logging.UtilSystemException;

import com.sun.corba.ee.impl.misc.ClassInfoCache ;
import java.util.IdentityHashMap;
import java.util.Map;
import org.glassfish.pfl.basic.logex.OperationTracer;
import org.glassfish.pfl.dynamic.copyobject.spi.ObjectCopier;
import org.glassfish.pfl.dynamic.copyobject.spi.ReflectiveCopyException;


public class Util implements javax.rmi.CORBA.UtilDelegate 
{
    
    private static KeepAlive keepAlive = null;

    
    private static final IdentityHashMap<Remote,Tie> exportedServants =
        new IdentityHashMap<Remote,Tie>();

    private static ValueHandler valueHandlerSingleton;          

    private static final UtilSystemException utilWrapper =
        UtilSystemException.self ;

    private static Util instance = null;

    
    
    private WeakHashMap<java.lang.Class<?>, String> annotationMap =
        new WeakHashMap<java.lang.Class<?>, String> ();

    private static final java.lang.Object annotObj = new java.lang.Object();

    private static final String SUN_JAVA_VENDOR = "Sun Microsystems Inc." ;

    static {
        
        
        valueHandlerSingleton = SharedSecrets.getJavaCorbaAccess().newValueHandlerImpl();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    public Util() { }

    public static synchronized Util getInstance() {
        if (instance == null) {
            instance = new Util() ;
        }

        return instance ;
    }

    
    
    public void unregisterTargetsForORB(org.omg.CORBA.ORB orb) 
    {
        
        
        Map<Remote,Tie> copy =
            new IdentityHashMap<Remote,Tie>( exportedServants ) ;

        for (Remote key : copy.keySet() ) {
            Remote target = key instanceof Tie ? ((Tie)key).getTarget() : key ;

            
            
            try {
                if (orb == getTie(target).orb()) {
                    try {
                        unexportObject(target);
                    } catch( java.rmi.NoSuchObjectException ex ) {
                        
                        
                    }
                }
            } catch (SystemException se) {
                utilWrapper.handleSystemException(se);
            }
        }
    }

   
    public RemoteException mapSystemException(SystemException ex) 
    {
        if (ex instanceof UnknownException) {
            Throwable orig = ((UnknownException)ex).originalEx;
            if (orig instanceof Error) {
                return new ServerError("Error occurred in server thread",(Error)orig);
            } else if (orig instanceof RemoteException) {
                return new ServerException("RemoteException occurred in server thread",
                    (Exception)orig);
            } else if (orig instanceof RuntimeException) {
                throw (RuntimeException) orig;
            }
        }

        
        String name = ex.getClass().getName();
        String corbaName = name.substring(name.lastIndexOf('.')+1);
        String status;
        switch (ex.completed.value()) {
            case CompletionStatus._COMPLETED_YES:
                status = "Yes";
                break;
            case CompletionStatus._COMPLETED_NO:
                status = "No";
                break;
            case CompletionStatus._COMPLETED_MAYBE:
            default:
                status = "Maybe";
                break;
        }
        
        String message = "CORBA " + corbaName + " " + ex.minor + " " + status;

        
        if (ex instanceof COMM_FAILURE) {
            return new MarshalException(message, ex);
        } else if (ex instanceof INV_OBJREF) {
            RemoteException newEx = new NoSuchObjectException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof NO_PERMISSION) {
            return new AccessException(message, ex);
        } else if (ex instanceof MARSHAL) {
            return new MarshalException(message, ex);
        } else if (ex instanceof OBJECT_NOT_EXIST) {
            RemoteException newEx = new NoSuchObjectException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof TRANSACTION_REQUIRED) {
            RemoteException newEx = new TransactionRequiredException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof TRANSACTION_ROLLEDBACK) {
            RemoteException newEx = new TransactionRolledbackException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof INVALID_TRANSACTION) {
            RemoteException newEx = new InvalidTransactionException(message);
            newEx.detail = ex;
            return newEx;
        } else if (ex instanceof BAD_PARAM) {
            Exception inner = ex;

            
            
            if (ex.minor == ORBConstants.LEGACY_SUN_NOT_SERIALIZABLE ||
                ex.minor == OMGSystemException.NOT_SERIALIZABLE) {

                if (ex.getMessage() != null) {
                    inner = new NotSerializableException(ex.getMessage());
                } else {
                    inner = new NotSerializableException();
                }

                inner.initCause( ex ) ;
            }

            return new MarshalException(message,inner);
        } else if (ex instanceof ACTIVITY_REQUIRED) {
            return new ActivityRequiredException( message, ex ) ;
        } else if (ex instanceof ACTIVITY_COMPLETED) {
            return new ActivityCompletedException( message, ex ) ;
        } else if (ex instanceof INVALID_ACTIVITY) {
            return new InvalidActivityException( message, ex ) ;
        }

        
        return new RemoteException(message, ex);
    }

    
    public void writeAny( org.omg.CORBA.portable.OutputStream out, 
                         java.lang.Object obj) 
    {
        org.omg.CORBA.ORB orb = out.orb();

        
        java.lang.Object newObj = Utility.autoConnect(obj, orb, false);

        
        
        
        
        
        if (ORBUtility.getEncodingVersion() != ORBConstants.CDR_ENC_VERSION) {
            ((org.omg.CORBA_2_3.portable.OutputStream)out).
                                        write_abstract_interface(newObj);
            return;
        }

        
        Any any = orb.create_any();

        if (newObj instanceof org.omg.CORBA.Object) {
            any.insert_Object((org.omg.CORBA.Object)newObj);
        } else {
            if (newObj == null) {
                
                
                any.insert_Value(null, createTypeCodeForNull(orb));
            } else {
                if (newObj instanceof Serializable) {
                    
                    
                    TypeCode tc = createTypeCode((Serializable)newObj, any, orb);
                    if (tc == null) {
                        any.insert_Value((Serializable) newObj);
                    } else {
                        any.insert_Value((Serializable) newObj, tc);
                    }
                } else if (newObj instanceof Remote) {
                    ORBUtility.throwNotSerializableForCorba(newObj.getClass().getName());
                } else {
                    ORBUtility.throwNotSerializableForCorba(newObj.getClass().getName());
                }
            }
        }

        out.write_any(any);
    }

    
    private TypeCode createTypeCode(Serializable obj,
                                    org.omg.CORBA.Any any,
                                    org.omg.CORBA.ORB orb) {

        if (any instanceof com.sun.corba.ee.impl.corba.AnyImpl &&
            orb instanceof ORB) {

            com.sun.corba.ee.impl.corba.AnyImpl anyImpl
                = (com.sun.corba.ee.impl.corba.AnyImpl)any;

            ORB ourORB = (ORB)orb;

            return anyImpl.createTypeCodeForClass(obj.getClass(), ourORB);
        } else {
            return null;
        }
    }


    
    private TypeCode createTypeCodeForNull(org.omg.CORBA.ORB orb) 
    {
        if (orb instanceof ORB) {

            ORB ourORB = (ORB)orb;

            
            
            
            
            
            if (!ORBVersionFactory.getFOREIGN().equals(ourORB.getORBVersion()) &&
                ORBVersionFactory.getNEWER().compareTo(ourORB.getORBVersion()) > 0) {

                return orb.get_primitive_tc(TCKind.tk_value);
            }
        }

        

        
        String abstractBaseID = "IDL:omg.org/CORBA/AbstractBase:1.0";

        return orb.create_abstract_interface_tc(abstractBaseID, "");
    }

    
    public Object readAny(InputStream in) 
    {
        
        
        
        
        
        if (ORBUtility.getEncodingVersion() != ORBConstants.CDR_ENC_VERSION) {
            return ((org.omg.CORBA_2_3.portable.InputStream)in).
                                          read_abstract_interface();
        }

        Any any = in.read_any();
        if ( any.type().kind().value() == TCKind._tk_objref ) {
            return any.extract_Object();
        } else {
            return any.extract_Value();
        }
    }

    
    public void writeRemoteObject(OutputStream out, java.lang.Object obj) 
    {
        
        
    
        Object newObj = Utility.autoConnect(obj,out.orb(),false);
        out.write_Object((org.omg.CORBA.Object)newObj);
    }
    
    
    public void writeAbstractObject( OutputStream out, java.lang.Object obj ) 
    {
        
        
    
        Object newObj = Utility.autoConnect(obj,out.orb(),false);
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_abstract_interface(newObj);
    }
    
    
    @SuppressWarnings("unchecked")
    public void registerTarget(javax.rmi.CORBA.Tie tie, java.rmi.Remote target) 
    {
        synchronized (exportedServants) {
            
            if (lookupTie(target) == null) {
                
                exportedServants.put(target,tie);
                tie.setTarget(target);
            
                
                if (keepAlive == null) {
                    
                    
                    keepAlive = (KeepAlive)AccessController.doPrivileged(
                        new PrivilegedAction<Object>() {
                            public java.lang.Object run() {
                                return new KeepAlive();
                            }
                        });
                    keepAlive.start();
                }
            }
        }
    }
    
    
    public void unexportObject(java.rmi.Remote target) 
        throws java.rmi.NoSuchObjectException 
    {
        synchronized (exportedServants) {
            Tie cachedTie = lookupTie(target);
            if (cachedTie != null) {
                exportedServants.remove(target);
                Utility.purgeStubForTie(cachedTie);
                Utility.purgeTieAndServant(cachedTie);
                try {
                    cleanUpTie(cachedTie);
                } catch (BAD_OPERATION e) {
                    
                } catch (org.omg.CORBA.OBJ_ADAPTER e) {
                    
                    
                }
                
                
                if (exportedServants.isEmpty()) {
                    keepAlive.quit();
                    keepAlive = null;
                }
            } else {
                throw new java.rmi.NoSuchObjectException("Tie not found" );
            }
        }
    }

    protected void cleanUpTie(Tie cachedTie) 
        throws java.rmi.NoSuchObjectException 
    {
        cachedTie.setTarget(null);
        cachedTie.deactivate();
    }
    
    
    public Tie getTie (Remote target) 
    {
        synchronized (exportedServants) {
            return lookupTie(target);
        }
    }

    
    private static Tie lookupTie (Remote target) 
    {
        Tie result = exportedServants.get(target);
        if (result == null && target instanceof Tie) {
            if (exportedServants.containsKey(target)) {
                result = (Tie)target;
            }
        }
        return result;
    }

    
    public ValueHandler createValueHandler() 
    {
        return valueHandlerSingleton;
    }

    
    public String getCodebase(java.lang.Class clz) {
        String annot ;
        synchronized (annotObj) {
            annot = annotationMap.get(clz);
        }

        if (annot == null) {
            
            annot = RMIClassLoader.getClassAnnotation(clz);

            synchronized( annotObj ) {
                annotationMap.put(clz, annot);
            }
        }

        return annot;
    }

    
    public Class loadClass( String className, String remoteCodebase,    
        ClassLoader loader) throws ClassNotFoundException 
    {
        return JDKBridge.loadClass(className,remoteCodebase,loader);                                
    }

    
    public boolean isLocal(javax.rmi.CORBA.Stub stub) throws RemoteException 
    {
        boolean result = false ;

        try {
            org.omg.CORBA.portable.Delegate delegate = stub._get_delegate() ;
            if (delegate instanceof ClientDelegate) {
                
                ClientDelegate cdel = (ClientDelegate)delegate ;
                ContactInfoList cil = cdel.getContactInfoList() ;
                LocalClientRequestDispatcher lcs =
                    cil.getLocalClientRequestDispatcher() ;
                result = lcs.useLocalInvocation( null ) ;
            } else {
                
                result = delegate.is_local( stub ) ;
            }
        } catch (SystemException e) {
            throw mapSystemException(e);
        }

        return result ;
    }
    
    
    public RemoteException wrapException(Throwable orig) 
    {
        if (orig instanceof SystemException) {
            return mapSystemException((SystemException)orig);
        }
        
        if (orig instanceof Error) {
            return new ServerError("Error occurred in server thread",(Error)orig);   
        } else if (orig instanceof RemoteException) {
            return new ServerException("RemoteException occurred in server thread",
                                       (Exception)orig);   
        } else if (orig instanceof RuntimeException) {
            throw (RuntimeException) orig;
        }       
        
        if (orig instanceof Exception) {
            return new UnexpectedException(orig.toString(),
                (Exception) orig);
        } else {
            return new UnexpectedException(orig.toString());
        }
    }

    
    public Object[] copyObjects (Object[] obj, org.omg.CORBA.ORB orb)
        throws RemoteException 
    {
        if (obj == null) {
            throw new NullPointerException();
        }

        Class<?> compType = obj.getClass().getComponentType() ;
        ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( compType ) ;
        if (cinfo.isARemote(compType) && cinfo.isInterface()) {
            
            
            
            Remote[] result = new Remote[obj.length] ;
            System.arraycopy( (Object)obj, 0, (Object)result, 0, obj.length ) ;
            return (Object[])copyObject( result, orb ) ;
        } else {
            return (Object[]) copyObject( obj, orb );
        }
    }

    
    public Object copyObject (Object obj, org.omg.CORBA.ORB orb)
        throws RemoteException 
    {
        try {
            if (((ORB)orb).operationTraceDebugFlag) {
                OperationTracer.enable() ;
            }

            OperationTracer.begin( "copyObject") ;

            if (orb instanceof ORB) {
                ORB lorb = (ORB)orb ;

                try {
                    try {
                        
                        
                        return lorb.peekInvocationInfo().getCopierFactory().make().copy( obj ) ;
                    } catch (java.util.EmptyStackException exc) {
                        
                        
                        CopierManager cm = lorb.getCopierManager() ;
                        ObjectCopier copier = cm.getDefaultObjectCopierFactory().make() ;
                        return copier.copy( obj ) ;
                    }
                } catch (ReflectiveCopyException exc) {
                    RemoteException rexc = new RemoteException() ;
                    rexc.initCause( exc ) ;
                    throw rexc ;
                }
            } else {
                if (obj instanceof Remote) {
                    
                    
                    return Utility.autoConnect( obj, orb, true ) ;
                }

                org.omg.CORBA_2_3.portable.OutputStream out =
                    (org.omg.CORBA_2_3.portable.OutputStream)orb.create_output_stream();
                out.write_value((Serializable)obj);
                org.omg.CORBA_2_3.portable.InputStream in =
                    (org.omg.CORBA_2_3.portable.InputStream)out.create_input_stream();
                return in.read_value();
            }
        } finally {
            OperationTracer.disable();
            OperationTracer.finish(); 
        }
    }
}

class KeepAlive extends Thread 
{
    boolean quit = false;
    
    public KeepAlive () 
    {
        setDaemon(false);
    }
    
    @Override
    public synchronized void run () 
    {
        while (!quit) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
    }
    
    public synchronized void quit () 
    {
        quit = true;
        notifyAll();
    }  
}
