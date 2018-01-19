




package xxxx;




public class ObjectStreamField implements Comparable 
{
    private static final Bridge bridge = 
        AccessController.doPrivileged(
            new PrivilegedAction<Bridge>() {
                public Bridge run() {
                    return Bridge.get() ;
                }
            } 
        ) ;

    
    public ObjectStreamField(String n, Class clazz) {
        name = n;
        this.clazz = clazz;
        cinfo = ClassInfoCache.get( clazz ) ;

        
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                type = 'I';
            } else if (clazz == Byte.TYPE) {
                type = 'B';
            } else if (clazz == Long.TYPE) {
                type = 'J';
            } else if (clazz == Float.TYPE) {
                type = 'F';
            } else if (clazz == Double.TYPE) {
                type = 'D';
            } else if (clazz == Short.TYPE) {
                type = 'S';
            } else if (clazz == Character.TYPE) {
                type = 'C';
            } else if (clazz == Boolean.TYPE) {
                type = 'Z';
            }
        } else if (cinfo.isArray()) {
            type = '[';
            typeString = ObjectStreamClass.getSignature(clazz);
        } else {
            type = 'L';
            typeString = ObjectStreamClass.getSignature(clazz);
        }

        if (typeString != null)
            signature = typeString;
        else
            signature = String.valueOf(type);

    }

    public ObjectStreamField(Field field) {
        this(field.getName(), field.getType());
        setField( field ) ;
    }

    
    public String getName() {
        return name;
    }

    public ClassInfoCache.ClassInfo getClassInfo() {
        return cinfo ;
    }

    
    public Class getType() {
        if (clazz != null)
            return clazz;
        switch (type) {
        case 'B': clazz = Byte.TYPE;
            break;
        case 'C': clazz = Character.TYPE;
            break;
        case 'S': clazz = Short.TYPE;
            break;
        case 'I': clazz = Integer.TYPE;
            break;
        case 'J': clazz = Long.TYPE;
            break;
        case 'F': clazz = Float.TYPE;
            break;
        case 'D': clazz = Double.TYPE;
            break;
        case 'Z': clazz = Boolean.TYPE;
            break;
        case '[':
        case 'L':
            clazz = Object.class;
            break;
        }

        return clazz;
    }

    public char getTypeCode() {
        return type;
    }

    public String getTypeString() {
        return typeString;
    }

    Field getField() {
        return field;
    }

    void setField(Field field) {
        this.field = field;
        this.fieldID = bridge.objectFieldOffset( field ) ;
    }


    
    public boolean isPrimitive() {
        return (type != '[' && type != 'L');
    }

    
    public int compareTo(Object o) {
        ObjectStreamField f2 = (ObjectStreamField)o;
        boolean thisprim = (this.typeString == null);
        boolean otherprim = (f2.typeString == null);

        if (thisprim != otherprim) {
            return (thisprim ? -1 : 1);
        }
        return this.name.compareTo(f2.name);
    }

    
    public boolean typeEquals(ObjectStreamField other) {
        if (other == null || type != other.type)
            return false;

        
        if (typeString == null && other.typeString == null)
            return true;

        return ObjectStreamClass.compareClassNames(typeString,
                                                   other.typeString,
                                                   '/');
    }

    
    public String getSignature() {

        return signature;

    }

    
    public String toString() {
        if (typeString != null)
            return typeString + " " + name;
        else
            return type + " " + name;
    }

    public Class getClazz() {
        return clazz;
    }

    
    public long getFieldID() {
        return fieldID ;
    }

    private String name;                
    private char type;                  
    private Field field;                
    private String typeString;          
    private Class clazz;                
    private ClassInfoCache.ClassInfo cinfo ;

    
    
    
    

    private String signature;   
    private long fieldID = Bridge.INVALID_FIELD_OFFSET ; 
}
