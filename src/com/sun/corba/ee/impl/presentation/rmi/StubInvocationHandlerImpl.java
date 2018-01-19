


package com.sun.corba.ee.impl.presentation.rmi ;

import com.sun.corba.ee.impl.javax.rmi.CORBA.Util;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.ee.spi.presentation.rmi.InvocationInterceptor;
import com.sun.corba.ee.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.ee.spi.presentation.rmi.PresentationManager;
import com.sun.corba.ee.spi.presentation.rmi.StubAdapter;
import com.sun.corba.ee.spi.protocol.ClientDelegate;
import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.ee.spi.trace.IsLocal;
import com.sun.corba.ee.spi.transport.ContactInfoList;
import org.glassfish.pfl.basic.proxy.DynamicAccessPermission;
import org.glassfish.pfl.basic.proxy.LinkedInvocationHandler;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;

@IsLocal
public final class StubInvocationHandlerImpl implements LinkedInvocationHandler  
{
    private transient PresentationManager.ClassData classData ;
    private transient PresentationManager pm ;
    private transient org.omg.CORBA.Object stub ;
    private transient Proxy self ;

    public void setProxy( Proxy self )
    {
        this.self = self ;
    }

    public Proxy getProxy()
    {
        return self ;
    }

    public StubInvocationHandlerImpl( PresentationManager pm,
        PresentationManager.ClassData classData, org.omg.CORBA.Object stub ) 
    {
        if (!PresentationDefaults.inAppServer()) {
            SecurityManager s = System.getSecurityManager();
            if (s != null) {
                s.checkPermission(new DynamicAccessPermission("access"));
            }
        }

        this.classData = classData ;
        this.pm = pm ;
        this.stub = stub ;
    }

    @IsLocal
    private boolean isLocal(Delegate delegate)
    {
        boolean result = false ;
        if (delegate instanceof ClientDelegate) {
            ClientDelegate cdel = (ClientDelegate)delegate ;
            ContactInfoList cil = cdel.getContactInfoList() ;
            LocalClientRequestDispatcher lcrd =
                cil.getLocalClientRequestDispatcher() ;
            result = lcrd.useLocalInvocation( null ) ;
        }
         
        return result ;
    }
    
    public Object invoke( Object proxy, final Method method,
        Object[] args ) throws Throwable {

        Delegate delegate = null ;
        try {
            delegate = StubAdapter.getDelegate( stub ) ;
        } catch (SystemException ex) {
            throw Util.getInstance().mapSystemException(ex) ;
        } 

        org.omg.CORBA.ORB delORB = delegate.orb( stub ) ;
        if (delORB instanceof ORB) {
            ORB orb = (ORB)delORB ;

            InvocationInterceptor interceptor = orb.getInvocationInterceptor() ;

            try {
                interceptor.preInvoke() ;
            } catch (Exception exc) {
                
            }

            try {
                return privateInvoke( delegate, proxy, method, args ) ;
            } finally {
                try {
                    interceptor.postInvoke() ;
                } catch (Exception exc) {
                    
                }
            }
        } else {
            
            return privateInvoke( delegate, proxy, method, args ) ;
        }
    }

    @InfoMethod
    private void takingRemoteBranch() {}

    @InfoMethod
    private void takingLocalBranch() {}

    
    @IsLocal
    private Object privateInvoke( Delegate delegate, Object proxy, final Method method,
        Object[] args ) throws Throwable
    {
        boolean retry;
        do {
            retry = false;
            String giopMethodName = classData.getIDLNameTranslator().
              getIDLName( method )  ;
            DynamicMethodMarshaller dmm = 
              pm.getDynamicMethodMarshaller( method ) ;
           
            if (!isLocal(delegate)) {
                try {
                    takingRemoteBranch() ;
                    org.omg.CORBA_2_3.portable.InputStream in = null ;
                    try {
                        
                        org.omg.CORBA_2_3.portable.OutputStream out = 
                          (org.omg.CORBA_2_3.portable.OutputStream)
                          delegate.request( stub, giopMethodName, true);
                        
                        dmm.writeArguments( out, args ) ;
                        
                        in = (org.omg.CORBA_2_3.portable.InputStream)
                          delegate.invoke( stub, out);
                        
                        return dmm.readResult( in ) ;
                    } catch (ApplicationException ex) {
                        throw dmm.readException( ex ) ;
                    } catch (RemarshalException ex) {
                      
                      retry = true;
                    } finally {
                        delegate.releaseReply( stub, in );
                    }
                } catch (SystemException ex) {
                    throw Util.getInstance().mapSystemException(ex) ;
                } 
            } else {
                takingLocalBranch();
                org.omg.CORBA.ORB orb = delegate.orb( stub ) ;
                ServantObject so = delegate.servant_preinvoke( stub, giopMethodName,
                                                               method.getDeclaringClass() );
                if (so == null) {
                    
                    retry = true;
                    continue;
                }

                try {
                    Object[] copies = dmm.copyArguments( args, orb ) ;

                    if (!method.isAccessible()) {       
                        
                        
                        
                        AccessController.doPrivileged(new PrivilegedAction() {
                            public Object run() {
                                method.setAccessible( true ) ;
                                return null ;
                            } 
                        } ) ;
                    }

                    Object result = method.invoke( so.servant, copies ) ;

                    return dmm.copyResult( result, orb ) ;
                } catch (InvocationTargetException ex) {
                    Throwable mex = ex.getCause() ;
                    
                    if (dmm.isDeclaredException( mex ))
                        throw mex ;
                    else
                        throw Util.getInstance().wrapException(mex);
                } catch (Throwable thr) {
                    if (thr instanceof ThreadDeath)
                        throw thr;

                    
                    
                    
                    throw Util.getInstance().wrapException( thr ) ;
                } finally {              
                    delegate.servant_postinvoke( stub, so);
                }
            }
        } while (retry);
        return null;
    }
}
