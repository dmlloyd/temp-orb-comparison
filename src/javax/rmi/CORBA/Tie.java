


package javax.rmi.CORBA;




public interface Tie extends org.omg.CORBA.portable.InvokeHandler {
    
    org.omg.CORBA.Object thisObject();

    
    void deactivate() throws java.rmi.NoSuchObjectException;

    
    ORB orb();

    
    void orb(ORB orb);

    
    void setTarget(java.rmi.Remote target);

    
    java.rmi.Remote getTarget();
}
