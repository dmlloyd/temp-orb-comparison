


package com.sun.corba.ee.spi.ior;

public interface TaggedComponentFactoryFinder 
    extends IdentifiableFactoryFinder<TaggedComponent>
{
    
    TaggedComponent create( org.omg.CORBA.ORB orb,
        org.omg.IOP.TaggedComponent comp ) ;
}
