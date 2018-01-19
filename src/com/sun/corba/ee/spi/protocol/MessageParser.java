


package com.sun.corba.ee.spi.protocol;

import java.nio.ByteBuffer;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.trace.Transport;
import com.sun.corba.ee.spi.transport.Connection;

import com.sun.corba.ee.impl.protocol.giopmsgheaders.Message;



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
