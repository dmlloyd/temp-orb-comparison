


package com.sun.corba.ee.spi.servicecontext;

import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.impl.encoding.EncapsOutputStream ;
import com.sun.corba.ee.impl.encoding.OutputStreamFactory;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;


public abstract class ServiceContextBase {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    

    private byte[] data;

    protected ServiceContextBase() { }

    
    protected ServiceContextBase(InputStream s) 
    {
        in = s;
    }

    
    public abstract int getId() ;

    
    public synchronized void write(OutputStream s, GIOPVersion gv) throws SystemException {
        if (data == null) {
            EncapsOutputStream os = OutputStreamFactory.newEncapsOutputStream((ORB)(s.orb()), gv);   
            try {
                os.putEndian();
                writeData(os);
                data = os.toByteArray();
            } finally {
                try {
                    os.close();
                } catch (java.io.IOException e) {
                    wrapper.ioexceptionDuringStreamClose(e);
                }
            }
        }
        s.write_long(getId());
        s.write_long(data.length);
        s.write_octet_array(data, 0, data.length);
    }

    
    protected abstract void writeData( OutputStream os ) ;

    
    protected InputStream in = null ;

    @Override
    public String toString() 
    {
        return "ServiceContext[ id=" + getId() + " ]" ;
    } 
}
