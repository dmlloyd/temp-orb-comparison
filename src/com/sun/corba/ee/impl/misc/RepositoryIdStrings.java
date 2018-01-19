


package com.sun.corba.ee.impl.misc;

import java.io.Serializable;
import com.sun.corba.ee.impl.io.TypeMismatchException;

import com.sun.corba.ee.impl.misc.ClassInfoCache ;


public interface RepositoryIdStrings
{
    String createForAnyType(Class type);

    String createForAnyType(Class type, ClassInfoCache.ClassInfo cinfo );
    
    String createForJavaType(Serializable ser)
        throws TypeMismatchException;
    
    String createForJavaType(Class clz)
        throws TypeMismatchException;
    
    String createForJavaType(Class clz, ClassInfoCache.ClassInfo cinfo )
        throws TypeMismatchException;
    
    String createSequenceRepID(java.lang.Object ser);
    
    String createSequenceRepID(java.lang.Class clazz);
    
    RepositoryIdInterface getFromString(String repIdString);

    String getClassDescValueRepId();
    String getWStringValueRepId();
}
