

package com.sun.corba.ee.impl.protocol.giopmsgheaders;

import org.omg.CORBA.SystemException;
import com.sun.corba.ee.spi.ior.IOR;

public interface LocateReplyOrReplyMessage extends Message {

    int getRequestId();
    int getReplyStatus();
    SystemException getSystemException(String message);
    IOR getIOR();
    short getAddrDisposition();
}



