


package com.sun.corba.ee.impl.dynamicany;




public class DynAnyFactoryImpl
    extends org.omg.CORBA.LocalObject
    implements org.omg.DynamicAny.DynAnyFactory
{
    private static final long serialVersionUID = 5207021167805787406L;

    
    
    

    private transient ORB orb;

    
    
    

    private DynAnyFactoryImpl() {
        this.orb = null;
    }

    public DynAnyFactoryImpl(ORB orb) {
        this.orb = orb;
    }

    
    private void readObject( ObjectInputStream is ) throws IOException,
        ClassNotFoundException {
        this.orb = null ;
    }
    
    
    

    
    public org.omg.DynamicAny.DynAny create_dyn_any (org.omg.CORBA.Any any)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode
    {
        return DynAnyUtil.createMostDerivedDynAny(any, orb, true);
    }

    
    public org.omg.DynamicAny.DynAny create_dyn_any_from_type_code (org.omg.CORBA.TypeCode type)
        throws org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode
    {
        return DynAnyUtil.createMostDerivedDynAny(type, orb);
    }

    

    private final String[] __ids = { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };

    public String[] _ids() {
        return __ids.clone() ;
    }
}
