



package xxxx;


@SuppressWarnings({"deprecation"})
public class PrincipalImpl extends org.omg.CORBA.Principal
{
    private byte[] value;

    public void name(byte[] value)
    {
        this.value = value.clone();
    }

    public byte[] name()
    {
        return value.clone() ;
    }
}
