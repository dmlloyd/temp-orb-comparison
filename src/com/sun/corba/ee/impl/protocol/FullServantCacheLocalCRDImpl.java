


package xxxx;


@Subcontract
public class FullServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase
{
    public FullServantCacheLocalCRDImpl( ORB orb, int scid, IOR ior ) 
    {
        super( orb, scid, ior ) ;
    }

    @Subcontract
    @Override
    public ServantObject internalPreinvoke( org.omg.CORBA.Object self,
        String operation, Class expectedType ) throws OADestroyed {

        OAInvocationInfo cachedInfo = getCachedInfo() ;
        if (!checkForCompatibleServant( cachedInfo, expectedType )) {
            return null;
        }

        
        
        
        OAInvocationInfo newInfo = new OAInvocationInfo( cachedInfo, operation ) ;
        newInfo.oa().enter() ;
        orb.pushInvocationInfo( newInfo ) ;
        return newInfo ;
    }

    @Subcontract
    public void servant_postinvoke(org.omg.CORBA.Object self,
        ServantObject servantobj) {
        try {
            OAInvocationInfo cachedInfo = getCachedInfo() ;
            cachedInfo.oa().exit() ;
        } catch (OADestroyed oades) {
            caughtOADestroyed() ;
            
            
        } finally {
            orb.popInvocationInfo() ;
        }
    }

    @InfoMethod
    private void caughtOADestroyed() { }
}
