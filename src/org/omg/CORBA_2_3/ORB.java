


package org.omg.CORBA_2_3;


public abstract class ORB extends org.omg.CORBA.ORB {


    public org.omg.CORBA.portable.ValueFactory register_value_factory(String id,
                                                                     org.omg.CORBA.portable.ValueFactory factory)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }



    public void unregister_value_factory(String id)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }



    public org.omg.CORBA.portable.ValueFactory lookup_value_factory(String id)
    {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }



    
     
     public org.omg.CORBA.Object get_value_def(String repid)
                               throws org.omg.CORBA.BAD_PARAM {
       throw new org.omg.CORBA.NO_IMPLEMENT();
     }



     public void set_delegate(java.lang.Object wrapper) {
       throw new org.omg.CORBA.NO_IMPLEMENT();
     }


}
