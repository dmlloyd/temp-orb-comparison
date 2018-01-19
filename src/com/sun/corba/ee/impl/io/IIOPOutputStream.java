



package xxxx;






@ValueHandlerWrite
public class IIOPOutputStream
    extends com.sun.corba.ee.impl.io.OutputStreamHook
{
    private static Bridge bridge = 
        AccessController.doPrivileged(
            new PrivilegedAction<Bridge>() {
                public Bridge run() {
                    return Bridge.get() ;
                }
            } 
        ) ;

    private org.omg.CORBA_2_3.portable.OutputStream orbStream;

    private Object currentObject = null;

    private ObjectStreamClass currentClassDesc = null;

    private int recursionDepth = 0;

    private int simpleWriteDepth = 0;

    private IOException abortIOException = null;

    private Stack<ObjectStreamClass> classDescStack = 
        new Stack<ObjectStreamClass>();

    public IIOPOutputStream()
        throws java.io.IOException
   {
        super();
    }

    
    
    
    
    @ValueHandlerWrite
    protected void beginOptionalCustomData() {
        if (streamFormatVersion == 2) {

            org.omg.CORBA.portable.ValueOutputStream vout
                = (org.omg.CORBA.portable.ValueOutputStream)orbStream;

            vout.start_value(currentClassDesc.getRMIIIOPOptionalDataRepId());
        }
    }

    final void setOrbStream(org.omg.CORBA_2_3.portable.OutputStream os) {
        orbStream = os;
    }

    final org.omg.CORBA_2_3.portable.OutputStream getOrbStream() {
        return orbStream;
    }

    @InfoMethod
    private void recursionDepthInfo( int rd ) {}

    @ValueHandlerWrite
    final void increaseRecursionDepth(){
        recursionDepth++;
        recursionDepthInfo( recursionDepth ) ;
    }

    @ValueHandlerWrite
    final int decreaseRecursionDepth(){
        --recursionDepth;
        recursionDepthInfo(recursionDepth);
        return recursionDepth ;
    }

    @ValueHandlerWrite
    private void writeFormatVersion() {
        orbStream.write_octet(streamFormatVersion);
    }

    
    @ValueHandlerWrite
    @Override
    public final void writeObjectOverride(Object obj)
        throws IOException {

        writeObjectState.writeData(this);

        Util.getInstance().writeAbstractObject((OutputStream)orbStream, obj);
    }

    
    @ValueHandlerWrite
    public final void simpleWriteObject(Object obj, byte formatVersion) {
        byte oldStreamFormatVersion = streamFormatVersion;

        streamFormatVersion = formatVersion;

        Object prevObject = currentObject;
        ObjectStreamClass prevClassDesc = currentClassDesc;
        simpleWriteDepth++;

        try {
            
            outputObject(obj);

        } catch (IOException ee) {
            if (abortIOException == null) {
                abortIOException = ee;
            }
        } finally {
            
            streamFormatVersion = oldStreamFormatVersion;
            simpleWriteDepth--;
            currentObject = prevObject;
            currentClassDesc = prevClassDesc;
        }

        
        IOException pending = abortIOException;
        if (simpleWriteDepth == 0) {
            abortIOException = null;
        }

        if (pending != null) {
            bridge.throwException( pending ) ;
        }
    }

    
    ObjectStreamField[] getFieldsNoCopy() {
        return currentClassDesc.getFieldsNoCopy();
    }

    
    @ValueHandlerWrite
    public final void defaultWriteObjectDelegate()
    
    {
        try {
            if (currentObject == null || currentClassDesc == null) {
                throw new NotActiveException("defaultWriteObjectDelegate");
            }

            ObjectStreamField[] fields =
                currentClassDesc.getFieldsNoCopy();
            if (fields.length > 0) {
                outputClassFields(currentObject, currentClassDesc.forClass(),
                                  fields);
            }
        } catch(IOException ioe) {
            bridge.throwException(ioe);
        }
    }

    
    public final boolean enableReplaceObjectDelegate(boolean enable)
    
    {
        return false;
                
    }


    @Override
    protected final void annotateClass(Class<?> cl) throws IOException{
        throw Exceptions.self.annotateClassNotSupported() ;
    }

    @Override
    public final void close() throws IOException{
        
    }

    @Override
    protected final void drain() throws IOException{
        
    }

    @ValueHandlerWrite
    @Override
    public final void flush() throws IOException{
        try{
            orbStream.flush();
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    protected final Object replaceObject(Object obj) throws IOException{
        throw Exceptions.self.replaceObjectNotSupported() ;
    }

    
    @ValueHandlerWrite
    @Override
    public final void reset() throws IOException{
        try{
            

            if (currentObject != null || currentClassDesc != null) {
                throw new IOException("Illegal call to reset");
            }

            abortIOException = null;

            if (classDescStack == null) {
                classDescStack =
                    new Stack<ObjectStreamClass>();
            } else {
                classDescStack.setSize(0);
            }

        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void write(byte b[]) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_octet_array(b, 0, b.length);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void write(byte b[], int off, int len) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_octet_array(b, off, len);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void write(int data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_octet((byte)(data & 0xFF));
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeBoolean(boolean data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_boolean(data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeByte(int data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_octet((byte)data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeBytes(String data) throws IOException{
        try{
            writeObjectState.writeData(this);

            byte buf[] = data.getBytes();
            orbStream.write_octet_array(buf, 0, buf.length);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeChar(int data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_wchar((char)data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeChars(String data) throws IOException{
        try{
            writeObjectState.writeData(this);

            char buf[] = data.toCharArray();
            orbStream.write_wchar_array(buf, 0, buf.length);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeDouble(double data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_double(data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeFloat(float data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_float(data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeInt(int data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_long(data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeLong(long data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_longlong(data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @ValueHandlerWrite
    @Override
    public final void writeShort(int data) throws IOException{
        try{
            writeObjectState.writeData(this);

            orbStream.write_short((short)data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    @Override
    protected final void writeStreamHeader() throws IOException{
        
    }

    
    protected void internalWriteUTF(org.omg.CORBA.portable.OutputStream stream,
                                    String data) 
    {
        stream.write_wstring(data);
    }

    @ValueHandlerWrite
    @Override
    public final void writeUTF(String data) throws IOException{
        try{
            writeObjectState.writeData(this);

            internalWriteUTF(orbStream, data);
        } catch(Error e) {
            throw new IOException(e) ;
        }
    }

    
    
    private boolean checkSpecialClasses(Object obj) throws IOException {

        
        
        
        

        if (obj instanceof ObjectStreamClass) {
            throw Exceptions.self.serializationObjectStreamClassNotSupported() ;
        }

        return false;
    }

    
    private boolean checkSubstitutableSpecialClasses(Object obj)
        throws IOException
    {
        if (obj instanceof String) {
            orbStream.write_value((java.io.Serializable)obj);
            return true;
        }

        return false;
    }

    
    @ValueHandlerWrite
    private void outputObject(final Object obj) throws IOException{
        currentObject = obj;
        Class<?> currclass = obj.getClass();

        
        currentClassDesc = ObjectStreamClass.lookup(currclass);
        if (currentClassDesc == null) {
            throw Exceptions.self.notSerializable( currclass.getName() ) ;
        }

        
        if (currentClassDesc.isExternalizable()) {
            
            writeFormatVersion() ;

            
            
            
            
            WriteObjectState oldState = writeObjectState ;
            setState( NOT_IN_WRITE_OBJECT ) ;

            try {
                Externalizable ext = (Externalizable)obj;
                ext.writeExternal(this);
            } finally {
                setState(oldState) ;
            }
        } else {
            
            if (currentClassDesc.forClass().getName().equals("java.lang.String")) {
                this.writeUTF((String)obj);
                return;
            }
            int stackMark = classDescStack.size();
            try {
                ObjectStreamClass next;
                while ((next = currentClassDesc.getSuperclass()) != null) {
                    classDescStack.push(currentClassDesc);
                    currentClassDesc = next;
                }

                do {
                    WriteObjectState oldState = writeObjectState;

                    try {
                        setState(NOT_IN_WRITE_OBJECT);

                        if (currentClassDesc.hasWriteObject()) {
                            invokeObjectWriter(currentClassDesc, obj );
                        } else {
                            defaultWriteObjectDelegate();
                        }
                    } finally {
                        setState(oldState);
                    }
                } while (classDescStack.size() > stackMark &&
                    (currentClassDesc = classDescStack.pop()) != null);
            } finally {
                classDescStack.setSize(stackMark);
            }
        }
    }

    
    @ValueHandlerWrite
    private void invokeObjectWriter(ObjectStreamClass osc, Object obj)
        throws IOException {

        Class<?> c = osc.forClass() ;

        try {
            
            writeFormatVersion() ;

            writeObjectState.enterWriteObject(this);

            try {
                osc.getWriteObjectMethod().invoke( obj, this ) ;
            } finally {
                writeObjectState.exitWriteObject(this);
            }
        } catch (Throwable t) {
            if (t instanceof IOException) {
                throw (IOException) t;
            } else if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else if (t instanceof Error) {
                throw (Error) t;
            } else {
                throw new Error("invokeObjectWriter internal error", t);
            }
        }
    }

    
    @ValueHandlerWrite
    void writeField(ObjectStreamField field, Object value) throws IOException {
        switch (field.getTypeCode()) {
            case 'B':
                if (value == null) {
                    orbStream.write_octet((byte) 0);
                } else {
                    orbStream.write_octet(((Byte) value).byteValue());
                }
                break;
            case 'C':
                if (value == null) {
                    orbStream.write_wchar((char) 0);
                } else {
                    orbStream.write_wchar(((Character) value).charValue());
                }
                break;
            case 'F':
                if (value == null) {
                    orbStream.write_float((float) 0);
                } else {
                    orbStream.write_float(((Float) value).floatValue());
                }
                break;
            case 'D':
                if (value == null) {
                    orbStream.write_double((double) 0);
                } else {
                    orbStream.write_double(((Double) value).doubleValue());
                }
                break;
            case 'I':
                if (value == null) {
                    orbStream.write_long(0);
                } else {
                    orbStream.write_long(((Integer) value).intValue());
                }
                break;
            case 'J':
                if (value == null) {
                    orbStream.write_longlong((long) 0);
                } else {
                    orbStream.write_longlong(((Long) value).longValue());
                }
                break;
            case 'S':
                if (value == null) {
                    orbStream.write_short((short) 0);
                } else {
                    orbStream.write_short(((Short) value).shortValue());
                }
                break;
            case 'Z':
                if (value == null) {
                    orbStream.write_boolean(false);
                } else {
                    orbStream.write_boolean(((Boolean) value).booleanValue());
                }
                break;
            case '[':
            case 'L':
                
                writeObjectField(field, value);
                break;
            default:
                throw Exceptions.self.invalidClassForWrite(
                    currentClassDesc.getName());
            }
    }

    @ValueHandlerWrite
    private void writeObjectField(ObjectStreamField field,
                                  Object objectValue) throws IOException {

        if (ObjectStreamClassCorbaExt.isAny(field.getTypeString())) {
            Util.getInstance().writeAny(orbStream, objectValue);
        }
        else {
            Class<?> type = field.getType();
            int callType = ValueHandlerImpl.kValueType;
            ClassInfoCache.ClassInfo cinfo = field.getClassInfo() ;

            if (cinfo.isInterface()) { 
                String className = type.getName();
                
                if (cinfo.isARemote(type)) {
                    
                    callType = ValueHandlerImpl.kRemoteType;
                } else if (cinfo.isACORBAObject(type)) {
                    
                    callType = ValueHandlerImpl.kRemoteType;
                } else if (RepositoryId.isAbstractBase(type)) {
                    
                    callType = ValueHandlerImpl.kAbstractType;
                } else if (ObjectStreamClassCorbaExt.isAbstractInterface(type)) {
                    callType = ValueHandlerImpl.kAbstractType;
                }
            }
                                        
            switch (callType) {
            case ValueHandlerImpl.kRemoteType: 
                Util.getInstance().writeRemoteObject(orbStream, objectValue);
                break;
            case ValueHandlerImpl.kAbstractType: 
                Util.getInstance().writeAbstractObject(orbStream, objectValue);
                break;
            case ValueHandlerImpl.kValueType:
                try{
                    orbStream.write_value((java.io.Serializable)objectValue, 
                        type);
                } catch(ClassCastException cce){
                    if (objectValue instanceof java.io.Serializable) {
                        throw cce;
                    } else {
                        Utility.throwNotSerializableForCorba(objectValue.getClass().
                            getName());
                    }
                }
            }
        }
    }

    
    @ValueHandlerWrite
    private void outputClassFields(Object o, Class cl,
                                   ObjectStreamField[] fields)
        throws IOException, InvalidClassException {

        
        
        
        
        

        for (int i = 0; i < fields.length; i++) {
            ObjectStreamField field = fields[i] ;
            final long offset = field.getFieldID() ;
            if (offset == Bridge.INVALID_FIELD_OFFSET) {
                throw new InvalidClassException(cl.getName(),
                    "Nonexistent field " + fields[i].getName());
            }
            switch (field.getTypeCode()) {
                case 'B':
                    byte byteValue = bridge.getByte( o, offset ) ;
                    orbStream.write_octet(byteValue);
                    break;
                case 'C':
                    char charValue = bridge.getChar( o, offset ) ;
                    orbStream.write_wchar(charValue);
                    break;
                case 'F':
                    float floatValue = bridge.getFloat( o, offset ) ;
                    orbStream.write_float(floatValue);
                    break;
                case 'D' :
                    double doubleValue = bridge.getDouble( o, offset ) ;
                    orbStream.write_double(doubleValue);
                    break;
                case 'I':
                    int intValue = bridge.getInt( o, offset ) ;
                    orbStream.write_long(intValue);
                    break;
                case 'J':
                    long longValue = bridge.getLong( o, offset ) ;
                    orbStream.write_longlong(longValue);
                    break;
                case 'S':
                    short shortValue = bridge.getShort( o, offset ) ;
                    orbStream.write_short(shortValue);
                    break;
                case 'Z':
                    boolean booleanValue = bridge.getBoolean( o, offset ) ;
                    orbStream.write_boolean(booleanValue);
                    break;
                case '[':
                case 'L':
                    Object objectValue = bridge.getObject( o, offset ) ;
                    writeObjectField(fields[i], objectValue);
                    break;
                default:
                    throw Exceptions.self.invalidClassForWrite(
                        cl.getName() ) ;
            }
        }
    }
}

