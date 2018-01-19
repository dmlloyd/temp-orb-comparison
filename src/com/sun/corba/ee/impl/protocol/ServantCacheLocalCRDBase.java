


package xxxx;





@Subcontract
public abstract class ServantCacheLocalCRDBase 
    extends LocalClientRequestDispatcherBase {

    private OAInvocationInfo cachedInfo ;

    protected ServantCacheLocalCRDBase( ORB orb, int scid, IOR ior )
    {
        super( orb, scid, ior ) ;
    }

    @Subcontract
    protected void cleanupAfterOADestroyed() {
        cachedInfo = null ;
    }

    @Subcontract
    protected synchronized OAInvocationInfo getCachedInfo() throws OADestroyed {
        if (!servantIsLocal) {
            throw poaWrapper.servantMustBeLocal() ;
        }

        if (cachedInfo == null) {
            updateCachedInfo() ;
        }

        return cachedInfo ;
    }

    @Subcontract
    private void updateCachedInfo() throws OADestroyed {
        
        ObjectAdapter oa = oaf.find( oaid ) ;
        cachedInfo = oa.makeInvocationInfo( objectId ) ;
        oa.enter( );

        
        orb.pushInvocationInfo( cachedInfo ) ;

        try {
            oa.getInvocationServant( cachedInfo ) ;
        } catch (ForwardException freq) {
            throw poaWrapper.illegalForwardRequest( freq ) ;
        } finally {
            oa.returnServant();
            oa.exit();
            orb.popInvocationInfo() ;
        }

        return ;
    }
}


