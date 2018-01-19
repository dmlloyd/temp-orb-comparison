

package com.sun.corba.se.spi.ior;

public interface TaggedComponentFactoryFinder extends IdentifiableFactoryFinder
{
    
    TaggedComponent create( org.omg.CORBA.ORB orb,
        org.omg.IOP.TaggedComponent comp ) ;
}
