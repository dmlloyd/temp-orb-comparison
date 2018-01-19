

package sun.corba ;

import java.io.OptionalDataException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field ;
import java.lang.reflect.Constructor ;
import java.lang.StackWalker;
import java.lang.StackWalker.StackFrame;
import java.util.Optional;
import java.util.stream.Stream;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;


public final class Bridge
{
    private static final Permission getBridgePermission =
            new BridgePermission("getBridge");
    private static Bridge bridge = null ;

    
    private static final Unsafe unsafe = AccessController.doPrivileged(
            (PrivilegedAction<Unsafe>)() -> {
                try {
                    Field field = Unsafe.class.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    return (Unsafe)field.get(null);

                } catch (NoSuchFieldException |IllegalAccessException ex) {
                    throw new InternalError("Unsafe.theUnsafe field not available", ex);
                }
            }
    ) ;

    private final ReflectionFactory reflectionFactory ;
    private final StackWalker stackWalker;

    private Bridge() {
        reflectionFactory = ReflectionFactory.getReflectionFactory();
        stackWalker  = StackWalker.getInstance(
                            StackWalker.Option.RETAIN_CLASS_REFERENCE);
    }

    
    public static final synchronized Bridge get()
    {
        SecurityManager sman = System.getSecurityManager() ;
        if (sman != null)
            sman.checkPermission( getBridgePermission ) ;

        if (bridge == null) {
            bridge = new Bridge() ;
        }

        return bridge ;
    }

    
    private boolean isUserLoader(StackFrame sf) {
        ClassLoader cl = sf.getDeclaringClass().getClassLoader();
        if (cl == null) return false;
        ClassLoader p = ClassLoader.getPlatformClassLoader();
        while (cl != p && p != null) p = p.getParent();
        return cl != p;
    }

    private Optional<StackFrame> getLatestUserDefinedLoaderFrame(Stream<StackFrame> stream) {
        return stream.filter(this::isUserLoader).findFirst();
    }


    
    public final ClassLoader getLatestUserDefinedLoader() {
        
        PrivilegedAction<ClassLoader> pa = () ->
            stackWalker.walk(this::getLatestUserDefinedLoaderFrame)
                .map(sf -> sf.getDeclaringClass().getClassLoader())
                .orElseGet(() -> ClassLoader.getPlatformClassLoader());
        return AccessController.doPrivileged(pa);
    }

    
    public final int getInt(Object o, long offset)
    {
        return unsafe.getInt( o, offset ) ;
    }

    
    public final void putInt(Object o, long offset, int x)
    {
        unsafe.putInt( o, offset, x ) ;
    }

    
    public final Object getObject(Object o, long offset)
    {
        return unsafe.getObject( o, offset ) ;
    }

    
    public final void putObject(Object o, long offset, Object x)
    {
        unsafe.putObject( o, offset, x ) ;
    }

    
    public final boolean getBoolean(Object o, long offset)
    {
        return unsafe.getBoolean( o, offset ) ;
    }
    
    public final void    putBoolean(Object o, long offset, boolean x)
    {
        unsafe.putBoolean( o, offset, x ) ;
    }
    
    public final byte    getByte(Object o, long offset)
    {
        return unsafe.getByte( o, offset ) ;
    }
    
    public final void    putByte(Object o, long offset, byte x)
    {
        unsafe.putByte( o, offset, x ) ;
    }
    
    public final short   getShort(Object o, long offset)
    {
        return unsafe.getShort( o, offset ) ;
    }
    
    public final void    putShort(Object o, long offset, short x)
    {
        unsafe.putShort( o, offset, x ) ;
    }
    
    public final char    getChar(Object o, long offset)
    {
        return unsafe.getChar( o, offset ) ;
    }
    
    public final void    putChar(Object o, long offset, char x)
    {
        unsafe.putChar( o, offset, x ) ;
    }
    
    public final long    getLong(Object o, long offset)
    {
        return unsafe.getLong( o, offset ) ;
    }
    
    public final void    putLong(Object o, long offset, long x)
    {
        unsafe.putLong( o, offset, x ) ;
    }
    
    public final float   getFloat(Object o, long offset)
    {
        return unsafe.getFloat( o, offset ) ;
    }
    
    public final void    putFloat(Object o, long offset, float x)
    {
        unsafe.putFloat( o, offset, x ) ;
    }
    
    public final double  getDouble(Object o, long offset)
    {
        return unsafe.getDouble( o, offset ) ;
    }
    
    public final void    putDouble(Object o, long offset, double x)
    {
        unsafe.putDouble( o, offset, x ) ;
    }

    
    public static final long INVALID_FIELD_OFFSET   = -1;

    
    public final long objectFieldOffset(Field f)
    {
        return unsafe.objectFieldOffset( f ) ;
    }

    
    public final long staticFieldOffset(Field f)
    {
        return unsafe.staticFieldOffset( f ) ;
    }

    
    public final void ensureClassInitialized(Class<?> cl) {
        unsafe.ensureClassInitialized(cl);
    }


    
    public final void throwException(Throwable ee)
    {
        unsafe.throwException( ee ) ;
    }

    
    public final Constructor<?> newConstructorForSerialization( Class<?> cl ) {
        return reflectionFactory.newConstructorForSerialization( cl ) ;
    }

    public final Constructor<?> newConstructorForExternalization(Class<?> cl) {
        return reflectionFactory.newConstructorForExternalization( cl ) ;
    }

    
    public final boolean hasStaticInitializerForSerialization(Class<?> cl) {
        return reflectionFactory.hasStaticInitializerForSerialization(cl);
    }

    public final MethodHandle writeObjectForSerialization(Class<?> cl) {
        return reflectionFactory.writeObjectForSerialization(cl);
    }

    public final MethodHandle readObjectForSerialization(Class<?> cl) {
        return reflectionFactory.readObjectForSerialization(cl);
    }

    public final MethodHandle readObjectNoDataForSerialization(Class<?> cl) {
        return reflectionFactory.readObjectNoDataForSerialization(cl);
    }

    public final MethodHandle readResolveForSerialization(Class<?> cl) {
        return reflectionFactory.readResolveForSerialization(cl);
    }

    public final MethodHandle writeReplaceForSerialization(Class<?> cl) {
        return reflectionFactory.writeReplaceForSerialization(cl);
    }

    
    public final OptionalDataException newOptionalDataExceptionForSerialization(boolean bool) {
        return reflectionFactory.newOptionalDataExceptionForSerialization(bool);
    }

}
