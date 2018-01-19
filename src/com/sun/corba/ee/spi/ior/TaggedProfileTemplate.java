


package com.sun.corba.ee.spi.ior;








@ManagedData
@Description( "A template for creating a TaggedProfile" ) 
@IncludeSubclass( { com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate.class } )
public interface TaggedProfileTemplate extends List<TaggedComponent>, 
    Identifiable, WriteContents, MakeImmutable
{    
    @ManagedAttribute
    @Description( "The list of TaggedComponents in this TaggedProfileTemplate" ) 
    public Iterator<TaggedComponent> getTaggedComponents() ;

    
    public Iterator<TaggedComponent> iteratorById( int id ) ;

    public <T extends TaggedComponent> Iterator<T> iteratorById( int id, 
        Class<T> cls )  ;

    
    TaggedProfile create( ObjectKeyTemplate oktemp, ObjectId id ) ;

    
    void write( ObjectKeyTemplate oktemp, ObjectId id, OutputStream os) ;

    
    boolean isEquivalent( TaggedProfileTemplate temp );

    
    org.omg.IOP.TaggedComponent[] getIOPComponents( 
        ORB orb, int id );
}
