

package com.sun.corba.ee.impl.encoding;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_1 extends CDRInputStream_1_0
{
    
    protected int fragmentOffset = 0;

    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_1;
    }

    
    @Override
    public CDRInputStreamBase dup() {
        CDRInputStreamBase result = super.dup();

        ((CDRInputStream_1_1)result).fragmentOffset = this.fragmentOffset;

        return result;
    }

    @Override
    protected int get_offset() {
        return byteBuffer.position() + fragmentOffset;
    }

    @Override
    protected void alignAndCheck(int align, int n) {


        checkBlockLength(align, n);

        
        
        int alignment = computeAlignment(byteBuffer.position(), align);

        if (byteBuffer.position() + n + alignment  > byteBuffer.limit()) {

            
            
            
            if (byteBuffer.position() + alignment == byteBuffer.limit())
            {
                byteBuffer.position(byteBuffer.position() + alignment);
            }

            grow(align, n);

            
            
            

            alignment = computeAlignment(byteBuffer.position(), align);
        }

        byteBuffer.position(byteBuffer.position() + alignment);
    }

    
    
    
    @Override
    protected void grow(int align, int n) {

        
        
        int oldSize = byteBuffer.position();

        byteBuffer = bufferManagerRead.underflow(byteBuffer);

        if (bufferManagerRead.isFragmentOnUnderflow()) {
            
            
            
            
            
            fragmentOffset += (oldSize - byteBuffer.position());

            markAndResetHandler.fragmentationOccured(byteBuffer);
        }
    }

    

    private class FragmentableStreamMemento extends StreamMemento
    {
        private int fragmentOffset_;

        public FragmentableStreamMemento()
        {
            super();

            fragmentOffset_ = fragmentOffset;
        }
    }

    @Override
    public java.lang.Object createStreamMemento() {
        return new FragmentableStreamMemento();
    }

    @Override
    public void restoreInternalState(java.lang.Object streamMemento) 
    {
        super.restoreInternalState(streamMemento);

        fragmentOffset 
            = ((FragmentableStreamMemento)streamMemento).fragmentOffset_;
    }

    

    @Override
    public char read_wchar() {
        
        
        
        
        alignAndCheck(2, 2);

        
        
        char[] result = getConvertedChars(2, getWCharConverter());

        
        
        
        
        if (getWCharConverter().getNumChars() > 1)
            throw wrapper.btcResultMoreThanOneChar() ;

        return result[0];
    }

    @Override
    public String read_wstring() {
        
        
        int len = read_long();

        
        
        
        if (len == 0)
            return newEmptyString();

        checkForNegativeLength(len);

        
        
        
        
        len = len - 1;

        char[] result = getConvertedChars(len * 2, getWCharConverter());

        
        read_short();

        return new String(result, 0, getWCharConverter().getNumChars());
    }

}
