



package com.sun.corba.ee.impl.protocol;

import org.omg.CORBA.portable.ServantObject ;

import com.sun.corba.ee.spi.oa.OAInvocationInfo ;
import com.sun.corba.ee.spi.oa.OADestroyed;

import com.sun.corba.ee.spi.orb.ORB ;

import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcherFactory ;
import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcher ;

import com.sun.corba.ee.spi.ior.IOR;

public class InfoOnlyServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase
{
    public InfoOnlyServantCacheLocalCRDImpl( ORB orb, int scid, IOR ior ) 
    {
        super( (com.sun.corba.ee.spi.orb.ORB)orb, scid, ior ) ;
    }

    public ServantObject internalPreinvoke( org.omg.CORBA.Object self,
        String operation, Class expectedType ) throws OADestroyed
    {
        OAInvocationInfo cachedInfo = getCachedInfo() ;
        if (!checkForCompatibleServant( cachedInfo, expectedType ))
            return null ;

        
        
        
        OAInvocationInfo info =  new OAInvocationInfo(cachedInfo, operation) ;
        orb.pushInvocationInfo( info ) ;

        return info ;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj) 
    {
        orb.popInvocationInfo() ;
    }
}
