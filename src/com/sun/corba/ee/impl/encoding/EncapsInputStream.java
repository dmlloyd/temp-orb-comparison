


package com.sun.corba.ee.impl.encoding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;


public class EncapsInputStream extends CDRInputObject
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public EncapsInputStream(org.omg.CORBA.ORB orb, byte[] buf,
                             int size, ByteOrder byteOrder,
                             GIOPVersion version) {
        super(orb, ByteBuffer.wrap(buf), size, byteOrder, version,
                BufferManagerFactory.newReadEncapsulationBufferManager()
        );

        performORBVersionSpecificInit();
    }

    public EncapsInputStream(org.omg.CORBA.ORB orb, ByteBuffer byteBuffer,
                             int size, ByteOrder byteOrder,
                             GIOPVersion version) {
        super(orb, byteBuffer, size, byteOrder, version,
                BufferManagerFactory.newReadEncapsulationBufferManager()
        );

        performORBVersionSpecificInit();
    }

    
    public EncapsInputStream(org.omg.CORBA.ORB orb, byte[] data, int size) 
    {
        this(orb, data, size, GIOPVersion.V1_2);
    }
    
    
    public EncapsInputStream(EncapsInputStream eis) 
    {
        super(eis);

        performORBVersionSpecificInit();
    }

    
    
    
    
    
    
    public EncapsInputStream(org.omg.CORBA.ORB orb, byte[] data, int size, GIOPVersion version) 
    {
        this(orb, data, size, ByteOrder.BIG_ENDIAN, version);
    }

    
    public EncapsInputStream(org.omg.CORBA.ORB orb, 
                             byte[] data, 
                             int size, 
                             GIOPVersion version, 
                             CodeBase codeBase) {
        super(orb, 
              ByteBuffer.wrap(data), 
              size, 
              ByteOrder.BIG_ENDIAN,
              version,
                BufferManagerFactory.newReadEncapsulationBufferManager()
        ); 

        this.codeBase = codeBase;

        performORBVersionSpecificInit();
    }

    @Override
    public CDRInputObject dup() {
        return EncapsInputStreamFactory.newEncapsInputStream(this);
    }

    @Override
    protected CodeSetConversion.BTCConverter createCharBTCConverter() {
        return CodeSetConversion.impl().getBTCConverter(
            OSFCodeSetRegistry.ISO_8859_1);
    }

    @Override
    protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
        
        if (getGIOPVersion().equals(GIOPVersion.V1_0))
            throw wrapper.wcharDataInGiop10();

        
        
        if (getGIOPVersion().equals(GIOPVersion.V1_1))
            return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, getByteOrder());

        
        
        
        
        
        
        
        return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, ByteOrder.BIG_ENDIAN);
    }

    @Override
    public CodeBase getCodeBase() {
        return codeBase;
    }

    private CodeBase codeBase;
}
