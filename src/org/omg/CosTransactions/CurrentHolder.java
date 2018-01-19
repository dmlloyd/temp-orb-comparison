


package xxxx;


public final class CurrentHolder implements org.omg.CORBA.portable.Streamable
{
    
    public org.omg.CosTransactions.Current value;
    
    public CurrentHolder() {
        this(null);
    }
    public CurrentHolder(org.omg.CosTransactions.Current __arg) {
        value = __arg;
    }

    public void _write(org.omg.CORBA.portable.OutputStream out) {
        org.omg.CosTransactions.CurrentHelper.write(out, value);
    }

    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = org.omg.CosTransactions.CurrentHelper.read(in);
    }

    public org.omg.CORBA.TypeCode _type() {
        return org.omg.CosTransactions.CurrentHelper.type();
    }
}
