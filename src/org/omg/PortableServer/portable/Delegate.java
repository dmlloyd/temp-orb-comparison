

package xxxx;



public interface Delegate {

    org.omg.CORBA.ORB orb(Servant Self);


    org.omg.CORBA.Object this_object(Servant Self);


    POA poa(Servant Self);


    byte[] object_id(Servant Self);


    POA default_POA(Servant Self);


    boolean is_a(Servant Self, String Repository_Id);


    boolean non_existent(Servant Self);
    
    


    
    

    org.omg.CORBA.Object get_interface_def(Servant self);
}

