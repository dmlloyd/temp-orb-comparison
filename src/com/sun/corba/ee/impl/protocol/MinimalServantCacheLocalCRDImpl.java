



package xxxx;






public class MinimalServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase
{
    public MinimalServantCacheLocalCRDImpl( ORB orb, int scid, IOR ior ) 
    {
        super( (com.sun.corba.ee.spi.orb.ORB)orb, scid, ior ) ;
    }

    public ServantObject internalPreinvoke( org.omg.CORBA.Object self,
        String operation, Class expectedType ) throws OADestroyed
    {
        OAInvocationInfo cachedInfo = getCachedInfo() ;
        if (checkForCompatibleServant( cachedInfo, expectedType ))
            return cachedInfo ;
        else
            return null ;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
                                   ServantObject servantobj) 
    {
    }
}
