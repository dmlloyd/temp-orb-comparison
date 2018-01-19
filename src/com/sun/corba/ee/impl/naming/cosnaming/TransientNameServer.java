


package xxxx;





@Naming
public class TransientNameServer
{
    static private boolean debug = false ;
    private static final NamingSystemException wrapper =
        NamingSystemException.self ;

    @Naming
    private static org.omg.CORBA.Object initializeRootNamingContext( ORB orb ) {
        org.omg.CORBA.Object rootContext = null;
        try {
            com.sun.corba.ee.spi.orb.ORB coreORB =
                (com.sun.corba.ee.spi.orb.ORB)orb ; 
                
            TransientNameService tns = new TransientNameService(coreORB );
            return tns.initialNamingContext();
        } catch (org.omg.CORBA.SystemException e) {
            throw wrapper.transNsCannotCreateInitialNcSys( e ) ;
        } catch (Exception e) {
            throw wrapper.transNsCannotCreateInitialNc( e ) ;
        }
    }

    
    @Naming
    public static void main(String args[]) {
        boolean invalidHostOption = false;
        boolean orbInitialPort0 = false;

        
        int initialPort = 0;
        try {
            
            Properties props = System.getProperties() ;

            props.put( ORBConstants.ORB_SERVER_ID_PROPERTY,
                ORBConstants.NAME_SERVICE_SERVER_ID ) ;
            props.put( "org.omg.CORBA.ORBClass", 
                "com.sun.corba.ee.impl.orb.ORBImpl" );

            String ips = null ;
            try {
                ips = System.getProperty( ORBConstants.INITIAL_PORT_PROPERTY ) ;
                if (ips != null && ips.length() > 0 ) {
                    initialPort = java.lang.Integer.parseInt(ips);
                    if( initialPort == 0 ) {
                        orbInitialPort0 = true;
                        throw wrapper.transientNameServerBadPort() ;
                    }
                }

                String hostName = 
                    System.getProperty( ORBConstants.INITIAL_HOST_PROPERTY ) ;

                if( hostName != null ) {
                    invalidHostOption = true;
                    throw wrapper.transientNameServerBadHost() ;
                }
            } catch (java.lang.NumberFormatException e) {
                wrapper.badInitialPortValue( ips, e ) ;
            }

            
            for (int i=0;i<args.length;i++) {
                if (args[i].equals("-ORBInitialPort") &&
                    i < args.length-1) {
                    initialPort = java.lang.Integer.parseInt(args[i+1]);

                    if( initialPort == 0 ) {
                        orbInitialPort0 = true;
                        throw wrapper.transientNameServerBadPort() ;
                    }
                }

                if (args[i].equals("-ORBInitialHost" ) ) { 
                    invalidHostOption = true;
                    throw wrapper.transientNameServerBadHost() ;
                }
            }

            
            
            if( initialPort == 0 ) {
                initialPort = ORBConstants.DEFAULT_INITIAL_PORT;
                props.put( ORBConstants.INITIAL_PORT_PROPERTY,
                    java.lang.Integer.toString(initialPort) );
            }

            
            
            props.put( ORBConstants.PERSISTENT_SERVER_PORT_PROPERTY, 
               java.lang.Integer.toString(initialPort) );

            org.omg.CORBA.ORB corb = ORB.init( args, props ) ;
  
            org.omg.CORBA.Object ns = initializeRootNamingContext( corb ) ;
            ((com.sun.corba.ee.org.omg.CORBA.ORB)corb).register_initial_reference( 
                "NamingService", ns ) ;

            String stringifiedIOR = null;
 
            if( ns != null ) {
                stringifiedIOR = corb.object_to_string(ns) ;
            } else {
                 NamingUtils.errprint(CorbaResourceUtil.getText(
                     "tnameserv.exception", initialPort));
                 NamingUtils.errprint(CorbaResourceUtil.getText(
                     "tnameserv.usage"));
                System.exit( 1 );
            }

            
            
            

            System.out.println(CorbaResourceUtil.getText(
                "tnameserv.hs1", stringifiedIOR));
            System.out.println(CorbaResourceUtil.getText(
                "tnameserv.hs2", initialPort));
            System.out.println(CorbaResourceUtil.getText("tnameserv.hs3"));

            
            java.lang.Object sync = new java.lang.Object();
            synchronized (sync) {sync.wait();}
        } catch (Exception e) {
            if( invalidHostOption ) {
                
                
                NamingUtils.errprint( CorbaResourceUtil.getText(
                    "tnameserv.invalidhostoption" ) );
            } else if( orbInitialPort0 ) {
                
                
                NamingUtils.errprint( CorbaResourceUtil.getText(
                    "tnameserv.orbinitialport0" ));
            } else {
                NamingUtils.errprint(CorbaResourceUtil.getText(
                    "tnameserv.exception", initialPort));
                NamingUtils.errprint(CorbaResourceUtil.getText(
                    "tnameserv.usage"));
            }
        }
    }

     
    private TransientNameServer() {}
}
