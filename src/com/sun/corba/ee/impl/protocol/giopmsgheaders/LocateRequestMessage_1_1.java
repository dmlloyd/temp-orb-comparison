


package com.sun.corba.ee.impl.protocol.giopmsgheaders;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;

import com.sun.corba.ee.spi.orb.ORB;

import com.sun.corba.ee.spi.orb.ObjectKeyCacheEntry;



public final class LocateRequestMessage_1_1 extends Message_1_1
        implements LocateRequestMessage {

    

    private ORB orb = null;
    private int request_id = (int) 0;
    private byte[] object_key = null;
    private ObjectKeyCacheEntry entry = null;

    

    LocateRequestMessage_1_1(ORB orb) {
        this.orb = orb;
    }

    LocateRequestMessage_1_1(ORB orb, int _request_id, byte[] _object_key) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_1, FLAG_NO_FRAG_BIG_ENDIAN,
            Message.GIOPLocateRequest, 0);
        this.orb = orb;
        request_id = _request_id;
        object_key = _object_key;
    }

    

    public int getRequestId() {
        return this.request_id;
    }

    public ObjectKeyCacheEntry getObjectKeyCacheEntry() {
        if (this.entry == null) {
            
            this.entry = orb.extractObjectKeyCacheEntry(object_key);
        }

        return this.entry;
    }

    

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
        int _len1 = istream.read_long();
        this.object_key = new byte[_len1];
        istream.read_octet_array(this.object_key, 0, _len1);
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong(this.request_id);
        nullCheck(this.object_key);
        ostream.write_long(this.object_key.length);
        ostream.write_octet_array(this.object_key, 0, this.object_key.length);
    }

    public void callback(MessageHandler handler)
        throws java.io.IOException
    {
        handler.handleInput(this);
    }
} 
