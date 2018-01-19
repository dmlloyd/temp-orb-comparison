

package com.sun.corba.ee.impl.encoding;

import com.sun.corba.ee.impl.protocol.giopmsgheaders.FragmentMessage;
import com.sun.corba.ee.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.ee.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.ee.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.transport.ContactInfoListIterator;
import org.glassfish.pfl.basic.reflection.Bridge;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.RemarshalException;

import java.nio.ByteBuffer;
import java.util.EmptyStackException;


public class BufferManagerWriteStream extends BufferManagerWrite
{
    private int fragmentCount = 0;

    BufferManagerWriteStream( ORB orb )
    {
        super(orb) ;
    }

    public boolean sentFragment() {
        return fragmentCount > 0;
    }

    
    public int getBufferSize() {
        return orb.getORBData().getGIOPFragmentSize();
    }

    protected ByteBuffer overflow(ByteBuffer byteBuffer, int numBytesNeeded) {
        
        MessageBase.setFlag(byteBuffer, Message.MORE_FRAGMENTS_BIT);

        try {
            sendFragment(false);
        } catch (SystemException se) {
            
            
            
            ContactInfoListIterator itr;
            try {
                itr = getContactInfoListIterator();
            } catch (EmptyStackException ese) {
                
                throw se;
            }

            
            orb.getPIHandler().invokeClientPIEndingPoint( ReplyMessage.SYSTEM_EXCEPTION, se ) ;

            boolean retry = itr.reportException(null, se);
            if (retry) {
                Bridge bridge = Bridge.get();
                bridge.throwException(new RemarshalException());
            } else {
                
                throw se;
            }
        }

        

        
        
        
        byteBuffer.position(0);
        byteBuffer.limit(byteBuffer.capacity());

        

        
        

        FragmentMessage header = ((CDROutputObject)outputObject).getMessageHeader().createFragmentMessage();

        header.write(((CDROutputObject)outputObject));
        return byteBuffer;
    }

    @Override
    public boolean isFragmentOnOverflow() {
        return true;
    }

    private void sendFragment(boolean isLastFragment)
    {
        Connection conn = ((CDROutputObject)outputObject).getMessageMediator().getConnection();

        
        
        conn.writeLock();

        try {
            
            conn.sendWithoutLock(((CDROutputObject)outputObject));

            fragmentCount++;

        } finally {

            conn.writeUnlock();
        }

    }

    
    public void sendMessage ()
    {
        sendFragment(true);

        sentFullMessage = true;
    }

    
    public void close(){};

    
    protected ContactInfoListIterator getContactInfoListIterator() {
        return (ContactInfoListIterator) this.orb.getInvocationInfo().getContactInfoListIterator();
    }
}
