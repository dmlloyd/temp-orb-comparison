


package xxxx;



public class ReferenceFactoryImpl extends LocalObject implements ReferenceFactory
{
    private ReferenceFactoryManagerImpl manager ;
    private String name ;
    private String repositoryId ;

    public ReferenceFactoryImpl( ReferenceFactoryManagerImpl manager, 
        String name, String repositoryId ) {
        this.manager = manager ;
        this.name = name ;
        this.repositoryId = repositoryId ;
    }

    public org.omg.CORBA.Object createReference( byte[] key ) {
        return manager.createReference( name, key, repositoryId ) ;
    }

    public void destroy() {
        manager.destroy( name ) ;
    }

    public String toString()
    {
        return "ReferenceFactoryImpl["
            + name
            + ", "
            + repositoryId
            + "]";
    }
}
