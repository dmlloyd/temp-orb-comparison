

package xxxx;


public interface BufferManagerRead
{
    

    public void processFragment ( ByteBuffer byteBuffer,
        FragmentMessage header);


    


    

    public ByteBufferWithInfo underflow (ByteBufferWithInfo bbwi);

    
    public void init(Message header);

    
    public MarkAndResetHandler getMarkAndResetHandler();

    
    public void cancelProcessing(int requestId);

    
    public void close(ByteBufferWithInfo bbwi);
}
