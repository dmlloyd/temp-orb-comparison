


package xxxx;



@Deprecated
public interface DynArray extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    
    public org.omg.CORBA.Any[] get_elements();

    
    public void set_elements(org.omg.CORBA.Any[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
