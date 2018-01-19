

package com.sun.corba.ee.impl.encoding;




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
