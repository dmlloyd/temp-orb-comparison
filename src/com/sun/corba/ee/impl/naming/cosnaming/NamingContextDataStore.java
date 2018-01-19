


package com.sun.corba.ee.impl.naming.cosnaming;





  
public interface NamingContextDataStore {
    
    void bindImpl(NameComponent n, org.omg.CORBA.Object obj, BindingType bt)
        throws org.omg.CORBA.SystemException;

    
    org.omg.CORBA.Object resolveImpl(NameComponent n,BindingTypeHolder bth)
        throws org.omg.CORBA.SystemException;

    
    org.omg.CORBA.Object unbindImpl(NameComponent n)
        throws org.omg.CORBA.SystemException;

    
    void listImpl(int how_many, BindingListHolder bl, BindingIteratorHolder bi)
        throws org.omg.CORBA.SystemException;

    
    NamingContext newContextImpl()
        throws org.omg.CORBA.SystemException;

    
    void destroyImpl()
        throws org.omg.CORBA.SystemException;
  
    
    boolean isEmptyImpl();

    POA getNSPOA( );
}
