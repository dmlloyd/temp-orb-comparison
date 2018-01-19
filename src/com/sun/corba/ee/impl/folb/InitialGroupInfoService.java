


package xxxx;

















@Folb
public class InitialGroupInfoService {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public interface InitialGIS extends Remote {
        public List<ClusterInstanceInfo> getClusterInstanceInfo()
            throws RemoteException ;
    }

    @Folb
    public static class InitialGISImpl extends PortableRemoteObject
        implements InitialGIS {
       
        private ORB orb;

        public InitialGISImpl(ORB orb) throws RemoteException {   
            super() ;      
            this.orb = orb;     
        }
        
        @InfoMethod
        private void exceptionReport( Exception exc ) { }

        @Folb
        public List<ClusterInstanceInfo> getClusterInstanceInfo()
            throws RemoteException {

            try {
                GroupInfoService gis =
                    (GroupInfoService)PortableRemoteObject.narrow(
                    orb.resolve_initial_references(
                        ORBConstants.FOLB_SERVER_GROUP_INFO_SERVICE),
                        GroupInfoService.class);
                return gis.getClusterInstanceInfo(null);
            } catch (org.omg.CORBA.ORBPackage.InvalidName inv) {
                exceptionReport( inv ) ;
                return null;
            }
        }
    }

    public static class InitialGISServantLocator extends LocalObject
        implements ServantLocator {
        private Servant servant ;
        private InitialGISImpl impl = null; 

        public InitialGISServantLocator(ORB orb) {
            try {
                impl = new InitialGISImpl(orb) ;
            } catch (Exception exc) {
                wrapper.couldNotInitializeInitialGIS( exc ) ;
            }

            Tie tie = com.sun.corba.ee.spi.orb.ORB.class.cast( orb )
                .getPresentationManager().getTie() ;
            tie.setTarget( impl ) ;
            servant = Servant.class.cast( tie ) ;
        }

        public String getType() {
            return servant._all_interfaces(null, null)[0];
        }

        public synchronized Servant preinvoke( byte[] oid, POA adapter,
            String operation, CookieHolder the_cookie 
        ) throws ForwardRequest {
            return servant ;
        }

        public void postinvoke( byte[] oid, POA adapter,
            String operation, Object the_cookie, Servant the_servant ) {
        }
    }

    public InitialGroupInfoService(ORB orb) {             
        bindName(orb);
    }


    public void bindName (ORB orb) {
      try {
        POA rootPOA = (POA)orb.resolve_initial_references(
            ORBConstants.ROOT_POA_NAME ) ;

        Policy[] arr = new Policy[] {                                   
            rootPOA.create_servant_retention_policy(
                ServantRetentionPolicyValue.NON_RETAIN ),
            rootPOA.create_request_processing_policy(
                RequestProcessingPolicyValue.USE_SERVANT_MANAGER ),
            rootPOA.create_lifespan_policy(
                LifespanPolicyValue.TRANSIENT ) } ;

        POA poa = rootPOA.create_POA( ORBConstants.INITIAL_GROUP_INFO_SERVICE,
            null, arr ) ;

        InitialGISServantLocator servantLocator =
            new InitialGISServantLocator(orb);
        poa.set_servant_manager(servantLocator) ; 
        poa.the_POAManager().activate();

        byte[] id = new byte[]{ 1, 2, 3 } ;
        org.omg.CORBA.Object provider = 
          poa.create_reference_with_id(id, servantLocator.getType());
            
        
        org.omg.CORBA.Object objRef =
          orb.resolve_initial_references("NameService");
        NamingContext ncRef = NamingContextHelper.narrow(objRef);
        NameComponent nc = 
          new NameComponent(ORBConstants.INITIAL_GROUP_INFO_SERVICE, "");
        NameComponent path[] = {nc};
        ncRef.rebind(path, provider);   
      } catch (Exception e) {
          throw wrapper.bindNameException( e ) ;
      }
    }
}
