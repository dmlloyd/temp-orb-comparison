

package xxxx;






public interface TaggedProfileTemplate extends List, Identifiable,
    WriteContents, MakeImmutable
{
    
    public Iterator iteratorById( int id ) ;

    
    TaggedProfile create( ObjectKeyTemplate oktemp, ObjectId id ) ;

    
    void write( ObjectKeyTemplate oktemp, ObjectId id, OutputStream os) ;

    
    boolean isEquivalent( TaggedProfileTemplate temp );

    
    org.omg.IOP.TaggedComponent[] getIOPComponents(
        ORB orb, int id );
}
