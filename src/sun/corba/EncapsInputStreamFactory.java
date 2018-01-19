
package xxxx;




public class EncapsInputStreamFactory {

    public static EncapsInputStream newEncapsInputStream(
            final org.omg.CORBA.ORB orb, final byte[] buf, final int size,
            final boolean littleEndian, final GIOPVersion version) {
        return AccessController
                .doPrivileged(new PrivilegedAction<EncapsInputStream>() {
                    @Override
                    public EncapsInputStream run() {
                        return new EncapsInputStream(orb, buf, size,
                                littleEndian, version);
                    }
                });
    }

    public static EncapsInputStream newEncapsInputStream(
            final org.omg.CORBA.ORB orb, final ByteBuffer byteBuffer,
            final int size, final boolean littleEndian,
            final GIOPVersion version) {
        return AccessController
                .doPrivileged(new PrivilegedAction<EncapsInputStream>() {
                    @Override
                    public EncapsInputStream run() {
                        return new EncapsInputStream(orb, byteBuffer, size,
                                littleEndian, version);
                    }
                });
    }

    public static EncapsInputStream newEncapsInputStream(
            final org.omg.CORBA.ORB orb, final byte[] data, final int size) {
        return AccessController
                .doPrivileged(new PrivilegedAction<EncapsInputStream>() {
                    @Override
                    public EncapsInputStream run() {
                        return new EncapsInputStream(orb, data, size);
                    }
                });
    }

    public static EncapsInputStream newEncapsInputStream(
            final EncapsInputStream eis) {
        return AccessController
                .doPrivileged(new PrivilegedAction<EncapsInputStream>() {
                    @Override
                    public EncapsInputStream run() {
                        return new EncapsInputStream(eis);
                    }
                });
    }

    public static EncapsInputStream newEncapsInputStream(
            final org.omg.CORBA.ORB orb, final byte[] data, final int size,
            final GIOPVersion version) {
        return AccessController
                .doPrivileged(new PrivilegedAction<EncapsInputStream>() {
                    @Override
                    public EncapsInputStream run() {
                        return new EncapsInputStream(orb, data, size, version);
                    }
                });
    }

    public static EncapsInputStream newEncapsInputStream(
            final org.omg.CORBA.ORB orb, final byte[] data, final int size,
            final GIOPVersion version, final CodeBase codeBase) {
        return AccessController
                .doPrivileged(new PrivilegedAction<EncapsInputStream>() {
                    @Override
                    public EncapsInputStream run() {
                        return new EncapsInputStream(orb, data, size, version,
                                codeBase);
                    }
                });
    }

    public static TypeCodeInputStream newTypeCodeInputStream(
            final org.omg.CORBA.ORB orb, final byte[] buf, final int size,
            final boolean littleEndian, final GIOPVersion version) {
        return AccessController
                .doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
                    @Override
                    public TypeCodeInputStream run() {
                        return new TypeCodeInputStream(orb, buf, size,
                                littleEndian, version);
                    }
                });
    }

    public static TypeCodeInputStream newTypeCodeInputStream(
            final org.omg.CORBA.ORB orb, final ByteBuffer byteBuffer,
            final int size, final boolean littleEndian,
            final GIOPVersion version) {
        return AccessController
                .doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
                    @Override
                    public TypeCodeInputStream run() {
                        return new TypeCodeInputStream(orb, byteBuffer, size,
                                littleEndian, version);
                    }
                });
    }

    public static TypeCodeInputStream newTypeCodeInputStream(
            final org.omg.CORBA.ORB orb, final byte[] data, final int size) {
        return AccessController
                .doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
                    @Override
                    public TypeCodeInputStream run() {
                        return new TypeCodeInputStream(orb, data, size);
                    }
                });
    }
}
