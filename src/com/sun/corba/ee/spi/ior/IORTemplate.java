


package com.sun.corba.ee.spi.ior ;



public interface IORTemplate extends List<TaggedProfileTemplate>, 
    IORFactory, MakeImmutable 
{
    
    Iterator<TaggedProfileTemplate> iteratorById( int id ) ;

    ObjectKeyTemplate getObjectKeyTemplate() ;
}
