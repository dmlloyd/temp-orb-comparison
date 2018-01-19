

package com.sun.corba.se.impl.naming.pcosnaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.BindingType;
import java.io.Serializable;


public class InternalBindingValue
                implements Serializable
{
    public BindingType theBindingType;
    
    
    
    
    public String strObjectRef;
    transient private org.omg.CORBA.Object theObjectRef;

    
    public InternalBindingValue() {
    }

    
    public InternalBindingValue(BindingType b, String o) {
        
        theBindingType = b;
        strObjectRef = o;
    }

    public org.omg.CORBA.Object getObjectRef( )
    {
        return theObjectRef;
    }

    public void setObjectRef( org.omg.CORBA.Object ObjectRef )
    {
        theObjectRef = ObjectRef;
    }

}
