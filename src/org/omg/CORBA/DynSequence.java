


package org.omg.CORBA;


@Deprecated
public interface DynSequence extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{

    
    public int length();

    
    public void length(int arg);

    
    public org.omg.CORBA.Any[] get_elements();

    
    public void set_elements(org.omg.CORBA.Any[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
