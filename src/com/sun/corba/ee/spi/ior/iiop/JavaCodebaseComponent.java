


package xxxx;




@ManagedData
@Description( "Component representing Codebase URLs for downloading code" )
public interface JavaCodebaseComponent extends TaggedComponent 
{
    @ManagedAttribute
    @Description( "List of URLs in the codebase" ) 
    public String getURLs() ;
}
