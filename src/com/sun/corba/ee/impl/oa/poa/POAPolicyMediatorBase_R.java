


package com.sun.corba.ee.impl.oa.poa ;

import org.omg.PortableServer.Servant ;

import org.omg.PortableServer.POAPackage.WrongPolicy ;
import org.omg.PortableServer.POAPackage.ServantNotActive ;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive ;
import org.omg.PortableServer.POAPackage.ObjectNotActive ;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive ;

import com.sun.corba.ee.spi.trace.Poa;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;

@Poa
public abstract class POAPolicyMediatorBase_R extends POAPolicyMediatorBase {
    protected ActiveObjectMap activeObjectMap ;
    
    POAPolicyMediatorBase_R( Policies policies, POAImpl poa ) 
    {
        super( policies, poa ) ;

        
        if (!policies.retainServants()) {
            throw wrapper.policyMediatorBadPolicyInFactory();
        }

        activeObjectMap = ActiveObjectMap.create(poa, !isUnique);
    }
    
    public void returnServant() 
    {
        
    }

    public void clearAOM() 
    {
        activeObjectMap.clear() ;
        activeObjectMap = null ;
    }

    protected Servant internalKeyToServant( ActiveObjectMap.Key key )
    {
        AOMEntry entry = activeObjectMap.get(key);
        if (entry == null) {
            return null;
        }

        return activeObjectMap.getServant( entry ) ;
    }

    protected Servant internalIdToServant( byte[] id )
    {
        ActiveObjectMap.Key key = new ActiveObjectMap.Key( id ) ;
        return internalKeyToServant( key ) ;
    }

    @Poa
    protected void activateServant( ActiveObjectMap.Key key, AOMEntry entry, Servant servant )
    {
        setDelegate(servant, key.id() );

        activeObjectMap.putServant( servant, entry ) ;

        POAManagerImpl pm = (POAManagerImpl)poa.the_POAManager() ;
        POAFactory factory = pm.getFactory() ;
        factory.registerPOAForServant(poa, servant);
    }

    @Poa
    public final void activateObject(byte[] id, Servant servant) 
        throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive
    {
        if (isUnique && activeObjectMap.contains(servant)) {
            throw new ServantAlreadyActive();
        }
        ActiveObjectMap.Key key = new ActiveObjectMap.Key( id ) ;

        AOMEntry entry = activeObjectMap.get( key ) ;

        
        entry.activateObject() ;

        activateServant( key, entry, servant ) ;
    }
    
    @Poa
    public Servant deactivateObject( byte[] id ) 
        throws ObjectNotActive, WrongPolicy 
    {
        ActiveObjectMap.Key key = new ActiveObjectMap.Key( id ) ;
        return deactivateObject( key ) ;
    }
    
    @Poa
    protected void deactivateHelper( ActiveObjectMap.Key key, AOMEntry entry, 
        Servant s ) throws ObjectNotActive, WrongPolicy
    {
        
        

        activeObjectMap.remove(key);

        POAManagerImpl pm = (POAManagerImpl)poa.the_POAManager() ;
        POAFactory factory = pm.getFactory() ;
        factory.unregisterPOAForServant(poa, s);
    }

    @InfoMethod
    private void deactivatingObject( Servant s, POAImpl poa ) { }

    @Poa
    public Servant deactivateObject( ActiveObjectMap.Key key ) 
        throws ObjectNotActive, WrongPolicy {

        AOMEntry entry = activeObjectMap.get(key);
        if (entry == null) {
            throw new ObjectNotActive();
        }

        Servant s = activeObjectMap.getServant( entry ) ;
        if (s == null) {
            throw new ObjectNotActive();
        }

        deactivatingObject( s, poa ) ;

        deactivateHelper( key, entry, s ) ;

        return s ;
    }

    @Poa
    public byte[] servantToId( Servant servant ) throws ServantNotActive, WrongPolicy
    {   
        if (!isUnique && !isImplicit) {
            throw new WrongPolicy();
        }

        if (isUnique) {
            ActiveObjectMap.Key key = activeObjectMap.getKey(servant);
            if (key != null) {
                return key.id();
            }
        } 

        
        
        if (isImplicit) {
            try {
                byte[] id = newSystemId();
                activateObject(id, servant);
                return id;
            } catch (ObjectAlreadyActive oaa) {
                throw wrapper.servantToIdOaa(oaa);
            } catch (ServantAlreadyActive s) {
                throw wrapper.servantToIdSaa(s);
            } catch (WrongPolicy w) {
                throw wrapper.servantToIdWp(w);
            }
        }

        throw new ServantNotActive();
    }
}

