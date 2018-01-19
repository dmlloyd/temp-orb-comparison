

package org.omg.CORBA;


@Deprecated
public interface DynValue extends org.omg.CORBA.Object, org.omg.CORBA.DynAny {

    
    String current_member_name();

    
    TCKind current_member_kind();

    
    org.omg.CORBA.NameValuePair[] get_members();

    
    void set_members(NameValuePair[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
