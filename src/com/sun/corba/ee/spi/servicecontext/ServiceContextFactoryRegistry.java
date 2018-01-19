


package xxxx;


public interface ServiceContextFactoryRegistry {

    public void register( ServiceContext.Factory factory ) ;

    public ServiceContext.Factory find( int scId ) ;
}
