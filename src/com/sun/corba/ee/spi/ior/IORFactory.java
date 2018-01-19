


package xxxx;



public interface IORFactory extends Writeable, MakeImmutable {
    
    IOR makeIOR( ORB orb, String typeid, ObjectId oid ) ;

    
    boolean isEquivalent( IORFactory other ) ;
}
