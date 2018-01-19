


package com.sun.corba.ee.impl.transport;



public class ByteBufferPoolImpl implements ByteBufferPool {
    final private static ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private ByteBuffer byteBufferSlab;
    final private boolean useDirectBuffers;
    final private int byteBufferSlabSize;
    final private ORB orb;

    public ByteBufferPoolImpl(ORB orb) {
        this.orb = orb;
        this.useDirectBuffers = !this.orb.getORBData().disableDirectByteBufferUse();
        
        
        
        if (this.useDirectBuffers) {
            this.byteBufferSlabSize = orb.getORBData().getPooledDirectByteBufferSlabSize();
            this.byteBufferSlab = allocateDirectByteBufferSlab();
        } else {
            
            this.byteBufferSlabSize = -1;
            this.byteBufferSlab = null;
        }
    }

    
    public ByteBuffer getByteBuffer(int size) {
        if (useDirectBuffers) {
            if (size > byteBufferSlabSize) {
                
                
                return ByteBuffer.allocate(size);
            }
            synchronized (this) {
                if (byteBufferSlab == null ||
                        (byteBufferSlab.capacity() - byteBufferSlab.limit() < size)) {
                    byteBufferSlab = allocateDirectByteBufferSlab();
                }
                
                byteBufferSlab.limit(byteBufferSlab.position() + size);
                ByteBuffer view = byteBufferSlab.slice();
                byteBufferSlab.position(byteBufferSlab.limit());
                
                return view;
            }
        } else {
            return ByteBuffer.allocate(size);
        }
    }


    public void releaseByteBuffer(ByteBuffer buffer) {
        
        
        
        
    }


    
    
    public int activeCount() {
         return 0;
    }

    
    public ByteBuffer reAllocate(ByteBuffer oldByteBuffer, int minimumSize) {
        int size = orb.getORBData().getReadByteBufferSize();
        while (size <= minimumSize) {
            size *= 2;
        }

        if (size > orb.getORBData().getMaxReadByteBufferSizeThreshold()) {
            if (minimumSize > orb.getORBData().getMaxReadByteBufferSizeThreshold()) {
                throw wrapper.maximumReadByteBufferSizeExceeded(
                      orb.getORBData().getMaxReadByteBufferSizeThreshold(), size, 
                      ORBConstants.MAX_READ_BYTE_BUFFER_SIZE_THRESHOLD_PROPERTY);
            } else {
                
                
                
                size = minimumSize;
            }
        }
        
        ByteBuffer newByteBuffer = getByteBuffer(size);
        
        
        newByteBuffer.put(oldByteBuffer);
        
        return newByteBuffer;
    }

    
    private ByteBuffer allocateDirectByteBufferSlab() {
        return ByteBuffer.allocateDirect(byteBufferSlabSize);
    }
}


