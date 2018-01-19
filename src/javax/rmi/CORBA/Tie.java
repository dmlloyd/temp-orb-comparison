


package javax.rmi.CORBA;

import java.rmi.Remote;
import java.util.Hashtable;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.ORB;


public interface Tie extends org.omg.CORBA.portable.InvokeHandler {
    
    org.omg.CORBA.Object thisObject();

    
    void deactivate() throws java.rmi.NoSuchObjectException;

    
    ORB orb();

    
    void orb(ORB orb);

    
    void setTarget(java.rmi.Remote target);

    
    java.rmi.Remote getTarget();
}
