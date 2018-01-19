


package com.sun.corba.ee.impl.protocol.giopmsgheaders;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.protocol.RequestId;
import com.sun.corba.ee.impl.protocol.RequestIdImpl;



public final class CancelRequestMessage_1_2 extends Message_1_1
        implements CancelRequestMessage {

    

    private int request_id = (int) 0;

    

    CancelRequestMessage_1_2() {}

    CancelRequestMessage_1_2(int _request_id) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_2, FLAG_NO_FRAG_BIG_ENDIAN,
            Message.GIOPCancelRequest, CANCEL_REQ_MSG_SIZE);
        request_id = _request_id;
    }

    

    public int getRequestId() {
        return this.request_id;
    }

    public RequestId getCorbaRequestId() {
        return new RequestIdImpl(getRequestId());
    }

    

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong(this.request_id);
    }

    public void callback(MessageHandler handler)
        throws java.io.IOException
    {
        handler.handleInput(this);
    }
} 
