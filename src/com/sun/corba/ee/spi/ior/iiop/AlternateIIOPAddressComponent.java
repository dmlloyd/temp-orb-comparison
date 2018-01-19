


package xxxx;




@ManagedData
@Description( "Component containing an alternate IIOP address to use" )
public interface AlternateIIOPAddressComponent extends TaggedComponent
{
    @ManagedAttribute
    @Description( "The Alternate address" ) 
    public IIOPAddress getAddress() ;
}
