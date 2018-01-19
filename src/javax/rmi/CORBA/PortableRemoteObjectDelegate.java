


package javax.rmi.CORBA;

import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;


public interface PortableRemoteObjectDelegate {

    
    void exportObject(Remote obj)
        throws RemoteException;

    
    Remote toStub (Remote obj)
        throws NoSuchObjectException;

    
    void unexportObject(Remote obj)
        throws NoSuchObjectException;

    
    java.lang.Object narrow (java.lang.Object narrowFrom,
                                    java.lang.Class<?> narrowTo)
        throws ClassCastException;

    
    void connect (Remote target, Remote source)
        throws RemoteException;

}
