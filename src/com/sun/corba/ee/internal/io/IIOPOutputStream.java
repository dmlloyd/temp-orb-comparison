

package com.sun.corba.ee.internal.io;


public class IIOPOutputStream {
    
    
    private static native void throwExceptionType(Class c, String message);

    private static native Object getObjectFieldOpt(Object o, long fieldID);
    private static native boolean getBooleanFieldOpt(Object o, long fieldID);
    private static native byte getByteFieldOpt(Object o, long fieldID);
    private static native char getCharFieldOpt(Object o, long fieldID);
    private static native short getShortFieldOpt(Object o, long fieldID);
    private static native int getIntFieldOpt(Object o, long fieldID);
    private static native long getLongFieldOpt(Object o, long fieldID);
    private static native float getFloatFieldOpt(Object o, long fieldID);
    private static native double getDoubleFieldOpt(Object o, long fieldID);

    private static native void writeObject(Object obj, Class asClass, Object oos) throws IllegalAccessException;
}
