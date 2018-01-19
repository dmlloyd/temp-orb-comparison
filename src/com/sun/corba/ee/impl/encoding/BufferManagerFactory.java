


package xxxx;





public class BufferManagerFactory {
    private static final ORBUtilSystemException wrapper = ORBUtilSystemException.self ;

    public static final int GROW    = 0;
    public static final int STREAM  = 2;

    
    
    
    public static BufferManagerRead newBufferManagerRead(
            GIOPVersion version, byte encodingVersion, ORB orb) {

        
        
        

        if (encodingVersion != ORBConstants.CDR_ENC_VERSION) {
            return new BufferManagerReadGrow();
        }

        switch (version.intValue()) {
            case GIOPVersion.VERSION_1_0:
                return new BufferManagerReadGrow();
            case GIOPVersion.VERSION_1_1:
            case GIOPVersion.VERSION_1_2:
                
                return new BufferManagerReadStream(orb);
            default:
                
                throw new INTERNAL("Unknown GIOP version: " + version);
        }
    }

    static BufferManagerRead newReadEncapsulationBufferManager() {
        return new BufferManagerReadGrow();
    }

    static BufferManagerWrite newWriteEncapsulationBufferManager(ORB orb) {
        return new BufferManagerWriteGrow(orb);
    }

    static BufferManagerWrite newBufferManagerWrite(int strategy, byte encodingVersion, ORB orb) {
        if (encodingVersion != ORBConstants.CDR_ENC_VERSION) {
            if (strategy != BufferManagerFactory.GROW) {
                throw wrapper.invalidBuffMgrStrategy("newBufferManagerWrite");
            }
            return new BufferManagerWriteGrow(orb);
        }
        switch (strategy) {
            case BufferManagerFactory.GROW:
                return new BufferManagerWriteGrow(orb);
            case BufferManagerFactory.STREAM:
                return new BufferManagerWriteStream(orb);
            default:
                throw new INTERNAL("Unknown buffer manager write strategy: " + strategy);
        }
    }

    public static BufferManagerWrite newBufferManagerWrite(GIOPVersion version, byte encodingVersion, ORB orb) {
        if (encodingVersion != ORBConstants.CDR_ENC_VERSION) {
            return new BufferManagerWriteGrow(orb);
        }
        return newBufferManagerWrite(orb.getORBData().getGIOPBuffMgrStrategy(version), encodingVersion, orb);
    }
}
