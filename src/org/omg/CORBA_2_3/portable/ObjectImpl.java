


package xxxx;





public abstract class ObjectImpl extends org.omg.CORBA.portable.ObjectImpl {

    
    public java.lang.String _get_codebase() {
        org.omg.CORBA.portable.Delegate delegate = _get_delegate();
        if (delegate instanceof Delegate)
            return ((Delegate) delegate).get_codebase(this);
        return null;
    }
}
