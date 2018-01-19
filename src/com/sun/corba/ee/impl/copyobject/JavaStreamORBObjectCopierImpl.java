


package xxxx;






public class JavaStreamORBObjectCopierImpl extends JavaStreamObjectCopierImpl {
    private ORB orb ;

    public JavaStreamORBObjectCopierImpl( ORB orb ) {
        this.orb = orb ;
    }

    public Object copy(Object obj, boolean debug ) {
        return copy( obj ) ;
    }

    @Override
    public Object copy(Object obj) {
        if (obj instanceof Remote) {
            
            
            return Utility.autoConnect(obj,orb,true);
        }

        return super.copy( obj ) ;
    }
}
