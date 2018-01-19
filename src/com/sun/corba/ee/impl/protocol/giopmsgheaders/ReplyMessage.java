


package com.sun.corba.ee.impl.protocol.giopmsgheaders;

import com.sun.corba.ee.spi.servicecontext.ServiceContexts;
import org.omg.CORBA.SystemException;
import com.sun.corba.ee.spi.ior.IOR;



public interface ReplyMessage extends Message, LocateReplyOrReplyMessage {

    
    
    int NO_EXCEPTION = 0;
    int USER_EXCEPTION = 1;
    int SYSTEM_EXCEPTION = 2;
    int LOCATION_FORWARD = 3;
    int LOCATION_FORWARD_PERM = 4;  
    int NEEDS_ADDRESSING_MODE = 5;  

    ServiceContexts getServiceContexts();
    void setIOR( IOR newIOR );
}
