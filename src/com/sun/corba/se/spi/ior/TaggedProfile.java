

package com.sun.corba.se.spi.ior;



public interface TaggedProfile extends Identifiable, MakeImmutable
{
    TaggedProfileTemplate getTaggedProfileTemplate() ;

    ObjectId getObjectId() ;

    ObjectKeyTemplate getObjectKeyTemplate() ;

    ObjectKey getObjectKey() ;

    
    boolean isEquivalent( TaggedProfile prof ) ;

    
    org.omg.IOP.TaggedProfile getIOPProfile();

    
    boolean isLocal() ;
}
