


package com.sun.corba.ee.impl.protocol.giopmsgheaders;



public interface CancelRequestMessage extends Message {
    int CANCEL_REQ_MSG_SIZE = 4;
    int getRequestId();
}
