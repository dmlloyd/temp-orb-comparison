


package xxxx;






@ManagedData
@Description( "Base class for all TaggedComponents" )
@IncludeSubclass( { AlternateIIOPAddressComponent.class, 
    CodeSetsComponent.class, JavaCodebaseComponent.class,
    MaxStreamFormatVersionComponent.class, ORBTypeComponent.class,
    RequestPartitioningComponent.class,
    GenericTaggedComponent.class } )
public interface TaggedComponent extends Identifiable
{
    org.omg.IOP.TaggedComponent getIOPComponent( ORB orb ) ;
}
