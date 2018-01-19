


package com.sun.org.omg.CORBA.portable;



@Deprecated
public interface ValueHelper extends BoxedValueHelper {
    Class get_class();
    String[] get_truncatable_base_ids();
    TypeCode get_type();
}

