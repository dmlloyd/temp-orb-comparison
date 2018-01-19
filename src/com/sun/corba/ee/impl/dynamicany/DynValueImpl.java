


package com.sun.corba.ee.impl.dynamicany;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;

import com.sun.corba.ee.spi.orb.ORB ;
import org.omg.DynamicAny.DynValue;

public class DynValueImpl extends DynValueCommonImpl implements DynValue
{
    private static final long serialVersionUID = 4860224542389276556L;
    
    
    

    private DynValueImpl() {
        this(null, (Any)null, false);
    }

    protected DynValueImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
    }

    protected DynValueImpl(ORB orb, TypeCode typeCode) {
        super(orb, typeCode);
    }
}
