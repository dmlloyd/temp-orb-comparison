

package com.sun.corba.se.spi.ior ;

import java.util.List ;
import java.util.Iterator ;


public interface IORTemplate extends List, IORFactory, MakeImmutable {
    
    Iterator iteratorById( int id ) ;

    ObjectKeyTemplate getObjectKeyTemplate() ;
}
