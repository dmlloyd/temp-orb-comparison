

package com.sun.corba.ee.impl.encoding;


@CdrRead
public class CDRInputStream_1_2 extends CDRInputStream_1_1
{
    
    
    
    
    protected boolean headerPadding;
    
    
    protected boolean restoreHeaderPadding;

    
    @Override
    void setHeaderPadding(boolean headerPadding) {
        this.headerPadding = headerPadding;
    }

    
    
    
    @Override
    public void mark(int readlimit) {
        super.mark(readlimit);
        restoreHeaderPadding = headerPadding;
    }

    @Override
    public void reset() {
        super.reset();
        headerPadding = restoreHeaderPadding;
        restoreHeaderPadding = false;
    }

    
    
    
    @Override
    public CDRInputStreamBase dup() {
        CDRInputStreamBase result = super.dup();
        ((CDRInputStream_1_2)result).headerPadding = this.headerPadding;
        return result;
    }
    
    @CdrRead
    @Override
    protected void alignAndCheck(int align, int n) {
        
        
        
        
        
        
        
        if (headerPadding == true) {
            headerPadding = false;
            alignOnBoundary(ORBConstants.GIOP_12_MSG_BODY_ALIGNMENT);
        }

        checkBlockLength(align, n);

        
        

        
        
        
        
        
        
        
        
        int savedPosition = byteBuffer.position();
        int alignIncr = computeAlignment(savedPosition,align);
        int bytesNeeded = alignIncr + n;
        if (savedPosition + alignIncr <= byteBuffer.limit()) {
            byteBuffer.position(savedPosition + alignIncr);
        }

        if (savedPosition + bytesNeeded > byteBuffer.limit()) {
            grow(1, n);
        }
    }

    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_2;
    }
        
    @Override
    public char read_wchar() {
        
        
        int numBytes = read_octet();

        char[] result = getConvertedChars(numBytes, getWCharConverter());

        
        
        
        
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

        return new String(getConvertedChars(len, getWCharConverter()),
                          0,
                          getWCharConverter().getNumChars());
    }
}
