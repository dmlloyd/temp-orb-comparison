


package com.sun.corba.ee.spi.ior.iiop;






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
