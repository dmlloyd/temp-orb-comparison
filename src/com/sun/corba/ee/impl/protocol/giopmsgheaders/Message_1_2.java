

package xxxx;





public class Message_1_2 extends Message_1_1
{
    protected int request_id = (int) 0;

    Message_1_2() {}
    
    Message_1_2(int _magic, GIOPVersion _GIOP_version, byte _flags,
            byte _message_type, int _message_size) {

        super(_magic,
              _GIOP_version,
              _flags,
              _message_type,
              _message_size);
    }    

    
    public void unmarshalRequestID(ByteBuffer byteBuffer) {
        byteBuffer.order(isLittleEndian() ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        request_id = byteBuffer.getInt(GIOPMessageHeaderLength);
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        if (getEncodingVersion() == ORBConstants.CDR_ENC_VERSION) {
            super.write(ostream);
            return;
        }
        GIOPVersion gv = GIOP_version; 
        GIOP_version = GIOPVersion.getInstance(GIOPVersion.V13_XX.getMajor(),
                                               getEncodingVersion());
        super.write(ostream);
        GIOP_version = gv; 
    }

    public RequestId getCorbaRequestId() {
        return new RequestIdImpl(this.request_id);
    }
}

