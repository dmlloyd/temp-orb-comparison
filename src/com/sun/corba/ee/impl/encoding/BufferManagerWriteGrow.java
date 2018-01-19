


package com.sun.corba.ee.impl.encoding;

import com.sun.corba.ee.spi.transport.ByteBufferPool;
import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.orb.ORB;

import java.nio.ByteBuffer;

public class BufferManagerWriteGrow extends BufferManagerWrite
{
    BufferManagerWriteGrow( ORB orb )
    {
        super(orb) ;
    }

    public boolean sentFragment() {
        return false;
    }

    
    public int getBufferSize() {
        return orb.getORBData().getGIOPBufferSize();
    }

    @Override
    protected ByteBuffer overflow(ByteBuffer byteBuffer, int numBytesNeeded) {
        int newLength = byteBuffer.limit() * 2;

        while (byteBuffer.position() + numBytesNeeded >= newLength)
            newLength = newLength * 2;

        ByteBufferPool byteBufferPool = orb.getByteBufferPool();
        ByteBuffer newBB = byteBufferPool.getByteBuffer(newLength);

        byteBuffer.flip();
        newBB.put(byteBuffer);

        byteBufferPool.releaseByteBuffer(byteBuffer);
        return newBB;
    }

    @Override
    public boolean isFragmentOnOverflow() {
        return false;
    }

    public void sendMessage () {
        Connection conn =
              ((CDROutputObject)outputObject).getMessageMediator().getConnection();

        conn.writeLock();

        try {

            conn.sendWithoutLock((CDROutputObject)outputObject);

            sentFullMessage = true;

        } finally {

            conn.writeUnlock();
        }
    }

    
    public void close() {}

}
