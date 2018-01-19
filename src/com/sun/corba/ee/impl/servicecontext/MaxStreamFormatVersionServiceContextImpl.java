


package com.sun.corba.ee.impl.servicecontext;

import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.servicecontext.ServiceContextBase ;
import com.sun.corba.ee.spi.servicecontext.MaxStreamFormatVersionServiceContext ;

import com.sun.corba.ee.impl.misc.ORBUtility;

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
    



