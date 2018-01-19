

package com.sun.corba.se.impl.encoding;

import java.nio.ByteBuffer;
import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

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
