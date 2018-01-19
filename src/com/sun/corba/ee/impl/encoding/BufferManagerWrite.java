


package com.sun.corba.ee.impl.encoding;

import com.sun.corba.ee.spi.orb.ORB;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;

import java.nio.ByteBuffer;


public abstract class BufferManagerWrite
{
    protected ORB orb ;
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    BufferManagerWrite( ORB orb ) 
    {
        this.orb = orb ;
    }

    
    public abstract boolean sentFragment();

    
    public boolean sentFullMessage() {
        return sentFullMessage;
    }

    
    public abstract int getBufferSize();

    
    protected abstract ByteBuffer overflow(ByteBuffer byteBuffer, int numBytesNeeded);

    
    public abstract boolean isFragmentOnOverflow();

    



    public abstract void sendMessage ();


    
    public void setOutputObject(Object outputObject) {
        this.outputObject = outputObject;
    }

    
     abstract public void close();

    
    
    
    protected Object outputObject;

    protected boolean sentFullMessage = false;
}

