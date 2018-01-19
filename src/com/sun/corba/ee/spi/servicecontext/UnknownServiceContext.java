


package com.sun.corba.ee.spi.servicecontext;

import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.servicecontext.ServiceContext ;

public interface UnknownServiceContext extends ServiceContext
{
    public byte[] getData() ;
}
