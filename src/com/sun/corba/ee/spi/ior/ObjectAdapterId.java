


package com.sun.corba.ee.spi.ior ;

import java.util.Iterator ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "The identifier for a particular Object adapter in the ORB" ) 
public interface ObjectAdapterId extends Iterable<String>, Writeable {
    
    int getNumLevels() ;

    
    @ManagedAttribute
    @Description( "Sequence of strings in the ObjectAdapterId" ) 
    Iterator<String> iterator() ;

    
    String[] getAdapterName() ;
}
