


package xxxx;





public final class TypeCodeOutputStream extends EncapsOutputStream {

    private static final InputObjectFactory TYPE_CODE_INPUT_OBJECT_FACTORY = new TypeCodeInputStreamFactory();

    private OutputStream enclosure = null;
    private Map<String,Integer> typeMap = null;
    private boolean isEncapsulation = false;

    public TypeCodeOutputStream(ORB orb) {
        super(orb);
    }

    @Override
    public org.omg.CORBA.portable.InputStream create_input_stream() {
        return createInputObject(null, TYPE_CODE_INPUT_OBJECT_FACTORY);
    }

    private static class TypeCodeInputStreamFactory implements InputObjectFactory {
        @Override
        public CDRInputObject createInputObject(CDROutputObject outputObject, ORB orb, ByteBuffer byteBuffer, int size, GIOPVersion giopVersion) {
            return EncapsInputStreamFactory.newTypeCodeInputStream(outputObject.orb(), byteBuffer, size, ByteOrder.BIG_ENDIAN, giopVersion);
        }
    }

    public void setEnclosingOutputStream(OutputStream enclosure) {
        this.enclosure = enclosure;
    }

    public TypeCodeOutputStream getTopLevelStream() {
        if (enclosure == null)
            return this;
        if (enclosure instanceof TypeCodeOutputStream)
            return ((TypeCodeOutputStream)enclosure).getTopLevelStream();
        return this;
    }

    public int getTopLevelPosition() {
        if (enclosure != null && enclosure instanceof TypeCodeOutputStream) {
            int pos = ((TypeCodeOutputStream)enclosure).getTopLevelPosition()
                + getPosition();
            
            
            if (isEncapsulation) {
                pos += 4;
            }

            return pos;
        }
        return getPosition();
    }

    public void addIDAtPosition(String id, int position) {
        if (typeMap == null)
            typeMap = new HashMap<String,Integer>(16);
        typeMap.put(id, position);
    }

    public int getPositionForID(String id) {
        if (typeMap == null)
            throw wrapper.refTypeIndirType() ;
        return
            typeMap.get(id) ;
    }

    public TypeCodeOutputStream createEncapsulation(org.omg.CORBA.ORB _orb) {
        TypeCodeOutputStream encap = OutputStreamFactory.newTypeCodeOutputStream((ORB)_orb);
        encap.setEnclosingOutputStream(this);
        encap.makeEncapsulation();
        return encap;
    }

    protected void makeEncapsulation() {
        
        putEndian();
        isEncapsulation = true;
    }

    public static TypeCodeOutputStream wrapOutputStream(OutputStream os) {
        TypeCodeOutputStream tos = OutputStreamFactory.newTypeCodeOutputStream((ORB)os.orb());
        tos.setEnclosingOutputStream(os);
        return tos;
    }

    public int getPosition() {
        return getIndex();
    }

    @Override
    public int getRealIndex(int index) {
        return getTopLevelPosition();
    }

    public byte[] getTypeCodeBuffer() {
        return toByteArray(4);
    }

}
