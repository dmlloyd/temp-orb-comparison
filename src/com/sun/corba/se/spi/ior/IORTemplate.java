

package com.sun.corba.se.spi.ior ;



public interface IORTemplate extends List, IORFactory, MakeImmutable {
    
    Iterator iteratorById( int id ) ;

    ObjectKeyTemplate getObjectKeyTemplate() ;
}
