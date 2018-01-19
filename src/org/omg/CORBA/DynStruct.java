


package xxxx;


@Deprecated
public interface DynStruct extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    
    public String current_member_name();

    
    public org.omg.CORBA.TCKind current_member_kind();

    
    public org.omg.CORBA.NameValuePair[] get_members();

    
    public void set_members(org.omg.CORBA.NameValuePair[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
