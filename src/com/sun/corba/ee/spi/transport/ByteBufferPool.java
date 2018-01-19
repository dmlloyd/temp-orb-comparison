


package xxxx;



public interface ByteBufferPool
{
    public ByteBuffer getByteBuffer(int theSize);
    public void releaseByteBuffer(ByteBuffer thebb);
    public int activeCount();
    
    public ByteBuffer reAllocate(ByteBuffer oldByteBuffer, int minimumSize);
}


