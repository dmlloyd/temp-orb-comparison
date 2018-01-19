



package com.sun.corba.ee.impl.io;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;

import org.omg.CORBA.portable.ValueInputStream;

import com.sun.corba.ee.spi.orb.ORBVersion;
import com.sun.corba.ee.spi.orb.ORBVersionFactory;
import com.sun.corba.ee.spi.logging.UtilSystemException;
import com.sun.corba.ee.spi.logging.OMGSystemException;

import com.sun.corba.ee.spi.trace.StreamFormatVersion;
import java.util.HashMap;
import java.util.Map;

@StreamFormatVersion
public abstract class InputStreamHook extends ObjectInputStream
{
    
    static final OMGSystemException omgWrapper =
        OMGSystemException.self ;

    static final UtilSystemException utilWrapper =
        UtilSystemException.self ;


    private class HookGetFields extends ObjectInputStream.GetField {
        private Map<String,Object> fields = null;

        HookGetFields(Map<String,Object> fields){
            this.fields = fields;
        }

        
        public java.io.ObjectStreamClass getObjectStreamClass() {
            return null;
        }
                
        
        public boolean defaulted(String name)
            throws IOException, IllegalArgumentException  {
            return (!fields.containsKey(name));
        }
                
        
        public boolean get(String name, boolean defvalue) 
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Boolean) fields.get(name)).booleanValue();
            }
        }
                
        
        public char get(String name, char defvalue) 
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Character) fields.get(name)).charValue();
            }

        }
                
        
        public byte get(String name, byte defvalue) 
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Byte) fields.get(name)).byteValue();
            }

        }
                
        
        public short get(String name, short defvalue) 
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Short) fields.get(name)).shortValue();
            }

        }
                
        
        public int get(String name, int defvalue) 
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Integer) fields.get(name)).intValue();
            }
        }
                
        
        public long get(String name, long defvalue)
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Long) fields.get(name)).longValue();
            }
        }
                
        
        public float get(String name, float defvalue) 
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Float) fields.get(name)).floatValue();
            }
        }
                
        
        public double get(String name, double defvalue) 
            throws IOException, IllegalArgumentException  {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return ((Double) fields.get(name)).doubleValue();
            }
        }
                
        
        public Object get(String name, Object defvalue) 
            throws IOException, IllegalArgumentException {
            if (defaulted(name)) {
                return defvalue;
            } else {
                return fields.get(name);
            }

        }
                
        @Override
        public String toString(){
            return fields.toString();
        }
    }

    public InputStreamHook()
        throws IOException {
        super();
    }

    @Override
    @StreamFormatVersion
    public void defaultReadObject()
        throws IOException, ClassNotFoundException, NotActiveException
    {
        readObjectState.beginDefaultReadObject(this);

        defaultReadObjectDelegate();

        readObjectState.endDefaultReadObject(this);
    }

    abstract void defaultReadObjectDelegate();

    abstract void readFields(java.util.Map<String,Object> fieldToValueMap)
        throws java.io.InvalidClassException, java.io.StreamCorruptedException,
               ClassNotFoundException, java.io.IOException;


    
    
    
    @Override
    public ObjectInputStream.GetField readFields()
        throws IOException, ClassNotFoundException, NotActiveException {

        Map<String,Object> fieldValueMap = new HashMap<String,Object>();

        
        
        
        
        
        
        
        
        
        
        
        

        readFields(fieldValueMap);

        readObjectState.endDefaultReadObject(this);

        return new HookGetFields(fieldValueMap);
    }

    
    
    
    
    
    
    
    
    

    @StreamFormatVersion
    protected void setState(ReadObjectState newState) {
        readObjectState = newState;
    }

    protected abstract byte getStreamFormatVersion();
    abstract org.omg.CORBA_2_3.portable.InputStream getOrbStream();

    
    @StreamFormatVersion
    protected static class ReadObjectState {
        private final String name ;

        public ReadObjectState() {
            String className = this.getClass().getName() ;
            int index = className.indexOf( '$' ) ;
            name = className.substring( index + 1 ) ;
        }

        @StreamFormatVersion
        public final void beginUnmarshalCustomValue(InputStreamHook stream, boolean calledDefaultWriteObject, 
            boolean hasReadObject) throws IOException {
            beginUnmarshalCustomValueOverride( stream, calledDefaultWriteObject, hasReadObject ) ;
        }

        @StreamFormatVersion
        public final void endUnmarshalCustomValue(InputStreamHook stream) throws IOException {
            endUnmarshalCustomValueOverride( stream ) ;
        }

        @StreamFormatVersion
        public final void beginDefaultReadObject(InputStreamHook stream) throws IOException {
            beginDefaultReadObjectOverride( stream ) ;
        }

        @StreamFormatVersion
        public final void endDefaultReadObject(InputStreamHook stream) throws IOException {
            endDefaultReadObjectOverride( stream ) ;
        }

        @StreamFormatVersion
        public final void readData(InputStreamHook stream) throws IOException {
            readDataOverride( stream ) ;
        }

        public void beginUnmarshalCustomValueOverride(InputStreamHook stream, 
            boolean calledDefaultWriteObject, boolean hasReadObject) throws IOException {}
        public void endUnmarshalCustomValueOverride(InputStreamHook stream) throws IOException {}
        public void beginDefaultReadObjectOverride(InputStreamHook stream) throws IOException {}
        public void endDefaultReadObjectOverride(InputStreamHook stream) throws IOException {}
        public void readDataOverride(InputStreamHook stream) throws IOException {}

        @Override
        public String toString() {
            return name ;
        }
    }

    protected ReadObjectState readObjectState = DEFAULT_STATE;
    
    protected static final ReadObjectState DEFAULT_STATE = new DefaultState();
    protected static final ReadObjectState IN_READ_OBJECT_OPT_DATA 
        = new InReadObjectOptionalDataState();
    protected static final ReadObjectState IN_READ_OBJECT_NO_MORE_OPT_DATA
        = new InReadObjectNoMoreOptionalDataState();
    protected static final ReadObjectState IN_READ_OBJECT_DEFAULTS_SENT
        = new InReadObjectDefaultsSentState();
    protected static final ReadObjectState NO_READ_OBJECT_DEFAULTS_SENT
        = new NoReadObjectDefaultsSentState();

    protected static final ReadObjectState IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED
        = new InReadObjectRemoteDidNotUseWriteObjectState();
    protected static final ReadObjectState IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM
        = new InReadObjectPastDefaultsRemoteDidNotUseWOState();

    protected static class DefaultState extends ReadObjectState {

        @Override
        public void beginUnmarshalCustomValueOverride(InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject)
            throws IOException {

            if (hasReadObject) {
                if (calledDefaultWriteObject) {
                    stream.setState(IN_READ_OBJECT_DEFAULTS_SENT);
                } else {
                    try {
                        if (stream.getStreamFormatVersion() == 2) {
                            ((ValueInputStream) stream.getOrbStream())
                                .start_value();
                        }
                    } catch( Exception e ) {
                        
                        
                        
                        
                        
                        
                 
                    }
                    stream.setState(IN_READ_OBJECT_OPT_DATA);
                }
            } else {
                if (calledDefaultWriteObject) {
                    stream.setState(NO_READ_OBJECT_DEFAULTS_SENT);
                } else {
                    throw new StreamCorruptedException("No default data sent");
                }
            }
        }
    }

    
    
    
    protected static class InReadObjectRemoteDidNotUseWriteObjectState extends ReadObjectState {

        @Override
        public void beginUnmarshalCustomValueOverride(InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject) 
        {
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        @Override
        public void endDefaultReadObjectOverride(InputStreamHook stream) {
            stream.setState(IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM);
        }

        @Override
        public void readDataOverride(InputStreamHook stream) {
            stream.throwOptionalDataIncompatibleException();
        }
    }

    protected static class InReadObjectPastDefaultsRemoteDidNotUseWOState extends ReadObjectState {

        @Override
        public void beginUnmarshalCustomValueOverride(InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject)
        {
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        @Override
        public void beginDefaultReadObjectOverride(InputStreamHook stream) throws IOException 
        {
            throw Exceptions.self.defaultDataAlreadyRead() ;
        }


        @Override
        public void readDataOverride(InputStreamHook stream) {
            stream.throwOptionalDataIncompatibleException();
        }
    }

    protected void throwOptionalDataIncompatibleException() 
    {
        throw omgWrapper.rmiiiopOptionalDataIncompatible2() ;
    }


    protected static class InReadObjectDefaultsSentState extends ReadObjectState {
        
        @Override
        public void beginUnmarshalCustomValueOverride(InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject) {
            
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        @Override
        public void endUnmarshalCustomValueOverride(InputStreamHook stream) {

            
            
            
            
            if (stream.getStreamFormatVersion() == 2) {
                ((ValueInputStream)stream.getOrbStream()).start_value();
                ((ValueInputStream)stream.getOrbStream()).end_value();
            }

            stream.setState(DEFAULT_STATE);
        }

        @Override
        public void endDefaultReadObjectOverride(InputStreamHook stream) throws IOException {

            
            if (stream.getStreamFormatVersion() == 2) {
                ((ValueInputStream) stream.getOrbStream()).start_value();
            }

            stream.setState(IN_READ_OBJECT_OPT_DATA);
        }

        @Override
        public void readDataOverride(InputStreamHook stream) throws IOException {
            org.omg.CORBA.ORB orb = stream.getOrbStream().orb();
            if ((orb == null) ||
                    !(orb instanceof com.sun.corba.ee.spi.orb.ORB)) {
                throw new StreamCorruptedException(
                                     "Default data must be read first");
            }
            ORBVersion clientOrbVersion = 
                ((com.sun.corba.ee.spi.orb.ORB)orb).getORBVersion();

            
            
            
            
            if ((ORBVersionFactory.getPEORB().compareTo(clientOrbVersion) <= 0) || 
                    (clientOrbVersion.equals(ORBVersionFactory.getFOREIGN()))) {
                throw Exceptions.self.defaultDataMustBeReadFirst() ;
            }
        }
    }

    protected static class InReadObjectOptionalDataState extends ReadObjectState {

        @Override
        public void beginUnmarshalCustomValueOverride(InputStreamHook stream,
                                              boolean calledDefaultWriteObject,
                                              boolean hasReadObject) 
        {
            
            throw utilWrapper.badBeginUnmarshalCustomValue() ;
        }

        @Override
        public void endUnmarshalCustomValueOverride(InputStreamHook stream) throws IOException 
        {
            if (stream.getStreamFormatVersion() == 2) {
                ((ValueInputStream)stream.getOrbStream()).end_value();
            }
            stream.setState(DEFAULT_STATE);
        }
        
        @Override
        public void beginDefaultReadObjectOverride(InputStreamHook stream) throws IOException 
        {
            throw Exceptions.self.defaultDataNotPresent() ;
        }

        
    }

    protected static class InReadObjectNoMoreOptionalDataState 
        extends InReadObjectOptionalDataState {

        @Override
        public void readDataOverride(InputStreamHook stream) throws IOException {
            stream.throwOptionalDataIncompatibleException();
        }
    }

    protected static class NoReadObjectDefaultsSentState extends ReadObjectState {
        @Override
        public void endUnmarshalCustomValueOverride(InputStreamHook stream) throws IOException {
            

            if (stream.getStreamFormatVersion() == 2) {
                ((ValueInputStream)stream.getOrbStream()).start_value();
                ((ValueInputStream)stream.getOrbStream()).end_value();
            }

            stream.setState(DEFAULT_STATE);
        }
    }
}
