

package xxxx;



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
