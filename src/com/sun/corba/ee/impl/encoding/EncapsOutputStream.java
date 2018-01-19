


package com.sun.corba.ee.impl.encoding;

import com.sun.corba.ee.spi.orb.ORB;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;

import com.sun.corba.ee.spi.misc.ORBConstants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class EncapsOutputStream extends CDROutputObject
{

    
    
    
    
    
    final static boolean usePooledByteBuffers = false;
    private static final InputObjectFactory INPUT_STREAM_FACTORY = new EncapsInputStreamFactory();

    
    
    

    
    
    
    
    
    
    public EncapsOutputStream(ORB orb) {
        
        
        this(orb, GIOPVersion.V1_2);
    }

    
    
    
    
    public EncapsOutputStream(ORB orb, GIOPVersion version) {
        super(orb, version, BufferManagerFactory.newWriteEncapsulationBufferManager(orb),
              ORBConstants.STREAM_FORMAT_VERSION_1, usePooledByteBuffers
        );
    }

    @Override
    public org.omg.CORBA.portable.InputStream create_input_stream() {
        freeInternalCaches();
        return createInputObject(null, INPUT_STREAM_FACTORY);
    }

    private static class EncapsInputStreamFactory implements InputObjectFactory {
        @Override
        public CDRInputObject createInputObject(CDROutputObject outputObject, ORB orb, ByteBuffer byteBuffer, int size, GIOPVersion giopVersion) {
            return com.sun.corba.ee.impl.encoding.EncapsInputStreamFactory.newEncapsInputStream(outputObject.orb(),
            		byteBuffer, size, ByteOrder.BIG_ENDIAN, giopVersion);
        }
    }
    
    @Override
    protected CodeSetConversion.CTBConverter createCharCTBConverter() {
        return CodeSetConversion.impl().getCTBConverter(
            OSFCodeSetRegistry.ISO_8859_1);
    }

    @Override
    protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
        if (getGIOPVersion().equals(GIOPVersion.V1_0))
            throw wrapper.wcharDataInGiop10();            

        
        
        
        if (getGIOPVersion().equals(GIOPVersion.V1_1))
            return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, false, false);

        
        
        
        

        boolean useBOM = ((ORB)orb()).getORBData().useByteOrderMarkersInEncapsulations();

        return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, false, useBOM);
    }
}
