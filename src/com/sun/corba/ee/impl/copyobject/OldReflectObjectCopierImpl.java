




package xxxx;




public class OldReflectObjectCopierImpl implements ObjectCopier 
{
    private IdentityHashMap objRefs;
    private ORB orb ;

    public OldReflectObjectCopierImpl( org.omg.CORBA.ORB orb ) 
    {
        objRefs = new IdentityHashMap();
        this.orb = (ORB)orb ;
    }

    
    private static Map reflectCache = new HashMap();

    
    class ReflectAttrs {
        public Field[] fields;
        public Constructor constr;
        public Class thisClass ;
        public Class arrayClass;
        public Class superClass;
        public boolean isImmutable;
        public boolean isDate;
        public boolean isSQLDate;

        public ReflectAttrs(Class cls) {
            thisClass = cls ;
            String name = cls.getName();
            char ch = name.charAt(0);

            isImmutable = false;
            isDate = false;
            isSQLDate = false; 
            fields = null;
            constr = null;
            superClass = null;
            if (ch == '[') {
                arrayClass = cls.getComponentType();
            } else if (isImmutable(name)) {
                isImmutable = true;
            } else if (name.equals("java.util.Date")) {
                isDate = true;
            } else if (name.equals("java.sql.Date")) {
                isSQLDate = true;
            } else {
                if (Externalizable.class.isAssignableFrom( cls ))
                    constr = getExternalizableConstructor(cls) ;
                else if (Serializable.class.isAssignableFrom( cls ))
                    constr = getSerializableConstructor(cls) ;
                if (constr != null) { constr.setAccessible(true); }    
                fields = cls.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);
                superClass = cls.getSuperclass();
            }
        }
    };

    
    private static final Bridge bridge = 
        (Bridge)AccessController.doPrivileged(
            new PrivilegedAction() {
                public Object run() {
                    return Bridge.get() ;
                }
            } 
        ) ;

    
    private Constructor getExternalizableConstructor(Class cl) {
        try {
            Constructor cons = cl.getDeclaredConstructor(new Class[0]);
            cons.setAccessible(true);
            return ((cons.getModifiers() & Modifier.PUBLIC) != 0) ?  cons : null;
        } catch (NoSuchMethodException ex) {
            
            return null;
        }
    }

   
    private boolean packageEquals(Class cl1, Class cl2) {
        Package pkg1 = cl1.getPackage(), pkg2 = cl2.getPackage();
        return ((pkg1 == pkg2) || ((pkg1 != null) && (pkg1.equals(pkg2))));
    }

    
    private Constructor getSerializableConstructor(Class cl) {
        Class initCl = cl;
        if (initCl == null) {
            
            
            return null;
        }
        while (Serializable.class.isAssignableFrom(initCl)) {
            if ((initCl = initCl.getSuperclass()) == null) {
                return null;
            }
        }
        try {
            Constructor cons = initCl.getDeclaredConstructor(new Class[0]);
            int mods = cons.getModifiers();
            if ((mods & Modifier.PRIVATE) != 0 ||
                ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0 &&
                 !packageEquals(cl, initCl)))
            {
               
               return null;
            }
            cons = bridge.newConstructorForSerialization(cl, cons);
            cons.setAccessible(true);
            return cons;
        } catch (NoSuchMethodException ex) {
            
            return null;
        }
    }

    
    private final synchronized ReflectAttrs getClassAttrs(Class cls) {
        ReflectAttrs attrs = null;

        attrs = (ReflectAttrs)reflectCache.get(cls);
        if (attrs == null) {
            attrs = new ReflectAttrs(cls);
            reflectCache.put(cls, (Object)attrs);
        }
        return attrs;
    }

    public static boolean isImmutable(String classname) {
        if (classname.startsWith("java.lang.")) {
            String typename = classname.substring(10);
            if (typename.compareTo("String") == 0 ||
                typename.compareTo("Class") == 0 ||
                typename.compareTo("Integer") == 0 ||
                typename.compareTo("Boolean") == 0 ||
                typename.compareTo("Long") == 0 ||
                typename.compareTo("Double") == 0 ||
                typename.compareTo("Byte") == 0 ||
                typename.compareTo("Char") == 0 ||
                typename.compareTo("Short") == 0 ||
                typename.compareTo("Object") == 0 ||
                typename.compareTo("Float") == 0) {
                return true;
            }
        }
        return false;
    }

    
    private final Object arrayCopy(Object obj, Class aClass) 
        throws RemoteException, InstantiationException, 
        IllegalAccessException, InvocationTargetException
    {
        Object acopy = null;

        if (aClass.isPrimitive()) {
            if (aClass == byte.class) {
                acopy = ((byte[])obj).clone();
            } else if (aClass == char.class) {
                acopy = ((char[])obj).clone();
            } else if (aClass == short.class) {
                acopy = ((short[])obj).clone();
            } else if (aClass == int.class) {
                acopy = ((int[])obj).clone();
            } else if (aClass == long.class) {
                acopy = ((long[])obj).clone();
            } else if (aClass == double.class) {
                acopy = ((double[])obj).clone();
            } else if (aClass == float.class) {
                acopy = ((float[])obj).clone();
            } else if (aClass == boolean.class) {
                acopy = ((boolean[])obj).clone();
            }
            objRefs.put(obj, acopy);
        } else if (aClass == String.class) {
            acopy = ((String [])obj).clone();
            objRefs.put(obj, acopy);
        } else {
            int alen = Array.getLength(obj);

            aClass = obj.getClass().getComponentType();

            acopy = Array.newInstance(aClass, alen);


                objRefs.put(obj, acopy);
                for (int idx=0; idx<alen; idx++) {
                    Object aobj = Array.get(obj, idx);
                    aobj = reflectCopy(aobj);
                    Array.set(acopy, idx, aobj);
                }
        }

        return acopy;
    }

    
    private final void copyFields(Class cls, Field[] fields, Object obj, 
        Object copy) throws RemoteException, IllegalAccessException,
        InstantiationException, InvocationTargetException
    {
        if (fields == null || fields.length == 0) {
            return;
        }

        
        for (int idx=0; idx<fields.length; idx++) {
            Field fld = fields[idx];
            int modifiers = fld.getModifiers() ;
            Object fobj = null;
            Class fieldClass = fld.getType();

            if (!Modifier.isStatic(modifiers)) {
                if (fieldClass == int.class) {
                    fld.setInt(copy, fld.getInt(obj));
                } else if (fieldClass == long.class) {
                    fld.setLong(copy, fld.getLong(obj));
                } else if (fieldClass == double.class) {
                    fld.setDouble(copy, fld.getDouble(obj));
                } else if (fieldClass == byte.class) {
                    fld.setByte(copy, fld.getByte(obj));
                } else if (fieldClass == char.class) {
                    fld.setChar(copy, fld.getChar(obj));
                } else if (fieldClass == short.class) {
                    fld.setShort(copy, fld.getShort(obj));
                } else if (fieldClass == float.class) {
                    fld.setFloat(copy, fld.getFloat(obj));
                } else if (fieldClass == boolean.class) {
                    fld.setBoolean(copy, fld.getBoolean(obj));
                } else {
                    fobj = fld.get(obj);
                    Object newfobj = reflectCopy(fobj);
                    fld.set(copy, newfobj);
                }
            }
        }
    }


    
    
    
    private Object makeInstanceOfClass (Class cls) 
        throws IllegalAccessException, InstantiationException
    {
        return cls.newInstance() ;
    }

    
    private Object copyMap( Object obj ) 
        throws RemoteException, InstantiationException, IllegalAccessException,
        InvocationTargetException
    {
        Map src = (Map)obj ;
        Map result = (Map)makeInstanceOfClass( src.getClass() ) ;
        
        objRefs.put( src, result ) ;  
        Iterator iter = src.entrySet().iterator() ;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)(iter.next());
            Object key = entry.getKey();
            Object value = entry.getValue() ;
            
            Object newKey = reflectCopy( key) ;
            Object newValue = reflectCopy( value) ;
            result.put( newKey, newValue ) ;
        }

        return result ;
    }

    
    private Object copyAnyClass( ReflectAttrs attrs, Object obj ) 
        throws RemoteException, InstantiationException, 
        IllegalAccessException, InvocationTargetException
    {
        
        Constructor cons = attrs.constr;
        if (cons == null)
            throw new IllegalArgumentException( "Class " + attrs.thisClass +
                 " is not Serializable" ) ;

        Object copy = cons.newInstance();

        
        objRefs.put(obj, copy);
        copyFields(attrs.thisClass, attrs.fields, obj, copy);
        Class cls = attrs.superClass;
        while (cls != null && cls != Object.class) {
            attrs = getClassAttrs(cls);
            copyFields(cls, attrs.fields, obj, copy);
            cls = attrs.superClass;
        } 

        return copy ;
    }

    
    private final Object reflectCopy(Object obj) 
        throws RemoteException, InstantiationException, 
        IllegalAccessException, InvocationTargetException
    {
        
        if (obj == null)
            return null ;

        Class cls = obj.getClass() ;
        ReflectAttrs attrs = getClassAttrs( cls ) ;

        Object copy = null;

        if (attrs.isImmutable || (obj instanceof org.omg.CORBA.Object)) {
            return obj;
        }

        if (obj instanceof Remote) {
            return Utility.autoConnect(obj, orb, true);
        }

        copy = objRefs.get(obj);
        if (copy == null) {
            
            
            if ( ( cls.getName().equals("java.util.HashMap") ) ||
                 ( cls.getName().equals("java.util.HashTable") ) ) {
                copy = copyMap( obj ) ;
            } else {
                Class aClass = attrs.arrayClass;

                if (aClass != null) {
                    
                    copy = arrayCopy(obj, aClass);
                } else {
                    if (attrs.isDate) {
                        copy = new java.util.Date(((java.util.Date)obj).getTime());
                        objRefs.put(obj, copy);
                    } else if (attrs.isSQLDate) {
                        copy = new java.sql.Date(((java.sql.Date)obj).getTime());
                        objRefs.put(obj, copy);
                    } else {
                        copy = copyAnyClass( attrs, obj ) ;
                    }
                }
            }
        }

        return copy;
    }

    
    
    
    
    public Object copy(final Object obj, boolean debug ) throws ReflectiveCopyException
    {
        return copy( obj ) ;
    }

    public Object copy(final Object obj) throws ReflectiveCopyException
    {
        try {
            return AccessController.doPrivileged(
                new PrivilegedExceptionAction() {
                    public Object run() throws RemoteException, InstantiationException, 
                        IllegalAccessException, InvocationTargetException
                    {
                        return reflectCopy(obj);
                    }
                } 
            ) ;
        } catch (ThreadDeath td) {
            throw td ;
        } catch (Throwable thr) {
            throw new ReflectiveCopyException( "Could not copy object of class " + 
                obj.getClass().getName(), thr ) ;
        }
    }
}

