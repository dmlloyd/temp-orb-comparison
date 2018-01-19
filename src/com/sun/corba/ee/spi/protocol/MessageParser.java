


package xxxx;






public interface MessageParser {

    @Transport
    ByteBuffer getNewBufferAndCopyOld(ByteBuffer byteBuffer);

    
    boolean isExpectingMoreData();

    
    
    
    
    Message parseBytes(ByteBuffer byteBuffer, Connection connection);

    
    boolean hasMoreBytesToParse();

    
    void setNextMessageStartPosition(int position);

    
    int getNextMessageStartPosition();

    
    int getSizeNeeded();

    
    ByteBuffer getMsgByteBuffer();

    
    void offerBuffer(ByteBuffer buffer);

    
    ByteBuffer getRemainderBuffer();

    
    MessageMediator getMessageMediator();

    
    void checkTimeout(long timeSinceLastInput);

    boolean isExpectingFragments();
}
