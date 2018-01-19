

package xxxx;



public interface IORTemplate extends List, IORFactory, MakeImmutable {
    
    Iterator iteratorById( int id ) ;

    ObjectKeyTemplate getObjectKeyTemplate() ;
}
