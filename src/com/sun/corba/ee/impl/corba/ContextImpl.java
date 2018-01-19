



package xxxx;



public final class ContextImpl extends Context {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private org.omg.CORBA.ORB _orb;

    public ContextImpl(org.omg.CORBA.ORB orb) 
    {
        _orb = orb;
    }

    public ContextImpl(Context parent) 
    {
        
    }
    
    public String context_name() 
    {
        throw wrapper.contextNotImplemented() ;
    }

    public Context parent() 
    {
        throw wrapper.contextNotImplemented() ;
    }

    public Context create_child(String name) 
    {
        throw wrapper.contextNotImplemented() ;
    }

    public void set_one_value(String propName, Any propValue) 
    {
        throw wrapper.contextNotImplemented() ;
    }

    public void set_values(NVList values) 
    {
        throw wrapper.contextNotImplemented() ;
    }


    public void delete_values(String propName) 
    {
        throw wrapper.contextNotImplemented() ;
    }

    public NVList get_values(String startScope, 
                             int opFlags, 
                             String propName) 
    {
        throw wrapper.contextNotImplemented() ;
    }
};

