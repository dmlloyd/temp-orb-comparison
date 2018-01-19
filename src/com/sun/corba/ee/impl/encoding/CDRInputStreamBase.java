

package xxxx;





abstract class CDRInputStreamBase extends java.io.InputStream
{
    protected CDRInputObject parent;

    public void setParent(CDRInputObject parent) {
        this.parent = parent;
    }

    abstract void init(org.omg.CORBA.ORB orb,
                       ByteBuffer byteBuffer,
                       int bufferSize,
                       ByteOrder byteOrder,
                       BufferManagerRead bufferManager);

    
    public abstract boolean read_boolean();
    public abstract char read_char();
    public abstract char read_wchar();
    public abstract byte read_octet();
    public abstract short read_short();
    public abstract short read_ushort();
    public abstract int read_long();
    public abstract int read_ulong();
    public abstract long read_longlong();
    public abstract long read_ulonglong();
    public abstract float read_float();
    public abstract double read_double();
    public abstract String read_string();
    public abstract String read_wstring();
    public abstract void read_boolean_array(boolean[] value, int offset, int length);
    public abstract void read_char_array(char[] value, int offset, int length);
    public abstract void read_wchar_array(char[] value, int offset, int length);
    public abstract void read_octet_array(byte[] value, int offset, int length);
    public abstract void read_short_array(short[] value, int offset, int length);
    public abstract void read_ushort_array(short[] value, int offset, int length);
    public abstract void read_long_array(int[] value, int offset, int length);
    public abstract void read_ulong_array(int[] value, int offset, int length);
    public abstract void read_longlong_array(long[] value, int offset, int length);
    public abstract void read_ulonglong_array(long[] value, int offset, int length);
    public abstract void read_float_array(float[] value, int offset, int length);
    public abstract void read_double_array(double[] value, int offset, int length);
    public abstract org.omg.CORBA.Object read_Object();
    public abstract TypeCode read_TypeCode();
    public abstract Any read_any();
    @SuppressWarnings({"deprecation"})
    public abstract org.omg.CORBA.Principal read_Principal();
    public int read() throws java.io.IOException {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
    public abstract java.math.BigDecimal read_fixed();
    public org.omg.CORBA.Context read_Context() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
    public abstract org.omg.CORBA.Object read_Object(java.lang.Class clz);

    public abstract org.omg.CORBA.ORB orb();
    
    public abstract java.io.Serializable read_value();
    public abstract java.io.Serializable read_value(java.lang.Class clz);
    public abstract java.io.Serializable read_value(org.omg.CORBA.portable.BoxedValueHelper factory);
    public abstract java.io.Serializable read_value(java.lang.String rep_id);
    public abstract java.io.Serializable read_value(java.io.Serializable value);
    public abstract java.lang.Object read_abstract_interface();

    public abstract java.lang.Object read_abstract_interface(java.lang.Class clz);
    
    public abstract void consumeEndian();

    public abstract int getPosition();
    
    public abstract java.lang.Object read_Abstract ();
    public abstract java.io.Serializable read_Value ();
    public abstract void read_any_array (org.omg.CORBA.AnySeqHolder seq, int offset, int length);
    public abstract void read_boolean_array (org.omg.CORBA.BooleanSeqHolder seq, int offset, int length);
    public abstract void read_char_array (org.omg.CORBA.CharSeqHolder seq, int offset, int length);
    public abstract void read_wchar_array (org.omg.CORBA.WCharSeqHolder seq, int offset, int length);
    public abstract void read_octet_array (org.omg.CORBA.OctetSeqHolder seq, int offset, int length);
    public abstract void read_short_array (org.omg.CORBA.ShortSeqHolder seq, int offset, int length);
    public abstract void read_ushort_array (org.omg.CORBA.UShortSeqHolder seq, int offset, int length);
    public abstract void read_long_array (org.omg.CORBA.LongSeqHolder seq, int offset, int length);
    public abstract void read_ulong_array (org.omg.CORBA.ULongSeqHolder seq, int offset, int length);
    public abstract void read_ulonglong_array (org.omg.CORBA.ULongLongSeqHolder seq, int offset, int length);
    public abstract void read_longlong_array (org.omg.CORBA.LongLongSeqHolder seq, int offset, int length);
    public abstract void read_float_array (org.omg.CORBA.FloatSeqHolder seq, int offset, int length);

    public abstract void read_double_array (org.omg.CORBA.DoubleSeqHolder seq, int offset, int length);

    
    public abstract String[] _truncatable_ids();

    
    
    
    




    public abstract void mark(int readlimit);

    public abstract void reset();

    
    
    
    
    
    
    
    
    
    
    public boolean markSupported() { return false; }

    
    public abstract CDRInputStreamBase dup();

    
    public abstract java.math.BigDecimal read_fixed(short digits, short scale);

    public abstract ByteOrder getByteOrder();

    
    abstract void setHeaderPadding(boolean headerPadding);

    
    public abstract int getBufferLength();

    public abstract void setBufferLength(int value);

    public abstract void setIndex(int value);

    public abstract void orb(org.omg.CORBA.ORB orb);
    public abstract BufferManagerRead getBufferManager();

    public abstract GIOPVersion getGIOPVersion();

    abstract CodeBase getCodeBase();

    abstract void alignOnBoundary(int octetBoundary);

    abstract void performORBVersionSpecificInit();

    public abstract void resetCodeSetConverters();
    
    public abstract void start_value();

    public abstract void end_value();
}
