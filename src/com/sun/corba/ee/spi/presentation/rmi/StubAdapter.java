


package com.sun.corba.ee.spi.presentation.rmi ;

import javax.rmi.CORBA.Tie ;

import org.omg.CORBA.portable.Delegate ;
import org.omg.CORBA.portable.ObjectImpl ;
import org.omg.CORBA.portable.OutputStream ;

import org.omg.PortableServer.POA ;
import org.omg.PortableServer.POAManager ;
import org.omg.PortableServer.POAManagerPackage.State ;
import org.omg.PortableServer.Servant ;

import org.omg.PortableServer.POAPackage.WrongPolicy ;
import org.omg.PortableServer.POAPackage.ServantNotActive ;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive ;

import org.omg.CORBA.ORB ;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException ;

import com.sun.corba.ee.impl.oa.poa.POAManagerImpl ;

 
public abstract class StubAdapter 
{
    private StubAdapter() {}

    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public static boolean isStubClass( Class cls )
    {
        return (ObjectImpl.class.isAssignableFrom( cls )) ||
            (DynamicStub.class.isAssignableFrom( cls )) ;
    }

    public static boolean isStub( Object stub )
    {
        return (stub instanceof DynamicStub) ||
            (stub instanceof ObjectImpl) ;
    }

    public static void setDelegate( Object stub, Delegate delegate ) 
    {
        if (stub instanceof DynamicStub) {
            ((DynamicStub) stub).setDelegate(delegate);
        } else if (stub instanceof ObjectImpl) {
            ((ObjectImpl) stub)._set_delegate(delegate);
        } else {
            throw wrapper.setDelegateRequiresStub();
        }
    }

    
    public static org.omg.CORBA.Object activateServant( Servant servant ) 
    {
        POA poa = servant._default_POA() ;
        org.omg.CORBA.Object ref = null ;

        try {
            ref = poa.servant_to_reference( servant ) ;
        } catch (ServantNotActive sna) {
            throw wrapper.getDelegateServantNotActive( sna ) ;
        } catch (WrongPolicy wp) {
            throw wrapper.getDelegateWrongPolicy( wp ) ;
        }

        
        
        POAManager mgr = poa.the_POAManager() ;
        if (mgr instanceof POAManagerImpl) {
            
            
            
            
            
            POAManagerImpl mgrImpl = (POAManagerImpl)mgr ;
            mgrImpl.implicitActivation() ;
        } else {
            
            
            
            
            if (mgr.get_state().value() == State._HOLDING) {
                try {
                    mgr.activate() ;
                } catch (AdapterInactive ai) {
                    throw wrapper.adapterInactiveInActivateServant( ai ) ;
                }
            }
        }

        return ref ;
    }

    
    public static org.omg.CORBA.Object activateTie( Tie tie )
    {
        
        if (tie instanceof ObjectImpl) {
            return tie.thisObject() ;
        } else if (tie instanceof Servant) {
            Servant servant = (Servant)tie ;
            return activateServant( servant ) ;
        } else {
            throw wrapper.badActivateTieCall() ;
        }
    }


    
    public static Delegate getDelegate( Object stub ) 
    {
        if (stub instanceof DynamicStub) {
            return ((DynamicStub) stub).getDelegate();
        } else if (stub instanceof ObjectImpl) {
            return ((ObjectImpl) stub)._get_delegate();
        } else if (stub instanceof Tie) {
            Tie tie = (Tie)stub ;
            org.omg.CORBA.Object ref = activateTie( tie ) ;
            return getDelegate( ref ) ;
        } else {
            throw wrapper.getDelegateRequiresStub();
        }
    }
    
    public static ORB getORB( Object stub ) 
    {
        if (stub instanceof DynamicStub) {
            return ((DynamicStub)stub).getORB() ;
        } else if (stub instanceof ObjectImpl) {
            return ((ObjectImpl) stub)._orb() ;
        } else {
            throw wrapper.getOrbRequiresStub() ;
        }
    }

    public static String[] getTypeIds( Object stub )
    {
        if (stub instanceof DynamicStub) {
            return ((DynamicStub)stub).getTypeIds() ;
        } else if (stub instanceof ObjectImpl) {
            return ((ObjectImpl)stub)._ids() ;
        } else {
            throw wrapper.getTypeIdsRequiresStub() ;
        }
    }

    public static void connect( Object stub, 
        ORB orb ) throws java.rmi.RemoteException 
    {
        if (stub instanceof DynamicStub) {
            ((DynamicStub)stub).connect( 
                (com.sun.corba.ee.spi.orb.ORB)orb ) ;
        } else if (stub instanceof javax.rmi.CORBA.Stub) {
            ((javax.rmi.CORBA.Stub)stub).connect( orb ) ;
        } else if (stub instanceof ObjectImpl) {
            orb.connect( (org.omg.CORBA.Object)stub ) ;
        } else {
            throw wrapper.connectRequiresStub() ;
        }
    }

    public static boolean isLocal( Object stub )
    {
        if (stub instanceof DynamicStub) {
            return ((DynamicStub)stub).isLocal() ;
        } else if (stub instanceof ObjectImpl) {
            return ((ObjectImpl)stub)._is_local() ;
        } else {
            throw wrapper.isLocalRequiresStub() ;
        }
    }

    public static OutputStream request( Object stub, 
        String operation, boolean responseExpected ) 
    {
        if (stub instanceof DynamicStub) {
            return ((DynamicStub)stub).request( operation,
                responseExpected ) ;
        } else if (stub instanceof ObjectImpl) {
            return ((ObjectImpl)stub)._request( operation,
                responseExpected ) ;
        } else {
            throw wrapper.requestRequiresStub() ;
        }
    }
}
