


package com.sun.corba.ee.impl.dynamicany;



public class DynFixedImpl extends DynAnyBasicImpl implements DynFixed
{
    private static final long serialVersionUID = -426296363713464920L;
    
    
    

    private DynFixedImpl() {
        this(null, (Any)null, false);
    }

    protected DynFixedImpl(ORB orb, Any any, boolean copyValue) {
        super(orb, any, copyValue);
    }

    
    protected DynFixedImpl(ORB orb, TypeCode typeCode) {
        super(orb, typeCode);
        index = NO_INDEX;
    }

    
    
    

    
    
    

    public String get_value () {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        return any.extract_fixed().toString();
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public boolean set_value (String val)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue
    {
        if (status == STATUS_DESTROYED) {
            throw wrapper.dynAnyDestroyed() ;
        }
        int digits = 0;
        boolean preservedPrecision = true;
        try {
            digits = any.type().fixed_digits();
        } catch (BadKind ex) { 
        }
        
        String string = val.trim();
        if (string.length() == 0) {
            throw new TypeMismatch();
        }
        
        String sign = "";
        if (string.charAt(0) == '-') {
            sign = "-";
            string = string.substring(1);
        } else if (string.charAt(0) == '+') {
            sign = "+";
            string = string.substring(1);
        }
        
        int dIndex = string.indexOf('d');
        if (dIndex == -1) {
            dIndex = string.indexOf('D');
        }
        if (dIndex != -1) {
            string = string.substring(0, dIndex);
        }
        
        if (string.length() == 0) {
            throw new TypeMismatch();
        }
        
        String integerPart;
        String fractionPart;
        int currentScale;
        int currentDigits;
        int dotIndex = string.indexOf('.');
        if (dotIndex == -1) {
            integerPart = string;
            fractionPart = null;
            currentScale = 0;
            currentDigits = integerPart.length();
        } else if (dotIndex == 0 ) {
            integerPart = null;
            fractionPart = string;
            currentScale = fractionPart.length();
            currentDigits = currentScale;
        } else {
            integerPart = string.substring(0, dotIndex);
            fractionPart = string.substring(dotIndex + 1);
            currentScale = fractionPart.length();
            currentDigits = integerPart.length() + currentScale;
        }

        int integerPartLength = (integerPart == null) ? 0 
            : integerPart.length() ;    
        
        
        if (currentDigits > digits) {
            preservedPrecision = false;
            
            if (integerPartLength < digits) {
                fractionPart = fractionPart.substring(0, digits - integerPartLength ) ;
            } else if (integerPartLength == digits) {
                
                
                fractionPart = null;
            } else {
                
                
                throw new InvalidValue();
            }
        }
        
        
        
        

        
        BigDecimal result;
        try {
            if (fractionPart == null) {
                result = new BigDecimal(sign + integerPart);
            } else {
                result = new BigDecimal(sign + integerPart + "." + fractionPart);
            }
        } catch (NumberFormatException nfe) {
            throw new TypeMismatch();
        }
        any.insert_fixed(result, any.type());
        return preservedPrecision;
    }

    @Override
    public String toString() {
        int digits = 0;
        int scale = 0;
        try {
            digits = any.type().fixed_digits();
            scale = any.type().fixed_scale();
        } catch (BadKind ex) { 
        }
        return "DynFixed with value=" + this.get_value() + ", digits=" + digits + ", scale=" + scale;
    }
}
