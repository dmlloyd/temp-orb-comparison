


package xxxx;









public class ObjectStreamClass implements java.io.Serializable {
    private static final boolean DEBUG_SVUID = false ;

    public static final long kDefaultUID = -1;

    
    private boolean isEnum;

    private static final Bridge bridge =
        AccessController.doPrivileged(
            new PrivilegedAction<Bridge>() {
                public Bridge run() {
                    return Bridge.get() ;
                }
            }
        ) ;

    
    static final ObjectStreamClass lookup(Class<?> cl)
    {
        ObjectStreamClass desc = lookupInternal(cl);
        if (desc.isSerializable() || desc.isExternalizable())
            return desc;
        return null;
    }

    
    static ObjectStreamClass lookupInternal(Class<?> cl)
    {
        
        ObjectStreamClass desc = null;
        synchronized (descriptorFor) {
            
            desc = findDescriptorFor(cl);
            if (desc == null) {
                
                boolean serializable = Serializable.class.isAssignableFrom(cl);

                
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
                        Externalizable.class.isAssignableFrom(cl);
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
        if( theosc != null )
        {
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

    public final boolean invokeWriteObject(Object obj, ObjectOutputStream ois) throws InvocationTargetException {
        if (!hasWriteObject()) {
            return false;
        }
        try {
            writeObjectMethod.invoke(obj, ois);
        } catch (Throwable t) {
            throw new InvocationTargetException(t, "writeObject");
        }
        return true;
    }

    public final boolean invokeReadObject(Object obj, ObjectInputStream ois) throws InvocationTargetException {
        if (hasReadObject()) {
            try {
                readObjectMethod.invoke(obj, ois);
                return true;
            } catch (Throwable t) {
                throw new InvocationTargetException(t, "readObject");
            }
        } else {
            return false;
        }
    }

    public Serializable writeReplace(Serializable value) {
        if (writeReplaceObjectMethod != null) {
            try {
                return (Serializable) writeReplaceObjectMethod.invoke(value);
            } catch (Throwable t) {
                throw new InternalError("unexpected error", t);
            }
        }
        else return value;
    }

    public Object readResolve(Object value) {
        if (readResolveObjectMethod != null) {
            try {
                return readResolveObjectMethod.invoke(value);
            } catch (Throwable t) {
                throw new InternalError("unexpected error", t);
            }
        }
        else return value;
    }

    
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
        isEnum = Enum.class.isAssignableFrom(cl);
        superclass = superdesc;
        serializable = serial;
        if (!forProxyClass) {
            
            externalizable = extern;
        }

        
        insertDescriptorFor(this);

        
    }

    static final class PersistentFieldsValue
            extends ClassValue<ObjectStreamField[]> {
        PersistentFieldsValue() { }

        protected ObjectStreamField[] computeValue(Class<?> type) {
            try {
                bridge.ensureClassInitialized(type);
                Field pf = type.getDeclaredField("serialPersistentFields");
                int mods = pf.getModifiers();
                if (Modifier.isPrivate(mods) && Modifier.isStatic(mods) &&
                        Modifier.isFinal(mods)) {
                    long offset = bridge.staticFieldOffset(pf);
                    java.io.ObjectStreamField[] fields =
                            (java.io.ObjectStreamField[])bridge.getObject(type, offset);
                    return translateFields(fields);
                }
            } catch (NoSuchFieldException |
                    IllegalArgumentException | ClassCastException e) {
            }
            return null;
        }

        private static ObjectStreamField[] translateFields(java.io.ObjectStreamField[] fields) {
            if (fields == null) {
                return null;
            }
            ObjectStreamField[] translation =
                    new ObjectStreamField[fields.length];
            for (int i = 0; i < fields.length; i++) {
                translation[i] = new ObjectStreamField(fields[i].getName(),
                        fields[i].getType());
            }
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

        if (!serializable ||
            externalizable ||
            forProxyClass ||
            name.equals("java.lang.String")){
            fields = NO_FIELDS;
        } else if (serializable) {
            
            AccessController.doPrivileged(new PrivilegedAction() {
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
                                fields[j].setField(reflField);
                            }
                        } catch (NoSuchFieldException e) {
                            
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
             
             AccessController.doPrivileged(new PrivilegedAction() {
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
                    cons = getExternalizableConstructor(cl) ;
                else
                    cons = getSerializableConstructor(cl) ;

                if (serializable && !forProxyClass) {
                    writeObjectMethod = bridge.writeObjectForSerialization(cl) ;
                    readObjectMethod = bridge.readObjectForSerialization(cl);
                }
                return null;
            }
          });
        }

        
        actualSuid = ObjectStreamClass.computeStructuralUID(this, cl);

        
        
        
        if (hasWriteObject())
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


    
    final void setClass(Class<?> cl) throws InvalidClassException {

        if (cl == null) {
            localClassDesc = null;
            ofClass = null;
            computeFieldInfo();
            return;
        }

        localClassDesc = lookupInternal(cl);
        if (localClassDesc == null)
            
            throw new InvalidClassException(cl.getName(),
                                            "Local class not compatible");
        if (suid != localClassDesc.suid) {

            

            
            boolean addedSerialOrExtern =
                isNonSerializable() || localClassDesc.isNonSerializable();

            

            boolean arraySUID = (cl.isArray() && ! cl.getName().equals(name));

            if (! arraySUID && ! addedSerialOrExtern ) {
                
                throw new InvalidClassException(cl.getName(),
                                                "Local class not compatible:" +
                                                " stream classdesc serialVersionUID=" + suid +
                                                " local class serialVersionUID=" + localClassDesc.suid);
            }
        }

        
        if (! compareClassNames(name, cl.getName(), '.'))
            
            throw new InvalidClassException(cl.getName(),
                         "Incompatible local class name. " +
                         "Expected class name compatible with " +
                         name);

        

        
        
        
        
        
        
        

        if ((serializable != localClassDesc.serializable) ||
            (externalizable != localClassDesc.externalizable) ||
            (!serializable && !externalizable))

            
            throw new InvalidClassException(cl.getName(),
                                            "Serialization incompatible with Externalization");

        

        ObjectStreamField[] destfield =
            (ObjectStreamField[])localClassDesc.fields;
        ObjectStreamField[] srcfield =
            (ObjectStreamField[])fields;

        int j = 0;
    nextsrc:
        for (int i = 0; i < srcfield.length; i++ ) {
            
            for (int k = j; k < destfield.length; k++) {
                if (srcfield[i].getName().equals(destfield[k].getName())) {
                    
                    if (srcfield[i].isPrimitive() &&
                        !srcfield[i].typeEquals(destfield[k])) {
                        
                        throw new InvalidClassException(cl.getName(),
                                                        "The type of field " +
                                                        srcfield[i].getName() +
                                                        " of class " + name +
                                                        " is incompatible.");
                    }

                    
                    j = k;

                    srcfield[i].setField(destfield[j].getField());
                    
                    continue nextsrc;
                }
            }
        }

        
        computeFieldInfo();

        
        ofClass = cl;

        
        readObjectMethod = localClassDesc.readObjectMethod;
        readResolveObjectMethod = localClassDesc.readResolveObjectMethod;
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

    
    boolean hasWriteReplaceMethod() {
        return (writeReplaceObjectMethod != null);
    }

    
    boolean hasReadResolveMethod() {
        return (readResolveObjectMethod != null);
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
                return cons.newInstance();
            } catch (IllegalAccessException ex) {
                
                InternalError ie = new InternalError();
                ie.initCause( ex ) ;
                throw ie ;
            }
        } else {
            throw new UnsupportedOperationException("no constructor for " + ofClass);
        }
    }

    
    private static Constructor<?> getExternalizableConstructor(Class<?> cl) {
        return bridge.newConstructorForExternalization(cl);
    }

    
    private static Constructor<?> getSerializableConstructor(Class<?> cl) {
        return bridge.newConstructorForSerialization(cl);
    }

    
    final ObjectStreamClass localClassDescriptor() {
        return localClassDesc;
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

            
            if (!cl.isArray()) {
                

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

            if (hasStaticInitializer(cl)) {
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

    private static long computeStructuralUID(com.sun.corba.se.impl.io.ObjectStreamClass osc, Class<?> cl) {
        ByteArrayOutputStream devnull = new ByteArrayOutputStream(512);

        long h = 0;
        try {

            if ((!java.io.Serializable.class.isAssignableFrom(cl)) ||
                (cl.isInterface())){
                return 0;
            }

            if (java.io.Externalizable.class.isAssignableFrom(cl)) {
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
        if (clazz.isArray()) {
            Class<?> cl = clazz;
            int dimensions = 0;
            while (cl.isArray()) {
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

    
    static String getSignature(Constructor cons) {
        StringBuffer sb = new StringBuffer();

        sb.append("(");

        Class<?>[] params = cons.getParameterTypes(); 
        for (int j = 0; j < params.length; j++) {
            sb.append(getSignature(params[j]));
        }
        sb.append(")V");
        return sb.toString();
    }

    
    static private ObjectStreamClassEntry[] descriptorFor = new ObjectStreamClassEntry[61];

    
    private static ObjectStreamClass findDescriptorFor(Class<?> cl) {

        int hash = cl.hashCode();
        int index = (hash & 0x7FFFFFFF) % descriptorFor.length;
        ObjectStreamClassEntry e;
        ObjectStreamClassEntry prev;

        
        while ((e = descriptorFor[index]) != null && e.get() == null) {
            descriptorFor[index] = e.next;
        }

        
        prev = e;
        while (e != null ) {
            ObjectStreamClass desc = (ObjectStreamClass)(e.get());
            if (desc == null) {
                
                prev.next = e.next;
            } else {
                if (desc.ofClass == cl)
                    return desc;
                prev = e;
            }
            e = e.next;
        }
        return null;
    }

    
    private static void insertDescriptorFor(ObjectStreamClass desc) {
        
        if (findDescriptorFor(desc.ofClass) != null) {
            return;
        }

        int hash = desc.ofClass.hashCode();
        int index = (hash & 0x7FFFFFFF) % descriptorFor.length;
        ObjectStreamClassEntry e = new ObjectStreamClassEntry(desc);
        e.next = descriptorFor[index];
        descriptorFor[index] = e;
    }

    private static Field[] getDeclaredFields(final Class<?> clz) {
        return (Field[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return clz.getDeclaredFields();
            }
        });
    }


    
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

    
    private Object lock = new Object();

    
    private boolean hasExternalizableBlockData;
    private transient MethodHandle writeObjectMethod;
    private transient MethodHandle readObjectMethod;
    private transient MethodHandle writeReplaceObjectMethod;
    private transient MethodHandle readResolveObjectMethod;
    private transient Constructor<?> cons;

    
    private String rmiiiopOptionalDataRepId = null;

    
    private ObjectStreamClass localClassDesc;

    
    private static boolean hasStaticInitializer(Class<?> cl) {
        return bridge.hasStaticInitializerForSerialization(cl);
    }


    
    private static final long serialVersionUID = -6120832682080437368L;

    
    public static final ObjectStreamField[] NO_FIELDS =
        new ObjectStreamField[0];

    
    private static class ObjectStreamClassEntry 
    {
        ObjectStreamClassEntry(ObjectStreamClass c) {
            
            this.c = c;
        }
        ObjectStreamClassEntry next;

        public Object get()
        {
            return c;
        }
        private ObjectStreamClass c;
    }

    
    private static Comparator compareClassByName =
        new CompareClassByName();

    private static class CompareClassByName implements Comparator {
        public int compare(Object o1, Object o2) {
            Class<?> c1 = (Class)o1;
            Class<?> c2 = (Class)o2;
            return (c1.getName()).compareTo(c2.getName());
        }
    }

    
    private final static Comparator compareObjStrFieldsByName
        = new CompareObjStrFieldsByName();

    private static class CompareObjStrFieldsByName implements Comparator {
        public int compare(Object o1, Object o2) {
            ObjectStreamField osf1 = (ObjectStreamField)o1;
            ObjectStreamField osf2 = (ObjectStreamField)o2;

            return osf1.getName().compareTo(osf2.getName());
        }
    }

    
    private static Comparator compareMemberByName =
        new CompareMemberByName();

    private static class CompareMemberByName implements Comparator {
        public int compare(Object o1, Object o2) {
            String s1 = ((Member)o1).getName();
            String s2 = ((Member)o2).getName();

            if (o1 instanceof Method) {
                s1 += getSignature((Method)o1);
                s2 += getSignature((Method)o2);
            } else if (o1 instanceof Constructor) {
                s1 += getSignature((Constructor)o1);
                s2 += getSignature((Constructor)o2);
            }
            return s1.compareTo(s2);
        }
    }

    
    private static class MethodSignature implements Comparator {
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

        
        public int compare(Object o1, Object o2) {
            
            if (o1 == o2)
                return 0;

            MethodSignature c1 = (MethodSignature)o1;
            MethodSignature c2 = (MethodSignature)o2;

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

        final private boolean isConstructor() {
            return member instanceof Constructor;
        }
        private MethodSignature(Member m) {
            member = m;
            if (isConstructor()) {
                signature = ObjectStreamClass.getSignature((Constructor)m);
            } else {
                signature = ObjectStreamClass.getSignature((Method)m);
            }
        }
    }

    
    private static Method getInheritableMethod(Class<?> cl, String name,
                                               Class<?>[] argTypes,
                                               Class<?> returnType)
    {
        Method meth = null;
        Class<?> defCl = cl;
        while (defCl != null) {
            try {
                meth = defCl.getDeclaredMethod(name, argTypes);
                break;
            } catch (NoSuchMethodException ex) {
                defCl = defCl.getSuperclass();
            }
        }

        if ((meth == null) || (meth.getReturnType() != returnType)) {
            return null;
        }
        int mods = meth.getModifiers();
        if ((mods & (Modifier.STATIC | Modifier.ABSTRACT)) != 0) {
            return null;
        } else if ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
            return meth;
        } else if ((mods & Modifier.PRIVATE) != 0) {
            return (cl == defCl) ? meth : null;
        } else {
            return packageEquals(cl, defCl) ? meth : null;
        }
    }

    
    private static boolean packageEquals(Class<?> cl1, Class<?> cl2) {
        Package pkg1 = cl1.getPackage(), pkg2 = cl2.getPackage();
        return ((pkg1 == pkg2) || ((pkg1 != null) && (pkg1.equals(pkg2))));
    }
}
