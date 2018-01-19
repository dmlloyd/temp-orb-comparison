


package xxxx;










public class ReferenceManagerConfigurator implements ORBConfigurator {
    private static final POASystemException wrapper =
        POASystemException.self ;

    private static class RMIORInterceptor
        extends LocalObject 
        implements IORInterceptor_3_0 
    {
        private ReferenceFactoryManagerImpl rm ;

        public RMIORInterceptor( ReferenceFactoryManagerImpl rm ) {
            this.rm = rm ;
        }

        public String name() {
            return "##" + this.getClass().getName() + "##" ;
        }

        public void destroy() {
            
        }

        public void establish_components( IORInfo info ) {
            
        }
        
        public void adapter_manager_state_changed( int id, short state ) {
            
        }

        public void adapter_state_changed( ObjectReferenceTemplate[] templates, short state ) {
            
        }

        
        
        
        
        public void components_established( IORInfo info ) {
            IORInfoExt ext = IORInfoExt.class.cast( info ) ;
            ObjectAdapter oa = ext.getObjectAdapter() ;
            if (!(oa instanceof POA)) {
                return;
            } 
            POA poa = POA.class.cast( oa ) ;
            rm.validatePOACreation( poa ) ;
        }
    }

    private static class RMORBInitializer
        extends LocalObject 
        implements ORBInitializer 
    {
        private IORInterceptor_3_0 interceptor ;

        public RMORBInitializer( IORInterceptor_3_0 interceptor ) {
            this.interceptor = interceptor ;
        }

        public void pre_init( ORBInitInfo info ) {
            
        }

        public void post_init( ORBInitInfo info ) {
            try {
                info.add_ior_interceptor( interceptor ) ;
            } catch (Exception exc) {
                throw wrapper.rfmPostInitException( exc ) ;
            }
        }
    }

    public void configure( DataCollector collector, ORB orb ) 
    {
        try {
            ReferenceFactoryManagerImpl rm = new ReferenceFactoryManagerImpl( orb ) ;
            orb.register_initial_reference( ORBConstants.REFERENCE_FACTORY_MANAGER, rm ) ;
            IORInterceptor_3_0 interceptor = new RMIORInterceptor( rm ) ;
            ORBInitializer initializer = new RMORBInitializer( interceptor ) ;  
            orb.getORBData().addORBInitializer( initializer ) ;
        } catch (Exception exc) {
            throw wrapper.rfmConfigureException( exc ) ;
        }
    }
}
