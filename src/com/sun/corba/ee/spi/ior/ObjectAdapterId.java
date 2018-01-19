


package com.sun.corba.ee.spi.ior ;




@ManagedData
@Description( "The identifier for a particular Object adapter in the ORB" ) 
public interface ObjectAdapterId extends Iterable<String>, Writeable {
    
    int getNumLevels() ;

    
    @ManagedAttribute
    @Description( "Sequence of strings in the ObjectAdapterId" ) 
    Iterator<String> iterator() ;

    
    String[] getAdapterName() ;
}
