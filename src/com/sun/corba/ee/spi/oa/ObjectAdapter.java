


package xxxx;








@ManagedObject
@Description( "ObjectAdapter used to dispatch requests and manage servants")
public interface ObjectAdapter 
{
    
    
    

    
    ORB getORB() ;

    Policy getEffectivePolicy( int type ) ;

    
    @ManagedAttribute
    @Description( "The IORTemplate used to create Object References")
    IORTemplate getIORTemplate() ;

    
    
    

    
    @ManagedAttribute
    @Description( "The identifier for the AdapterManager that manages this ObjectAdapter")
    int getManagerId() ;

    
    short getState() ;

    @ManagedAttribute
    @Description( "The adapter template" )
    ObjectReferenceTemplate getAdapterTemplate() ;

    @ManagedAttribute
    @Description( "The current object reference factory" )
    ObjectReferenceFactory getCurrentFactory() ;

    
    void setCurrentFactory( ObjectReferenceFactory factory ) ;

    
    
    

    
    org.omg.CORBA.Object getLocalServant( byte[] objectId ) ;

    
    void getInvocationServant( OAInvocationInfo info ) ;

    
    void enter( ) throws OADestroyed ;

    
    void exit( ) ;

    
    public void returnServant() ;

    
    OAInvocationInfo makeInvocationInfo( byte[] objectId ) ;

    
    String[] getInterfaces( Object servant, byte[] objectId ) ;

    public boolean isNameService();

    public void setNameService( boolean flag ) ;
} 
