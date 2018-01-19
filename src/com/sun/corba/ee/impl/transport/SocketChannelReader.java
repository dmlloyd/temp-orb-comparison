package com.sun.corba.ee.impl.transport;

import com.sun.corba.ee.spi.orb.ORB;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelReader {
    private ORB orb;

    public SocketChannelReader(ORB orb) {
        this.orb = orb;
    }

    
    public ByteBuffer read(SocketChannel channel, ByteBuffer previouslyReadData, int minNeeded) throws IOException {
        ByteBuffer byteBuffer = prepareToAppendTo(previouslyReadData);

        int numBytesRead = channel.read(byteBuffer);
        if (numBytesRead < 0) {
            throw new EOFException("End of input detected");
        } else if (numBytesRead == 0) {
            byteBuffer.flip();
            return null;
        }

        while (numBytesRead > 0 && byteBuffer.position() < minNeeded) {
            if (haveFilledBuffer(byteBuffer))
                byteBuffer = expandBuffer(byteBuffer);
            numBytesRead = channel.read(byteBuffer);
        }

        return byteBuffer;
    }

    private ByteBuffer expandBuffer(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        byteBuffer = reallocateBuffer(byteBuffer);
        return byteBuffer;
    }

    private boolean haveFilledBuffer(ByteBuffer byteBuffer) {
        return byteBuffer.position() == byteBuffer.capacity();
    }

    private ByteBuffer prepareToAppendTo(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            byteBuffer = allocateBuffer();
        } else if (byteBuffer.limit() == byteBuffer.capacity()) {
            byteBuffer = reallocateBuffer(byteBuffer);
        } else {
            byteBuffer.position(byteBuffer.limit()).limit(byteBuffer.capacity());
        }
        return byteBuffer;
    }

    private ByteBuffer reallocateBuffer(ByteBuffer byteBuffer) {
        try {
            return orb.getByteBufferPool().reAllocate(byteBuffer, 2*byteBuffer.capacity());
        } finally {
            byteBuffer.position(0); 
        }
    }

    private ByteBuffer allocateBuffer() {
        return orb.getByteBufferPool().getByteBuffer(orb.getORBData().getReadByteBufferSize());
    }

}
