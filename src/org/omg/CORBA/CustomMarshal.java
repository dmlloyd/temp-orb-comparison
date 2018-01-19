

package org.omg.CORBA;



public interface CustomMarshal {
    
    void marshal(DataOutputStream os);
    
    void unmarshal(DataInputStream is);
}
