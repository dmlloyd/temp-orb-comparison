


package javax.rmi.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;


public interface StubDelegate {

    
    int hashCode(Stub self);

    
    boolean equals(Stub self, java.lang.Object obj);

    
    String toString(Stub self);

    
    void connect(Stub self, ORB orb)
        throws RemoteException;

    
    
    void readObject(Stub self, ObjectInputStream s)
        throws IOException, ClassNotFoundException;

    
    
    void writeObject(Stub self, ObjectOutputStream s)
        throws IOException;

}
