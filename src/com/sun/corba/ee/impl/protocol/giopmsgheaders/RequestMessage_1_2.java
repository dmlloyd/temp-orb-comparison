


package com.sun.corba.ee.impl.protocol.giopmsgheaders;






@Transport
public final class RequestMessage_1_2 extends Message_1_2
        implements RequestMessage {

    private static final ORBUtilSystemException wrapper =
            ORBUtilSystemException.self;

    

    private ORB orb = null;
    private byte response_flags = (byte) 0;
    private byte reserved[] = null;
    private TargetAddress target = null;
    private String operation = null;
    private ServiceContexts service_contexts = null;
    private ObjectKeyCacheEntry entry = null;

    

    RequestMessage_1_2(ORB orb) {
        this.orb = orb;
        this.service_contexts = ServiceContextDefaults.makeServiceContexts(orb);
    }

    RequestMessage_1_2(ORB orb, int _request_id, byte _response_flags,
                       byte[] _reserved, TargetAddress _target,
                       String _operation, ServiceContexts _service_contexts) {
        super(Message.GIOPBigMagic, GIOPVersion.V1_2, FLAG_NO_FRAG_BIG_ENDIAN,
                Message.GIOPRequest, 0);
        this.orb = orb;
        request_id = _request_id;
        response_flags = _response_flags;
        reserved = _reserved;
        target = _target;
        operation = _operation;
        service_contexts = _service_contexts;
    }

    

    public int getRequestId() {
        return this.request_id;
    }

    public boolean isResponseExpected() {
        




        if ((this.response_flags & RESPONSE_EXPECTED_BIT) == RESPONSE_EXPECTED_BIT) {
            return true;
        }

        return false;
    }

    public byte[] getReserved() {
        return this.reserved;
    }

    public ObjectKeyCacheEntry getObjectKeyCacheEntry() {
        if (this.entry == null) {
            
            this.entry = MessageBase.extractObjectKeyCacheEntry(target, orb);
        }

        return this.entry;
    }

    public String getOperation() {
        return this.operation;
    }

    @SuppressWarnings({"deprecation"})
    public org.omg.CORBA.Principal getPrincipal() {
        
        return null;
    }

    public ServiceContexts getServiceContexts() {
        return this.service_contexts;
    }

    public void setServiceContexts(ServiceContexts sc) {
        this.service_contexts = sc;
    }

    

    @Transport
    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.request_id = istream.read_ulong();
        this.response_flags = istream.read_octet();
        this.reserved = new byte[3];
        for (int _o0 = 0; _o0 < (3); ++_o0) {
            this.reserved[_o0] = istream.read_octet();
        }
        this.target = TargetAddressHelper.read(istream);
        getObjectKeyCacheEntry(); 
        this.operation = istream.read_string();
        this.service_contexts = ServiceContextDefaults.makeServiceContexts(
                (org.omg.CORBA_2_3.portable.InputStream) istream);

        
        
        
        
        
        ((CDRInputObject) istream).setHeaderPadding(true);

    }

    @Transport
    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        ostream.write_ulong(this.request_id);
        ostream.write_octet(this.response_flags);
        nullCheck(this.reserved);
        if (this.reserved.length != (3)) {
            throw wrapper.badReservedLength();
        }
        for (int _i0 = 0; _i0 < (3); ++_i0) {
            ostream.write_octet(this.reserved[_i0]);
        }
        nullCheck(this.target);
        TargetAddressHelper.write(ostream, this.target);
        ostream.write_string(this.operation);
        service_contexts.write(
                (org.omg.CORBA_2_3.portable.OutputStream) ostream,
                GIOPVersion.V1_2);

        
        
        
        
        
        ((CDROutputObject) ostream).setHeaderPadding(true);
    }

    public void callback(MessageHandler handler)
            throws java.io.IOException {
        handler.handleInput(this);
    }

    @Override
    public boolean supportsFragments() {
        return true;
    }
} 
