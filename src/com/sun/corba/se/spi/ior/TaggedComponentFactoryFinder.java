

package xxxx;

public interface TaggedComponentFactoryFinder extends IdentifiableFactoryFinder
{
    
    TaggedComponent create( org.omg.CORBA.ORB orb,
        org.omg.IOP.TaggedComponent comp ) ;
}
