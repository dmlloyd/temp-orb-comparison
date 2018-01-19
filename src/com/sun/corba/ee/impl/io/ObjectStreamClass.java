



package com.sun.corba.ee.impl.io;

import com.sun.corba.ee.impl.misc.ClassInfoCache;
import com.sun.corba.ee.impl.util.RepositoryId;
import com.sun.corba.ee.spi.trace.TraceValueHandler;
import org.glassfish.pfl.basic.concurrent.SoftCache;
import org.glassfish.pfl.basic.reflection.Bridge;
import org.omg.CORBA.ValueMember;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@TraceValueHandler
public class ObjectStreamClass implements java.io.Serializable {
    private static final boolean DEBUG_SVUID = false ;

    public static final long kDefaultUID = -1;

    
    private boolean isEnum ;

    private static final Bridge bridge = 
        AccessController.doPrivileged(
            new PrivilegedAction<Bridge>() {
                public Bridge run() {
                    return Bridge.get() ;
                }
            } 
        ) ;

    
    @TraceValueHandler
    static final ObjectStreamClass lookup(Class<?> cl) {
        ObjectStreamClass desc = lookupInternal(cl);
        if (desc.isSerializable() || desc.isExternalizable())
            return desc;
        return null;
    }

    
    static ObjectStreamClass lookupInternal(Class<?> cl)
    {
        
        ObjectStreamClass desc = null;
        synchronized (descriptorFor) {
            
            desc = (ObjectStreamClass)descriptorFor.get( cl ) ;
            if (desc == null) {
                
                ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( cl ) ;
                boolean serializable = Serializable.class.isAssignableFrom(cl) ;
                
                
                ObjectStreamClass superdesc = null;
                if (serializable) {
                    Class<?> superclass = cl.getSuperclass();
                    if (superclass != null)
                        superdesc = lookup(superclass);
                }

                
                boolean externalizable = false;
                if (serializable) {
                    externalizable =
                        ((superdesc != null) && superdesc.isExternalizable()) ||
                        cinfo.isAExternalizable(cl);
                    if (externalizable) {
                        serializable = false;
                    }
                }

                
                desc = new ObjectStreamClass(cl, superdesc,
                                             serializable, externalizable);
            }

            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            desc.init();
        }

        return desc;
    }

    
    public final String getName() {
        return name;
    }

    
    public static final long getSerialVersionUID( java.lang.Class<?> clazz) {
        ObjectStreamClass theosc = ObjectStreamClass.lookup( clazz );
        if (theosc != null) {
            return theosc.getSerialVersionUID( );
        }
        return 0;
    }

    
    public final long getSerialVersionUID() {
        return suid;
    }

    
    public final String getSerialVersionUIDStr() {
        if (suidStr == null)
            suidStr = Long.toHexString(suid).toUpperCase();
        return suidStr;
    }

    
    public static final long getActualSerialVersionUID( java.lang.Class<?> clazz )
    {
        ObjectStreamClass theosc = ObjectStreamClass.lookup( clazz );
        if( theosc != null )
        {
                return theosc.getActualSerialVersionUID( );
        }
        return 0;
    }

    
    public final long getActualSerialVersionUID() {
        return actualSuid;
    }

    
    public final String getActualSerialVersionUIDStr() {
        if (actualSuidStr == null)
            actualSuidStr = Long.toHexString(actualSuid).toUpperCase();
        return actualSuidStr;
    }

    
    public final Class<?> forClass() {
        return ofClass;
    }

    
    public ObjectStreamField[] getFields() {
        
        if (fields.length > 0) {
            ObjectStreamField[] dup = new ObjectStreamField[fields.length];
            System.arraycopy(fields, 0, dup, 0, fields.length);
            return dup;
        } else {
            return fields;
        }
    }

    public boolean hasField(ValueMember field)
    {
        try {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().equals(field.name)) {
                    if (fields[i].getSignature().equals(
                        ValueUtility.getSignature(field)))
                        return true;
                }
            }
        } catch (Exception exc) {
            
            
        }

        return false;
    }

    
    final ObjectStreamField[] getFieldsNoCopy() {
        return fields;
    }

    
    public final ObjectStreamField getField(String name) {
        
        for (int i = fields.length-1; i >= 0; i--) {
            if (name.equals(fields[i].getName())) {
                return fields[i];
            }
        }
        return null;
    }

    public Serializable writeReplace(Serializable value) {
        if (writeReplaceObjectMethod != null) {
            try {
                return (Serializable) writeReplaceObjectMethod.invoke(value);
            } catch(Throwable t) {
                throw new RuntimeException(t);
            }
        }
        else return value;
    }

    public Object readResolve(Object value) {
        if (readResolveObjectMethod != null) {
            try {
                return readResolveObjectMethod.invoke(value);
            } catch(Throwable t) {
                throw new RuntimeException(t);
            }
        }
        else return value;
    }

    
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(name);
        sb.append(": static final long serialVersionUID = ");
        sb.append(Long.toString(suid));
        sb.append("L;");
        return sb.toString();
    }

    
    private ObjectStreamClass(java.lang.Class<?> cl, ObjectStreamClass superdesc,
                              boolean serial, boolean extern)
    {
        ofClass = cl;           

        if (Proxy.isProxyClass(cl)) {
            forProxyClass = true;
        }

        name = cl.getName();
        
        final ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( cl ) ;
        isEnum = cinfo.isEnum() ;
        superclass = superdesc;
        serializable = serial;
        if (!forProxyClass) {
            
            externalizable = extern;
        }

        
        descriptorFor.put( cl, this ) ;
    }

    MethodHandle getWriteObjectMethod() {
        return writeObjectMethod;
    }

    MethodHandle getReadObjectMethod() {
        return readObjectMethod;
    }

    private static final class PersistentFieldsValue {
        private final ConcurrentMap<Class<?>, Object> map = new ConcurrentHashMap<Class<?>, Object>();
        private static final Object NULL_VALUE =
                (PersistentFieldsValue.class.getName() + ".NULL_VALUE");

        PersistentFieldsValue() { }

        ObjectStreamField[] get(Class<?> type) {
            Object value = map.get(type);
            if (value == null) {
                value = computeValue(type);
                Object oldValue = map.putIfAbsent(type, value);
                if (oldValue != null) {
                    value = oldValue;
                }
            }
            return ((value == NULL_VALUE) ? null : (ObjectStreamField[])value);
        }

        private static Object computeValue(Class<?> type) {
            try {
                bridge.ensureClassInitialized(type);
                Field pf = type.getDeclaredField("serialPersistentFields");
                int mods = pf.getModifiers();
                if (Modifier.isPrivate(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods)) {
                    java.io.ObjectStreamField[] fields = bridge.getObject(type, bridge.staticFieldOffset(pf));
                    return translateFields(fields);
                }
            } catch (NoSuchFieldException | IllegalArgumentException | ClassCastException ignored) {
            }
            return NULL_VALUE;
        }

        private static ObjectStreamField[] translateFields(java.io.ObjectStreamField[] fields) {
            ObjectStreamField[] translation = new ObjectStreamField[fields.length];
            for (int i = 0; i < fields.length; i++)
                translation[i] = new ObjectStreamField(fields[i].getName(), fields[i].getType());
            return translation;
        }
    }

    private static final PersistentFieldsValue persistentFieldsValue =
            new PersistentFieldsValue();
    
    private void init() {
        synchronized (lock) {
            
            if (initialized)
                return;

            final Class<?> cl = ofClass;

            
            if (!serializable || externalizable || forProxyClass || isEnum ||
                name.equals("java.lang.String")) {
                fields = NO_FIELDS;
            } else if (serializable) {
                fields = null ;

                
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        
                        fields = persistentFieldsValue.get(cl);

                        if (fields == null) {
                            
                            Field[] actualfields = cl.getDeclaredFields();

                            int numFields = 0;
                            ObjectStreamField[] tempFields =
                                new ObjectStreamField[actualfields.length];
                            for (int i = 0; i < actualfields.length; i++) {
                                Field fld = actualfields[i] ;
                                int modifiers = fld.getModifiers();
                                if (!Modifier.isStatic(modifiers) &&
                                    !Modifier.isTransient(modifiers)) {
                
                                    tempFields[numFields++] = new ObjectStreamField(fld);
                                }
                            }

                            fields = new ObjectStreamField[numFields];
                            System.arraycopy(tempFields, 0, fields, 0, numFields);
                        } else {
                            
                            
                            
                            for (int j = fields.length-1; j >= 0; j--) {
                                try {
                                    Field reflField = cl.getDeclaredField(fields[j].getName());
                                    if (fields[j].getType() == reflField.getType()) {
                                        reflField.setAccessible(true);
                                        fields[j].setField(reflField);
                                    } else {
                                        Exceptions.self.fieldTypeMismatch(
                                            cl.getName(),
                                            fields[j].getName(),
                                            fields[j].getType(),
                                            reflField.getName(),
                                            reflField.getType() ) ;
                                    }
                                } catch (NoSuchFieldException e) {
                                    Exceptions.self.noSuchField( e, cl.getName(),
                                        fields[j].getName() ) ;
                                }
                            }
                        }

                        return null;
                    }
                });

                if (fields.length > 1)
                    Arrays.sort(fields);

                
                computeFieldInfo();
            }

            
             
             if (isNonSerializable() || isEnum) {
                 suid = 0L;
             } else {
                 
                 AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    public Object run() {
                        if (forProxyClass) {
                            
                            suid = 0L;
                        } else {
                            try {
                                final Field f = cl.getDeclaredField("serialVersionUID");
                                int mods = f.getModifiers();
                                
                                if (Modifier.isStatic(mods) && Modifier.isFinal(mods) ) {
                                    long offset = bridge.staticFieldOffset(f);
                                    suid = bridge.getLong(cl, offset);
                                    
                                    
                                } else {
                                    suid = _computeSerialVersionUID(cl);
                                    
                                    
                                }
                            } catch (NoSuchFieldException ex) {
                                suid = _computeSerialVersionUID(cl);
                                
                                
                            }
                        }

                        writeReplaceObjectMethod = bridge.writeReplaceForSerialization(cl);
                        readResolveObjectMethod = bridge.readResolveForSerialization(cl);

                        if (externalizable) 
                            cons = bridge.newConstructorForExternalization(cl) ;
                        else
                            cons = bridge.newConstructorForSerialization(cl) ;

                        if (serializable && !forProxyClass) {
                            
                            writeObjectMethod = bridge.writeObjectForSerialization(cl);
                            readObjectMethod = bridge.readObjectForSerialization(cl);
                        }
                        return null;
                    }
                });
            }

            
            actualSuid = ObjectStreamClass.computeStructuralUID(this, cl);

            
            
            
            
            
            if (hasWriteObject() || isExternalizable())
                rmiiiopOptionalDataRepId = computeRMIIIOPOptionalDataRepId();

            
            initialized = true;
        }
    }

    
    private String computeRMIIIOPOptionalDataRepId() {

        StringBuffer sbuf = new StringBuffer("RMI:org.omg.custom.");
        sbuf.append(RepositoryId.convertToISOLatin1(this.getName()));
        sbuf.append(':');
        sbuf.append(this.getActualSerialVersionUIDStr());
        sbuf.append(':');
        sbuf.append(this.getSerialVersionUIDStr());

        return sbuf.toString();
    }

    
    public final String getRMIIIOPOptionalDataRepId() {
        return rmiiiopOptionalDataRepId;
    }

    
    ObjectStreamClass(String n, long s) {
        name = n;
        suid = s;
        superclass = null;
    }

    public static final synchronized ObjectStreamField[] translateFields(
            java.io.ObjectStreamField fields[]) {
        return PersistentFieldsValue.translateFields(fields);
    }

    
    static boolean compareClassNames(String streamName,
                                     String localName,
                                     char pkgSeparator) {
        
        int streamNameIndex = streamName.lastIndexOf(pkgSeparator);
        if (streamNameIndex < 0)
            streamNameIndex = 0;

        int localNameIndex = localName.lastIndexOf(pkgSeparator);
        if (localNameIndex < 0)
            localNameIndex = 0;

        return streamName.regionMatches(false, streamNameIndex,
                                        localName, localNameIndex,
                                        streamName.length() - streamNameIndex);
    }

    
    final boolean typeEquals(ObjectStreamClass other) {
        return (suid == other.suid) &&
            compareClassNames(name, other.name, '.');
    }

    
    final void setSuperclass(ObjectStreamClass s) {
        superclass = s;
    }

    
    final ObjectStreamClass getSuperclass() {
        return superclass;
    }

    
    final boolean hasReadObject() {
        return readObjectMethod != null;
    }

    
    final boolean hasWriteObject() {
        return writeObjectMethod != null ;
    }

    
    final boolean isCustomMarshaled() {
        return (hasWriteObject() || isExternalizable())
            || (superclass != null && superclass.isCustomMarshaled());
    }

    
    boolean hasExternalizableBlockDataMode() {
        return hasExternalizableBlockData;
    }

    
    Object newInstance()
        throws InstantiationException, InvocationTargetException,
               UnsupportedOperationException
    {
        if (cons != null) {
            try {
                return cons.newInstance(new Object[0]);
            } catch (IllegalAccessException ex) {
                
                InternalError ie = new InternalError();
                ie.initCause( ex ) ;
                throw ie ;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    
    boolean isSerializable() {
        return serializable;
    }

    
    boolean isExternalizable() {
        return externalizable;
    }

    boolean isNonSerializable() {
        return ! (externalizable || serializable);
    }

    
    private void computeFieldInfo() {
        primBytes = 0;
        objFields = 0;

        for (int i = 0; i < fields.length; i++ ) {
            switch (fields[i].getTypeCode()) {
            case 'B':
            case 'Z':
                primBytes += 1;
                break;
            case 'C':
            case 'S':
                primBytes += 2;
                break;

            case 'I':
            case 'F':
                primBytes += 4;
                break;
            case 'J':
            case 'D' :
                primBytes += 8;
                break;

            case 'L':
            case '[':
                objFields += 1;
                break;
            }
        }
    }

    private static void msg( String str )
    {
        System.out.println( str ) ;
    }

    


    public static final int CLASS_MASK = Modifier.PUBLIC | Modifier.FINAL |
        Modifier.INTERFACE | Modifier.ABSTRACT ;
    public static final int FIELD_MASK = Modifier.PUBLIC | Modifier.PRIVATE |
        Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL | 
        Modifier.TRANSIENT | Modifier.VOLATILE ;
    public static final int METHOD_MASK = Modifier.PUBLIC | Modifier.PRIVATE |
        Modifier.PROTECTED | Modifier.STATIC | Modifier.FINAL | 
        Modifier.SYNCHRONIZED | Modifier.NATIVE | Modifier.ABSTRACT |
        Modifier.STRICT ;
    
    
    private static long _computeSerialVersionUID(Class<?> cl) {
        if (DEBUG_SVUID)
            msg( "Computing SerialVersionUID for " + cl ) ; 
        ByteArrayOutputStream devnull = new ByteArrayOutputStream(512);
        ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( cl ) ;

        long h = 0;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            DigestOutputStream mdo = new DigestOutputStream(devnull, md);
            DataOutputStream data = new DataOutputStream(mdo);

            if (DEBUG_SVUID)
                msg( "\twriteUTF( \"" + cl.getName() + "\" )" ) ;
            data.writeUTF(cl.getName());

            int classaccess = cl.getModifiers();
            classaccess &= (Modifier.PUBLIC | Modifier.FINAL |
                            Modifier.INTERFACE | Modifier.ABSTRACT);

            
            Method[] method = cl.getDeclaredMethods();
            if ((classaccess & Modifier.INTERFACE) != 0) {
                classaccess &= (~Modifier.ABSTRACT);
                if (method.length > 0) {
                    classaccess |= Modifier.ABSTRACT;
                }
            }

            
            classaccess &= CLASS_MASK ;

            if (DEBUG_SVUID)
                msg( "\twriteInt( " + classaccess + " ) " ) ;
            data.writeInt(classaccess);

            
            if (!cinfo.isArray()) {
                

                Class<?> interfaces[] = cl.getInterfaces();
                Arrays.sort(interfaces, compareClassByName);

                for (int i = 0; i < interfaces.length; i++) {
                    if (DEBUG_SVUID)
                        msg( "\twriteUTF( \"" + interfaces[i].getName() + "\" ) " ) ;
                    data.writeUTF(interfaces[i].getName());
                }
            }

            
            Field[] field = cl.getDeclaredFields();
            Arrays.sort(field, compareMemberByName);

            for (int i = 0; i < field.length; i++) {
                Field f = field[i];

                
                int m = f.getModifiers();
                if (Modifier.isPrivate(m) &&
                    (Modifier.isTransient(m) || Modifier.isStatic(m)))
                    continue;

                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"" + f.getName() + "\" ) " ) ;
                data.writeUTF(f.getName());

                
                m &= FIELD_MASK ;

                if (DEBUG_SVUID)
                    msg( "\twriteInt( " + m + " ) " ) ;
                data.writeInt(m);

                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"" + getSignature(f.getType()) + "\" ) " ) ;
                data.writeUTF(getSignature(f.getType()));
            }

            if (bridge.hasStaticInitializerForSerialization(cl)) {
                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"<clinit>\" ) " ) ;
                data.writeUTF("<clinit>");

                if (DEBUG_SVUID)
                    msg( "\twriteInt( " + Modifier.STATIC + " )" ) ;
                data.writeInt(Modifier.STATIC); 

                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"()V\" )" ) ;
                data.writeUTF("()V");
            }

            

            MethodSignature[] constructors =
                MethodSignature.removePrivateAndSort(cl.getDeclaredConstructors());
            for (int i = 0; i < constructors.length; i++) {
                MethodSignature c = constructors[i];
                String mname = "<init>";
                String desc = c.signature;
                desc = desc.replace('/', '.');
                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"" + mname + "\" )" ) ;
                data.writeUTF(mname);

                
                int modifier = c.member.getModifiers() & METHOD_MASK ;

                if (DEBUG_SVUID)
                    msg( "\twriteInt( " + modifier + " ) " ) ;
                data.writeInt( modifier ) ;

                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"" + desc+ "\" )" ) ;
                data.writeUTF(desc);
            }

            
            MethodSignature[] methods =
                MethodSignature.removePrivateAndSort(method);
            for (int i = 0; i < methods.length; i++ ) {
                MethodSignature m = methods[i];
                String desc = m.signature;
                desc = desc.replace('/', '.');

                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"" + m.member.getName()+ "\" )" ) ;
                data.writeUTF(m.member.getName());

                
                int modifier = m.member.getModifiers() & METHOD_MASK ;

                if (DEBUG_SVUID)
                    msg( "\twriteInt( " + modifier + " ) " ) ;
                data.writeInt( modifier ) ;

                if (DEBUG_SVUID)
                    msg( "\twriteUTF( \"" + desc + "\" )" ) ;
                data.writeUTF(desc);
            }

            
            data.flush();
            byte hasharray[] = md.digest();
            for (int i = 0; i < Math.min(8, hasharray.length); i++) {
                h += (long)(hasharray[i] & 255) << (i * 8);
            }
        } catch (IOException ignore) {
            
            h = -1;
        } catch (NoSuchAlgorithmException complain) {
            SecurityException se = new SecurityException() ;
            se.initCause( complain ) ;
            throw se ;
        }

        return h;
    }

    private static long computeStructuralUID(
        com.sun.corba.ee.impl.io.ObjectStreamClass osc, Class<?> cl) {

        ByteArrayOutputStream devnull = new ByteArrayOutputStream(512);
        ClassInfoCache.ClassInfo cinfo = ClassInfoCache.get( cl ) ;
                
        long h = 0;
        try {
            if (!cinfo.isASerializable(cl) || cinfo.isInterface()) {
                return 0;
            }

            if (cinfo.isAExternalizable(cl)) {
                return 1;
            }

            MessageDigest md = MessageDigest.getInstance("SHA");
            DigestOutputStream mdo = new DigestOutputStream(devnull, md);
            DataOutputStream data = new DataOutputStream(mdo);

            
            Class<?> parent = cl.getSuperclass();
            if ((parent != null))  
            
            
            
            
            {
                data.writeLong(computeStructuralUID(lookup(parent), parent));
            }

            if (osc.hasWriteObject())
                data.writeInt(2);
            else
                data.writeInt(1);

            
            
            
            ObjectStreamField[] field = osc.getFields();
            if (field.length > 1) {
                Arrays.sort(field, compareObjStrFieldsByName);
            }

            
            
            for (int i = 0; i < field.length; i++) {
                data.writeUTF(field[i].getName());
                data.writeUTF(field[i].getSignature());
            }
                        
            
            data.flush();
            byte hasharray[] = md.digest();
            
            
            
            for (int i = 0; i < Math.min(8, hasharray.length); i++) {
                h += (long)(hasharray[i] & 255) << (i * 8);
            }
        } catch (IOException ignore) {
            
            h = -1;
        } catch (NoSuchAlgorithmException complain) {
            SecurityException se = new SecurityException();
            se.initCause( complain ) ;
            throw se ;
        }
        return h;
    }

    
    static String getSignature(Class<?> clazz) {
        String type = null;
        if (ClassInfoCache.get( clazz ).isArray()) {
            Class<?> cl = clazz;
            int dimensions = 0;
            while (ClassInfoCache.get( cl ).isArray()) {
                dimensions++;
                cl = cl.getComponentType();
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < dimensions; i++) {
                sb.append("[");
            }
            sb.append(getSignature(cl));
            type = sb.toString();
        } else if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                type = "I";
            } else if (clazz == Byte.TYPE) {
                type = "B";
            } else if (clazz == Long.TYPE) {
                type = "J";
            } else if (clazz == Float.TYPE) {
                type = "F";
            } else if (clazz == Double.TYPE) {
                type = "D";
            } else if (clazz == Short.TYPE) {
                type = "S";
            } else if (clazz == Character.TYPE) {
                type = "C";
            } else if (clazz == Boolean.TYPE) {
                type = "Z";
            } else if (clazz == Void.TYPE) {
                type = "V";
            }
        } else {
            type = "L" + clazz.getName().replace('.', '/') + ";";
        }
        return type;
    }

    
    static String getSignature(Method meth) {
        StringBuffer sb = new StringBuffer();

        sb.append("(");

        Class<?>[] params = meth.getParameterTypes(); 
        for (int j = 0; j < params.length; j++) {
            sb.append(getSignature(params[j]));
        }
        sb.append(")");
        sb.append(getSignature(meth.getReturnType()));
        return sb.toString();
    }

    
    static String getSignature(Constructor<?> cons) {
        StringBuffer sb = new StringBuffer();

        sb.append("(");

        Class<?>[] params = cons.getParameterTypes(); 
        for (int j = 0; j < params.length; j++) {
            sb.append(getSignature(params[j]));
        }
        sb.append(")V");
        return sb.toString();
    }

    
    static private final SoftCache<Class<?>,ObjectStreamClass> descriptorFor =
        new SoftCache<Class<?>,ObjectStreamClass>() ;

    
    private String name;

    
    private ObjectStreamClass superclass;

    
    private boolean serializable;
    private boolean externalizable;

    
    private ObjectStreamField[] fields;

    
    private Class<?> ofClass;

    
    boolean forProxyClass;


    
    private long suid = kDefaultUID;
    private String suidStr = null;

    
    private long actualSuid = kDefaultUID;
    private String actualSuidStr = null;

    
    int primBytes;
    int objFields;

    
    private boolean initialized = false;

    
    private final Object lock = new Object();

    
    private boolean hasExternalizableBlockData;

    private MethodHandle writeObjectMethod;
    private MethodHandle readObjectMethod;
    private transient MethodHandle writeReplaceObjectMethod;
    private transient MethodHandle readResolveObjectMethod;
    private Constructor<?> cons ;

    
    private String rmiiiopOptionalDataRepId = null;

    
    private static final long serialVersionUID = -6120832682080437368L;

    
    public static final ObjectStreamField[] NO_FIELDS =
        new ObjectStreamField[0];

    
    private static Comparator<Class<?>> compareClassByName =
        new CompareClassByName();

    private static class CompareClassByName 
        implements Comparator<Class<?>> {

        public int compare(Class<?> c1, Class<?> c2) {
            return c1.getName().compareTo(c2.getName());
        }
    }

    
    private final static Comparator<ObjectStreamField> compareObjStrFieldsByName
        = new CompareObjStrFieldsByName();

    private static class CompareObjStrFieldsByName 
        implements Comparator<ObjectStreamField> {

        public int compare(ObjectStreamField o1, ObjectStreamField o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    
    private static Comparator<Member> compareMemberByName =
        new CompareMemberByName();

    private static class CompareMemberByName 
        implements Comparator<Member> {

        public int compare(Member o1, Member o2) {
            String s1 = o1.getName();
            String s2 = o2.getName();

            if ((o1 instanceof Method) && (o2 instanceof Method)) {
                s1 += getSignature((Method)o1);
                s2 += getSignature((Method)o2);
            } else if ((o1 instanceof Constructor) && (o2 instanceof Constructor)) {
                s1 += getSignature((Constructor<?>)o1);
                s2 += getSignature((Constructor<?>)o2);
            }
            return s1.compareTo(s2);
        }
    }

    
    private static class MethodSignature 
        implements Comparator<MethodSignature> {

        Member member;
        String signature;      

        
        
        static MethodSignature[] removePrivateAndSort(Member[] m) {
            int numNonPrivate = 0;
            for (int i = 0; i < m.length; i++) {
                if (! Modifier.isPrivate(m[i].getModifiers())) {
                    numNonPrivate++;
                }
            }
            MethodSignature[] cm = new MethodSignature[numNonPrivate];
            int cmi = 0;
            for (int i = 0; i < m.length; i++) {
                if (! Modifier.isPrivate(m[i].getModifiers())) {
                    cm[cmi] = new MethodSignature(m[i]);
                    cmi++;
                }
            }
            if (cmi > 0)
                Arrays.sort(cm, cm[0]);
            return cm;
        }

        
        public int compare(MethodSignature c1, MethodSignature c2) {
            
            if (c1 == c2)
                return 0;

            int result;
            if (isConstructor()) {
                result = c1.signature.compareTo(c2.signature);
            } else { 
                result = c1.member.getName().compareTo(c2.member.getName());
                if (result == 0)
                    result = c1.signature.compareTo(c2.signature);
            }
            return result;
        }

        private boolean isConstructor() {
            return member instanceof Constructor;
        }

        private MethodSignature(Member m) {
            member = m;
            if (isConstructor()) {
                signature = ObjectStreamClass.getSignature((Constructor<?>)m);
            } else {
                signature = ObjectStreamClass.getSignature((Method)m);
            }
        }
    }

}
