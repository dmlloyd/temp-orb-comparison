


package com.sun.corba.ee.impl.protocol.giopmsgheaders;




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
