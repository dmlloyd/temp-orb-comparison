


package xxxx;


public class ServiceContextFactoryRegistryImpl 
    implements ServiceContextFactoryRegistry
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    final private ORB orb ;
    private Map<Integer, ServiceContext.Factory> scMap ;

    public ServiceContextFactoryRegistryImpl( ORB orb )
    {
        scMap = new HashMap<Integer, ServiceContext.Factory>() ;
        this.orb = orb ;
    }           

    public void register( ServiceContext.Factory factory ) 
    {
        if (scMap.get(factory.getId()) == null) {
            scMap.put(factory.getId(), factory);
        } else {
            wrapper.registerDuplicateServiceContext();
        } 
    }

    public ServiceContext.Factory find( int scId )
    {
        ServiceContext.Factory result = scMap.get( scId ) ;
        return result ;
    }
}
