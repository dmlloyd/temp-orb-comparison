


package xxxx;



public interface IORTemplate extends List<TaggedProfileTemplate>, 
    IORFactory, MakeImmutable 
{
    
    Iterator<TaggedProfileTemplate> iteratorById( int id ) ;

    ObjectKeyTemplate getObjectKeyTemplate() ;
}
