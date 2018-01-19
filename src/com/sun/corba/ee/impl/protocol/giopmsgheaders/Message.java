


package com.sun.corba.ee.impl.protocol.giopmsgheaders;





public interface Message {

    

    static final int defaultBufferSize = 1024;
    static final int GIOPBigMagic =    0x47494F50;
    static final int GIOPMessageHeaderLength = 12;

    

    static final byte LITTLE_ENDIAN_BIT = 0x01;
    static final byte MORE_FRAGMENTS_BIT = 0x02;
    static final byte FLAG_NO_FRAG_BIG_ENDIAN = 0x00;
    static final byte TRAILING_TWO_BIT_BYTE_MASK = 0x3;
    static final byte THREAD_POOL_TO_USE_MASK = 0x3F;

    

    static final byte GIOPRequest = 0;
    static final byte GIOPReply = 1;
    static final byte GIOPCancelRequest = 2;
    static final byte GIOPLocateRequest = 3;
    static final byte GIOPLocateReply = 4;
    static final byte GIOPCloseConnection = 5;
    static final byte GIOPMessageError = 6;
    static final byte GIOPFragment = 7; 

    
    boolean supportsFragments();

    

    GIOPVersion getGIOPVersion();
    byte getEncodingVersion();
    boolean isLittleEndian();
    boolean moreFragmentsToFollow();
    int getType();
    int getSize();

    int getThreadPoolToUse();

    

    void read(org.omg.CORBA.portable.InputStream istream);
    void write(org.omg.CORBA.portable.OutputStream ostream);

    void setSize(ByteBuffer byteBuffer, int size);

    FragmentMessage createFragmentMessage();

    void callback(MessageHandler handler) throws IOException;
    void setEncodingVersion(byte version);
    
    
    RequestId getCorbaRequestId();
}
