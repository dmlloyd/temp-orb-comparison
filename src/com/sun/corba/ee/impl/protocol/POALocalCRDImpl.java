


package com.sun.corba.ee.impl.protocol;

import org.omg.CORBA.portable.ServantObject;

import com.sun.corba.ee.spi.oa.ObjectAdapter;
import com.sun.corba.ee.spi.oa.OAInvocationInfo ;
import com.sun.corba.ee.spi.oa.OADestroyed;

import com.sun.corba.ee.spi.orb.ORB;

import com.sun.corba.ee.spi.ior.IOR ;
import com.sun.corba.ee.spi.trace.Subcontract;

@Subcontract
public class POALocalCRDImpl extends LocalClientRequestDispatcherBase {

    public POALocalCRDImpl( ORB orb, int scid, IOR ior) {
        super( orb, scid, ior );
    }

    @Subcontract
    private OAInvocationInfo servantEnter( ObjectAdapter oa ) throws OADestroyed {
        oa.enter() ;

        OAInvocationInfo info = oa.makeInvocationInfo( objectId ) ;
        orb.pushInvocationInfo( info ) ;

        return info ;
    }

    @Subcontract
    private void servantExit( ObjectAdapter oa ) {
        try {
            oa.returnServant();
        } finally {
            oa.exit() ;
            orb.popInvocationInfo() ; 
        }
    }

    
    
    
    
    
    
    
    @Subcontract
    @Override
    public ServantObject internalPreinvoke( org.omg.CORBA.Object self,
        String operation, Class expectedType) throws OADestroyed {

        ObjectAdapter oa = null ;

        oa = oaf.find( oaid ) ;

        OAInvocationInfo info = servantEnter( oa ) ;
        info.setOperation( operation ) ;

        try {
            oa.getInvocationServant( info );
            if (!checkForCompatibleServant( info, expectedType )) {
                servantExit( oa ) ;
                return null ;
            }

            return info ;
        } catch (Error err) {
            
            
            servantExit( oa ) ;
            throw err ;
        } catch (RuntimeException re) {
            
            
            servantExit( oa ) ;
            throw re ;
        }
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj) 
    {
        ObjectAdapter oa = orb.peekInvocationInfo().oa() ; 
        servantExit( oa ) ;     
    }
}


