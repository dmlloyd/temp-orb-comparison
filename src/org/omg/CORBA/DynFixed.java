


package xxxx;


@Deprecated
public interface DynFixed extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    
    public byte[] get_value();

    
    public void set_value(byte[] val)
        throws org.omg.CORBA.DynAnyPackage.InvalidValue;
}
