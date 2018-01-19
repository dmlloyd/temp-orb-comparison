

package com.sun.corba.ee.impl.encoding;


@CdrWrite
public class CDROutputStream_1_2 extends CDROutputStream_1_1
{
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    protected boolean primitiveAcrossFragmentedChunk = false;

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    protected boolean specialChunk = false;

    
    
    
    
    private boolean headerPadding;

    @InfoMethod
    private void specialChunkCase() { }

    @CdrWrite
    @Override
    protected void handleSpecialChunkBegin(int requiredSize) {
        
        if (inBlock && requiredSize + byteBuffer.position() > byteBuffer.limit()) {
            specialChunkCase() ;

            
            

            int oldSize = byteBuffer.position();
            byteBuffer.position(blockSizeIndex - 4);

            
            writeLongWithoutAlign((oldSize - blockSizeIndex) + requiredSize);
            byteBuffer.position(oldSize);

            
            
            specialChunk = true;
        }
    }

    @CdrWrite
    @Override
    protected void handleSpecialChunkEnd() {
        
        if (inBlock && specialChunk) {
            specialChunkCase();

            
            
            
            inBlock = false;
            blockSizeIndex = -1;
            blockSizePosition = -1;

            
            
            start_block();

            
            
            
            specialChunk = false;
        }
    }
    
    
    @CdrWrite
    private void checkPrimitiveAcrossFragmentedChunk()
    {
        if (primitiveAcrossFragmentedChunk) {
            primitiveAcrossFragmentedChunk = false;

            inBlock = false;

            
            
            
            blockSizeIndex = -1;
            blockSizePosition = -1;

            
            start_block();
        }
    }


    @Override
    public void write_octet(byte x) {
        super.write_octet(x);
        checkPrimitiveAcrossFragmentedChunk();
    }

    @Override
    public void write_short(short x) {
        super.write_short(x);
        checkPrimitiveAcrossFragmentedChunk();
    }

    @Override
    public void write_long(int x) {
        super.write_long(x);
        checkPrimitiveAcrossFragmentedChunk();
    }

    @Override
    public void write_longlong(long x) {
        super.write_longlong(x);
        checkPrimitiveAcrossFragmentedChunk();
    }

    
    @Override
    void setHeaderPadding(boolean headerPadding) {
        this.headerPadding = headerPadding;
    }

    @Override
    @CdrWrite
    protected void alignAndReserve(int align, int n) {

        
        
        
        
        
        
        if (headerPadding == true) {
            headerPadding = false;
            alignOnBoundary(ORBConstants.GIOP_12_MSG_BODY_ALIGNMENT);
        }
        
        
        
        
        
        
        

        byteBuffer.position(byteBuffer.position() + computeAlignment(align));

        if (byteBuffer.position() + n  > byteBuffer.limit())
            grow(align, n);
    }

    @InfoMethod
    private void outOfSequenceWrite() { }

    @InfoMethod
    private void handlingFragmentCase() { }


    @Override
    @CdrWrite
    protected void grow(int align, int n) {
        
        
        int oldSize = byteBuffer.position();

        
        
        
        
        
        
        
        
        
        boolean handleChunk = (inBlock && !specialChunk);
        if (handleChunk) {
            int oldIndex = byteBuffer.position();

            byteBuffer.position(blockSizeIndex - 4);

            writeLongWithoutAlign((oldIndex - blockSizeIndex) + n);

            byteBuffer.position(oldIndex);
        }

        byteBuffer = bufferManagerWrite.overflow(byteBuffer, n);

        
        
        
        if (bufferManagerWrite.isFragmentOnOverflow()) {

            
            
            
            
            fragmentOffset += (oldSize - byteBuffer.position());

            
            
            if (handleChunk)
                primitiveAcrossFragmentedChunk = true;
        }
    }

    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_2;
    }

    @Override
    public void write_wchar(char x)
    {
        
        
        
        
        
        
        
        
        CodeSetConversion.CTBConverter converter = getWCharConverter();

        converter.convert(x);

        handleSpecialChunkBegin(1 + converter.getNumBytes());

        write_octet((byte)converter.getNumBytes());

        byte[] result = converter.getBytes();

        
        
        internalWriteOctetArray(result, 0, converter.getNumBytes());

        handleSpecialChunkEnd();
    }

    @Override
    public void write_wchar_array(char[] value, int offset, int length)
    {
        if (value == null) {
            throw wrapper.nullParam();
        }   

        CodeSetConversion.CTBConverter converter = getWCharConverter();

        
        
        
        
        int totalNumBytes = 0;

        
        
        int maxLength = (int)Math.ceil(converter.getMaxBytesPerChar() * length);
        byte[] buffer = new byte[maxLength + length];

        for (int i = 0; i < length; i++) {
            
            converter.convert(value[offset + i]);

            
            buffer[totalNumBytes++] = (byte)converter.getNumBytes();

            
            System.arraycopy(converter.getBytes(), 0,
                             buffer, totalNumBytes,
                             converter.getNumBytes());

            totalNumBytes += converter.getNumBytes();
        }

        
        
        
        handleSpecialChunkBegin(totalNumBytes);

        
        
        internalWriteOctetArray(buffer, 0, totalNumBytes);

        handleSpecialChunkEnd();
    }    

    @Override
    public void write_wstring(String value) {
        if (value == null) {
            throw wrapper.nullParam();
        }

        
        
        
        
        if (value.length() == 0) {
            write_long(0);
            return;
        }

        CodeSetConversion.CTBConverter converter = getWCharConverter();

        converter.convert(value);

        handleSpecialChunkBegin(computeAlignment(4) + 4 + converter.getNumBytes());

        write_long(converter.getNumBytes());

        
        internalWriteOctetArray(converter.getBytes(), 0, converter.getNumBytes());

        handleSpecialChunkEnd();
    }
}
