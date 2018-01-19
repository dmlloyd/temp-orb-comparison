


package com.sun.corba.ee.impl.protocol.giopmsgheaders;

import com.sun.corba.ee.spi.orb.ObjectKeyCacheEntry;
import com.sun.corba.ee.spi.servicecontext.ServiceContexts;



public interface RequestMessage extends Message {

    byte RESPONSE_EXPECTED_BIT = 0x01;

    ServiceContexts getServiceContexts();
    void setServiceContexts(ServiceContexts sc);
    int getRequestId();
    boolean isResponseExpected();
    byte[] getReserved();
    ObjectKeyCacheEntry getObjectKeyCacheEntry();
    String getOperation();
    @SuppressWarnings({"deprecation"})
    org.omg.CORBA.Principal getPrincipal();

    
    void setThreadPoolToUse(int poolToUse);


} 
