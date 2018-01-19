



package xxxx;



@StreamFormatVersion
public abstract class OutputStreamHook extends ObjectOutputStream
{
    private HookPutFields putFields = null;
    
    
    private class HookPutFields extends ObjectOutputStream.PutField {
        private Map<String,Object> fields = new HashMap<String,Object>();

        
        public void put(String name, boolean value){
            fields.put(name, Boolean.valueOf(value));
        }
                
        
        public void put(String name, char value){
            fields.put(name, Character.valueOf(value));
        }
                
        
        public void put(String name, byte value){
            fields.put(name, Byte.valueOf(value));
        }
                
        
        public void put(String name, short value){
            fields.put(name, Short.valueOf(value));
        }
                
        
        public void put(String name, int value){
            fields.put(name, Integer.valueOf(value));
        }
                
        
        public void put(String name, long value){
            fields.put(name, Long.valueOf(value));
        }
                
        
        public void put(String name, float value){
            fields.put(name, Float.valueOf(value));
        }
                
        
        public void put(String name, double value){
            fields.put(name, Double.valueOf(value));
        }
                
        
        public void put(String name, Object value){
            fields.put(name, value);
        }
                
        
        public void write(ObjectOutput out) throws IOException {
            OutputStreamHook hook = (OutputStreamHook)out;

            ObjectStreamField[] osfields = hook.getFieldsNoCopy();

            
            
            
            for (int i = 0; i < osfields.length; i++) {

                Object value = fields.get(osfields[i].getName());

                hook.writeField(osfields[i], value);
            }
        }
    }

    abstract void writeField(ObjectStreamField field, Object value) throws IOException;

    public OutputStreamHook()
        throws java.io.IOException {
        super();
                
    }

    @StreamFormatVersion
    @Override
    public void defaultWriteObject() throws IOException {
        writeObjectState.defaultWriteObject(this);

        defaultWriteObjectDelegate();
    }

    public abstract void defaultWriteObjectDelegate();
        
    @Override
    public ObjectOutputStream.PutField putFields()
        throws IOException {
        putFields = new HookPutFields();
        return putFields;
    }

    
    protected byte streamFormatVersion = 1;

    
    
    public byte getStreamFormatVersion() {
        return streamFormatVersion;
    }

    abstract ObjectStreamField[] getFieldsNoCopy();

    
    
    @Override
    @StreamFormatVersion
    public void writeFields()
        throws IOException {

        writeObjectState.defaultWriteObject(this);

        putFields.write(this);
    }

    abstract org.omg.CORBA_2_3.portable.OutputStream getOrbStream();

    protected abstract void beginOptionalCustomData();


    
    
    
    
    

    protected WriteObjectState writeObjectState = NOT_IN_WRITE_OBJECT;
    
    @StreamFormatVersion
    protected void setState(WriteObjectState newState) {
        writeObjectState = newState;
    }

    protected static final WriteObjectState NOT_IN_WRITE_OBJECT = new DefaultState();
    protected static final WriteObjectState IN_WRITE_OBJECT = new InWriteObjectState();
    protected static final WriteObjectState WROTE_DEFAULT_DATA = new WroteDefaultDataState();
    protected static final WriteObjectState WROTE_CUSTOM_DATA = new WroteCustomDataState();

    
    @StreamFormatVersion
    protected static class WriteObjectState {
        private final String name ;

        public WriteObjectState() {
            String className = this.getClass().getName() ;
            int index = className.indexOf( '$' ) ;
            name = className.substring( index + 1 ) ;
        }

        @StreamFormatVersion
        public final void enterWriteObject(OutputStreamHook stream) throws IOException {
            enterWriteObjectOverride( stream ) ;
        }

        @StreamFormatVersion
        public final void exitWriteObject(OutputStreamHook stream) throws IOException {
            exitWriteObjectOverride( stream ) ;
        }

        @StreamFormatVersion
        public final void defaultWriteObject(OutputStreamHook stream) throws IOException {
            defaultWriteObjectOverride( stream ) ;
        }

        @StreamFormatVersion
        public final void writeData(OutputStreamHook stream) throws IOException {
            writeDataOverride( stream ) ;
        }

        public void enterWriteObjectOverride(OutputStreamHook stream) throws IOException {}
        public void exitWriteObjectOverride(OutputStreamHook stream) throws IOException {}
        public void defaultWriteObjectOverride(OutputStreamHook stream) throws IOException {}
        public void writeDataOverride(OutputStreamHook stream) throws IOException {}

        @Override
        public String toString() {
            return name ;
        }
    }

    @StreamFormatVersion
    protected static class DefaultState extends WriteObjectState {
        @Override
        @StreamFormatVersion
        public void enterWriteObjectOverride(OutputStreamHook stream) throws IOException {
            stream.setState(IN_WRITE_OBJECT);
        }
    }

    @StreamFormatVersion
    protected static class InWriteObjectState extends WriteObjectState {

        @Override
        @StreamFormatVersion
        public void enterWriteObjectOverride(OutputStreamHook stream) throws IOException {
            throw Exceptions.self.calledWriteObjectTwice() ;
        }
        
        @Override
        @StreamFormatVersion
        public void exitWriteObjectOverride(OutputStreamHook stream) throws IOException {

            
            
            stream.getOrbStream().write_boolean(false);

            
            
            
            if (stream.getStreamFormatVersion() == 2) {
                stream.getOrbStream().write_long(0);
            }

            stream.setState(NOT_IN_WRITE_OBJECT);
        }

        @Override
        @StreamFormatVersion
        public void defaultWriteObjectOverride(OutputStreamHook stream) throws IOException {

            
            
            
            stream.getOrbStream().write_boolean(true);

            stream.setState(WROTE_DEFAULT_DATA);
        }

        @Override
        @StreamFormatVersion
        public void writeDataOverride(OutputStreamHook stream) throws IOException {

            
            
            
            
            
            stream.getOrbStream().write_boolean(false);
            stream.beginOptionalCustomData();
            stream.setState(WROTE_CUSTOM_DATA);
        }
    }

    @StreamFormatVersion
    protected static class WroteDefaultDataState extends InWriteObjectState {
        @Override
        @StreamFormatVersion
        public void exitWriteObjectOverride(OutputStreamHook stream) throws IOException {

            
            
            
            if (stream.getStreamFormatVersion() == 2) {
                stream.getOrbStream().write_long(0);
            }
            
            stream.setState(NOT_IN_WRITE_OBJECT);
        }

        @Override
        @StreamFormatVersion
        public void defaultWriteObjectOverride(OutputStreamHook stream) throws IOException {
            throw Exceptions.self.calledDefaultWriteObjectTwice() ;
        }

        @Override
        @StreamFormatVersion
        public void writeDataOverride(OutputStreamHook stream) throws IOException {

            
            
            
            stream.beginOptionalCustomData();
            
            stream.setState(WROTE_CUSTOM_DATA);
        }
    }

    @StreamFormatVersion
    protected static class WroteCustomDataState extends InWriteObjectState {
        @Override
        @StreamFormatVersion
        public void exitWriteObjectOverride(OutputStreamHook stream) throws IOException {
            
            
            if (stream.getStreamFormatVersion() == 2) {
                ((org.omg.CORBA.portable.ValueOutputStream) stream.getOrbStream()).end_value();
            }

            stream.setState(NOT_IN_WRITE_OBJECT);
        }

        @Override
        @StreamFormatVersion
        public void defaultWriteObjectOverride(OutputStreamHook stream) 
            throws IOException {
            throw Exceptions.self.defaultWriteObjectAfterCustomData() ;
        }

        
        
        @Override
        public void writeDataOverride(OutputStreamHook stream) throws IOException {}
    }
}
