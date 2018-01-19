


package xxxx;



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
