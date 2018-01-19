


package com.sun.corba.ee.spi.ior ;

import java.util.List ;
import java.util.Iterator ;


public interface IORTemplate extends List<TaggedProfileTemplate>, 
    IORFactory, MakeImmutable 
{
    
    Iterator<TaggedProfileTemplate> iteratorById( int id ) ;

    ObjectKeyTemplate getObjectKeyTemplate() ;
}
