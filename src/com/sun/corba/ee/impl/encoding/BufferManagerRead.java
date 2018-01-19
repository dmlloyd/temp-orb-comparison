


package com.sun.corba.ee.impl.encoding;


public interface BufferManagerRead
{
    

    public void processFragment ( ByteBuffer byteBuffer,
        FragmentMessage header);


    


    
    ByteBuffer underflow(ByteBuffer byteBuffer);

    
    boolean isFragmentOnUnderflow();

    
    public void init(Message header);

    
    public MarkAndResetHandler getMarkAndResetHandler();

    
    public void cancelProcessing(int requestId);

    
    public void close(ByteBuffer byteBuffer);
}
