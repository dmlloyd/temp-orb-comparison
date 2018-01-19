

package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.NameComponent;


public class InternalBindingValue
{
    public Binding theBinding;
    public String strObjectRef;
    public org.omg.CORBA.Object theObjectRef;

    
    public InternalBindingValue() {}

    
    public InternalBindingValue(Binding b, String o) {
        theBinding = b;
        strObjectRef = o;
    }
}
