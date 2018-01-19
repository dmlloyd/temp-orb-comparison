

package com.sun.corba.ee.impl.protocol.giopmsgheaders;


public interface LocateReplyOrReplyMessage extends Message {

    int getRequestId();
    int getReplyStatus();
    SystemException getSystemException(String message);
    IOR getIOR();
    short getAddrDisposition();
}



