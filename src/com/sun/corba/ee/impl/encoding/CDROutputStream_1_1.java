

package com.sun.corba.ee.impl.encoding;


public class CDROutputStream_1_1 extends CDROutputStream_1_0
{
    
    
    
    
    
    
    
    
    
    
    
    
    
    protected int fragmentOffset = 0;

    @Override
    protected void alignAndReserve(int align, int n) {

        
        
        
        
        
        
        

        int alignment = computeAlignment(align);

        if (byteBuffer.position() + n + alignment > byteBuffer.limit()) {
            grow(align, n);

            
            
            

            
            
            
            alignment = computeAlignment(align);
        }

        byteBuffer.position(byteBuffer.position() + alignment);
    }

    @Override
    protected void grow(int align, int n) {
        
        int oldSize = byteBuffer.position();

        byteBuffer = bufferManagerWrite.overflow(byteBuffer, n);

        
        
        
        if (bufferManagerWrite.isFragmentOnOverflow()) {

            
            
            
            
            fragmentOffset += (oldSize - byteBuffer.position());
        }
    }

    @Override
    public int get_offset() {
        return byteBuffer.position() + fragmentOffset;
    }

    @Override
    public GIOPVersion getGIOPVersion() {
        return GIOPVersion.V1_1;
    }

    @Override
    public void write_wchar(char x)
    {
        
        
        
        
        CodeSetConversion.CTBConverter converter = getWCharConverter();

        converter.convert(x);

        if (converter.getNumBytes() != 2)
            throw wrapper.badGiop11Ctb();

        alignAndReserve(converter.getAlignment(),
                        converter.getNumBytes());

        parent.write_octet_array(converter.getBytes(),
                                 0,
                                 converter.getNumBytes());
    }

    @Override
    public void write_wstring(String value)
    {
        if (value == null) {
            throw wrapper.nullParam();
        }

        
        

        int len = value.length() + 1;

        write_long(len);

        CodeSetConversion.CTBConverter converter = getWCharConverter();

        converter.convert(value);

        internalWriteOctetArray(converter.getBytes(), 0, converter.getNumBytes());

        
        write_short((short)0);
    }
}

