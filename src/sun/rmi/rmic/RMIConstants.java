




package sun.rmi.rmic;


public interface RMIConstants extends sun.rmi.rmic.Constants {

    
    public static final Identifier idRemoteObject =
        Identifier.lookup("java.rmi.server.RemoteObject");
    public static final Identifier idRemoteStub =
        Identifier.lookup("java.rmi.server.RemoteStub");
    public static final Identifier idRemoteRef =
        Identifier.lookup("java.rmi.server.RemoteRef");
    public static final Identifier idOperation =
        Identifier.lookup("java.rmi.server.Operation");
    public static final Identifier idSkeleton =
        Identifier.lookup("java.rmi.server.Skeleton");
    public static final Identifier idSkeletonMismatchException =
        Identifier.lookup("java.rmi.server.SkeletonMismatchException");
    public static final Identifier idRemoteCall =
        Identifier.lookup("java.rmi.server.RemoteCall");
    public static final Identifier idMarshalException =
        Identifier.lookup("java.rmi.MarshalException");
    public static final Identifier idUnmarshalException =
        Identifier.lookup("java.rmi.UnmarshalException");
    public static final Identifier idUnexpectedException =
        Identifier.lookup("java.rmi.UnexpectedException");

    
    public static final int STUB_VERSION_1_1  = 1;
    public static final int STUB_VERSION_FAT  = 2;
    public static final int STUB_VERSION_1_2  = 3;

    
    public static final long STUB_SERIAL_VERSION_UID = 2;

    
    public static final int INTERFACE_HASH_STUB_VERSION = 1;
}
