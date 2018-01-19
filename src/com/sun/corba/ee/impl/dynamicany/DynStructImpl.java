


package com.sun.corba.ee.impl.dynamicany;



public class DynStructImpl extends DynAnyComplexImpl implements DynStruct
{
    private static final long serialVersionUID = 2832306671453429704L;

    
    
    
    protected DynStructImpl(ORB orb, Any any, boolean copyValue) {
        
        super(orb, any, copyValue);
        
        
    }

    protected DynStructImpl(ORB orb, TypeCode typeCode) {
        
        super(orb, typeCode);
        
        
        
        index = 0;
    }

    
    
    
    public org.omg.DynamicAny.NameValuePair[] get_members () {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkInitComponents();
        return nameValuePairs.clone() ;
    }

    public org.omg.DynamicAny.NameDynAnyPair[] get_members_as_dyn_any () {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        checkInitComponents();
        return nameDynAnyPairs.clone() ;
    }
}
