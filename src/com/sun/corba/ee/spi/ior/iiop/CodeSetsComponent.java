


package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedComponent ;

import com.sun.corba.ee.impl.encoding.CodeSetComponentInfo ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;



@ManagedData
@Description( "The character codesets to be used for encoding "
    + "strings sent to the object reference represented by "
    + "this IOR" ) 
public interface CodeSetsComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "The codeset component info" ) 
    
    public CodeSetComponentInfo getCodeSetComponentInfo() ;
}
