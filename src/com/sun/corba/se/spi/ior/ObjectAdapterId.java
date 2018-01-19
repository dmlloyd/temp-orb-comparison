

package com.sun.corba.se.spi.ior ;



public interface ObjectAdapterId extends Writeable {
    
    int getNumLevels() ;

    
    Iterator iterator() ;

    
    String[] getAdapterName() ;
}
