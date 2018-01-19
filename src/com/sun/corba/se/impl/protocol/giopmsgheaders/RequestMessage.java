

package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Principal;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;



public interface RequestMessage extends Message {

    byte RESPONSE_EXPECTED_BIT = 0x01;

    ServiceContexts getServiceContexts();
    int getRequestId();
    boolean isResponseExpected();
    byte[] getReserved();
    ObjectKey getObjectKey();
    String getOperation();
    Principal getPrincipal();

    
    void setThreadPoolToUse(int poolToUse);


} 
