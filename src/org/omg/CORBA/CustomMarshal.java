

package org.omg.CORBA;

import org.omg.CORBA.DataOutputStream;
import org.omg.CORBA.DataInputStream;


public interface CustomMarshal {
    
    void marshal(DataOutputStream os);
    
    void unmarshal(DataInputStream is);
}
