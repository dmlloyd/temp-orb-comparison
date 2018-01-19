



package xxxx;



public interface Identifiable extends Writeable
{
    
    @ManagedAttribute
    @Description( "Id of tagged component or profile" )
    public int getId();
}
