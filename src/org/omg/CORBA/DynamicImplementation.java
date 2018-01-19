

package xxxx;



@Deprecated
public class DynamicImplementation extends org.omg.CORBA.portable.ObjectImpl {

    
    @Deprecated
    public void invoke(ServerRequest request) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public String[] _ids() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}
