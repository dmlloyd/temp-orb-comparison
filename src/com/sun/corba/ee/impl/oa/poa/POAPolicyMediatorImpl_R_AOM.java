


package com.sun.corba.ee.impl.oa.poa ;

import org.omg.PortableServer.Servant ;
import org.omg.PortableServer.ServantManager ;
import org.omg.PortableServer.ForwardRequest ;
import org.omg.PortableServer.POAPackage.WrongPolicy ;
import org.omg.PortableServer.POAPackage.ObjectNotActive ;
import org.omg.PortableServer.POAPackage.NoServant ;

import com.sun.corba.ee.impl.oa.NullServantImpl ;


public class POAPolicyMediatorImpl_R_AOM extends POAPolicyMediatorBase_R {
    POAPolicyMediatorImpl_R_AOM( Policies policies, POAImpl poa ) 
    {
        
        super( policies, poa ) ;

        
        if (!policies.useActiveMapOnly()) {
            throw wrapper.policyMediatorBadPolicyInFactory();
        }
    }
    
    protected java.lang.Object internalGetServant( byte[] id, 
        String operation ) throws ForwardRequest
    {
        poa.lock() ;
        try {
            java.lang.Object servant = internalIdToServant( id ) ;
            if (servant == null) {
                servant = new NullServantImpl(wrapper.nullServant());
            }
            return servant ;
        } finally {
            poa.unlock() ;
        }
    }

    public void etherealizeAll() {      
        
    }

    public ServantManager getServantManager() throws WrongPolicy {
        throw new WrongPolicy();
    }

    public void setServantManager( ServantManager servantManager ) 
        throws WrongPolicy {
        throw new WrongPolicy();
    }

    public Servant getDefaultServant() throws NoServant, WrongPolicy {
        throw new WrongPolicy();
    }

    public void setDefaultServant( Servant servant ) throws WrongPolicy {
        throw new WrongPolicy();
    }

    public Servant idToServant( byte[] id ) 
        throws WrongPolicy, ObjectNotActive {
        Servant s = internalIdToServant( id ) ; 

        if (s == null) {
            throw new ObjectNotActive();
        } else {
            return s;
        }
    }
}
