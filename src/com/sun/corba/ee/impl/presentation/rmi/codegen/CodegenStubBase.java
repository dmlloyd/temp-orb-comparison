


package com.sun.corba.ee.impl.presentation.rmi.codegen ;

import java.io.ObjectInputStream ;
import java.io.IOException ;
import java.io.ObjectStreamException ;

import java.lang.reflect.InvocationHandler ;
import java.lang.reflect.Method ;
import java.lang.reflect.Field ;

import java.security.AccessController ;
import java.security.PrivilegedAction ;

import javax.rmi.CORBA.Stub ;


import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.spi.presentation.rmi.PresentationManager ;
import com.sun.corba.ee.spi.presentation.rmi.StubAdapter ;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException ;
import com.sun.corba.ee.impl.util.JDKBridge ;
import com.sun.corba.ee.impl.util.RepositoryId ;
import com.sun.corba.ee.impl.ior.StubIORImpl ;
import com.sun.corba.ee.impl.javax.rmi.CORBA.StubDelegateImpl ;
import com.sun.corba.ee.impl.presentation.rmi.StubInvocationHandlerImpl ;

public class CodegenStubBase extends Stub 
{
    private transient String[] typeIds ;
    private transient Method[] methods ;
    private transient PresentationManager.ClassData classData ;
    private transient InvocationHandler handler ;

    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private Object readResolve( ) throws ObjectStreamException
    {
        
        
        
        
        PresentationManager pm = ORB.getPresentationManager() ;
        PresentationManager.StubFactoryFactory sff = pm.getDynamicStubFactoryFactory();
        PresentationManager.StubFactory sf = sff.createStubFactory(
            classData.getMyClass().getName(), false, null, null, null ) ;
        org.omg.CORBA.Object stub = sf.makeStub() ;
        StubDelegateImpl stubSDI = getStubDelegateImpl( stub ) ;
        StubDelegateImpl mySDI = getStubDelegateImpl( this ) ;
        stubSDI.setIOR( mySDI.getIOR() ) ;
        return stub ;
    }

    
    
    
    
    
    
    
    private static StubDelegateImpl getStubDelegateImpl( 
        final org.omg.CORBA.Object stub )
    {
        StubDelegateImpl sdi = getStubDelegateImplField( stub ) ;
        if (sdi == null) {
            setDefaultDelegate(stub);
        }
        sdi = getStubDelegateImplField( stub ) ;
        return sdi ;
    }

    private static StubDelegateImpl getStubDelegateImplField( 
        final org.omg.CORBA.Object stub )
    {
        return (StubDelegateImpl)AccessController.doPrivileged( 
            new PrivilegedAction() {
                public Object run() {
                    try {
                        Field fld = Stub.class.getDeclaredField( "stubDelegate" ) ;
                        fld.setAccessible( true ) ;
                        return fld.get( stub ) ;
                    } catch (Exception exc) {
                        throw wrapper.couldNotAccessStubDelegate() ;
                    } 
                }
            }
        ) ;
    }

    private static Method setDefaultDelegateMethod = null ;

    private static void setDefaultDelegate( final org.omg.CORBA.Object stub )
    {
        AccessController.doPrivileged( 
            new PrivilegedAction() {
                public Object run() {
                    try {
                        if (setDefaultDelegateMethod == null) {
                            setDefaultDelegateMethod = 
                                Stub.class.getDeclaredMethod( "setDefaultDelegate") ;
                            setDefaultDelegateMethod.setAccessible( true ) ;
                        }

                        setDefaultDelegateMethod.invoke( stub ) ;
                    } catch (Exception exc) {
                        throw wrapper.couldNotAccessStubDelegate( exc ) ;
                    }
                    return null ;
                }
            }
        ) ;
    }

    private void readObject( ObjectInputStream stream ) throws
        IOException, ClassNotFoundException 
    {
        
        
        
        stream.defaultReadObject() ;

        StubDelegateImpl sdi = getStubDelegateImpl( this ) ;

        StubIORImpl ior = sdi.getIOR() ;
        String repositoryId = ior.getRepositoryId() ;
        String cname = RepositoryId.cache.getId( repositoryId ).getClassName() ; 

        Class cls = null ;

        try {
            cls = JDKBridge.loadClass( cname, null, null ) ;
        } catch (ClassNotFoundException exc) {
            throw wrapper.couldNotLoadInterface( exc, cname ) ;
        }

        PresentationManager pm = ORB.getPresentationManager() ;
        classData = pm.getClassData( cls ) ;

        InvocationHandler handler = new StubInvocationHandlerImpl( pm, 
            classData, this ) ; 
        initialize( classData, handler ) ;
    }

    public String[] _ids()
    {
        return typeIds.clone() ;
    }

    
    public void initialize( PresentationManager.ClassData classData,
        InvocationHandler handler )
    {
        this.classData = classData ;
        this.handler = handler ;
        typeIds = classData.getTypeIds() ;
        methods = classData.getIDLNameTranslator().getMethods() ;
    }

    
    
    
    
    protected Object selfAsBaseClass()
    {
        CodegenStubBase result = new CodegenStubBase() ;
        StubAdapter.setDelegate( result,
            StubAdapter.getDelegate( this )) ;
        return result ;
    }
    
    
    protected Object invoke( int methodNumber, Object[] args ) throws Throwable 
    {
        Method method = methods[methodNumber] ;
        
        
        return handler.invoke( null, method, args ) ;
    }
}
