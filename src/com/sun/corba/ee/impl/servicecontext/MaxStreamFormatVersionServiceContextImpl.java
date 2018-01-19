


package com.sun.corba.ee.impl.servicecontext;




public class MaxStreamFormatVersionServiceContextImpl extends ServiceContextBase 
    implements MaxStreamFormatVersionServiceContext
{
    private byte maxStreamFormatVersion;

    
    
    public static final MaxStreamFormatVersionServiceContext singleton
        = new MaxStreamFormatVersionServiceContextImpl();

    private MaxStreamFormatVersionServiceContextImpl() 
    {
        maxStreamFormatVersion = ORBUtility.getMaxStreamFormatVersion();
    }

    public MaxStreamFormatVersionServiceContextImpl(byte maxStreamFormatVersion) 
    {
        this.maxStreamFormatVersion = maxStreamFormatVersion;
    }

    public MaxStreamFormatVersionServiceContextImpl(InputStream is, GIOPVersion gv) 
    {
        super(is) ;

        maxStreamFormatVersion = is.read_octet();
    }

    public int getId() 
    { 
        return SERVICE_CONTEXT_ID; 
    }

    public void writeData(OutputStream os) 
    {
        os.write_octet(maxStreamFormatVersion);
    }
    
    public byte getMaximumStreamFormatVersion()
    {
        return maxStreamFormatVersion;
    }

    public String toString() 
    {
        return "MaxStreamFormatVersionServiceContextImpl[" 
            + maxStreamFormatVersion + "]";
    }
}
    



