


package xxxx;




public class ObjectKeyCacheEntryImpl extends ObjectKeyCacheEntryBase {
    private volatile ObjectAdapter oa ;

    public ObjectKeyCacheEntryImpl( ObjectKey okey ) {
        super( okey ) ;
        oa = null ;
    }

    public ObjectAdapter getObjectAdapter() {
        return oa ;
    }

    public void clearObjectAdapter() {
        oa = null ;
    }

    public void setObjectAdapter( ObjectAdapter oa ) {
        this.oa = oa ;
    }
}
