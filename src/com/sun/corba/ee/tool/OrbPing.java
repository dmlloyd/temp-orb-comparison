

package xxxx;



public class OrbPing {
    public static class IntervalTimer {
        long lastTime ;

        public void start() {
            lastTime = System.nanoTime() ;
        }

        
        public long interval() {
            final long current = System.nanoTime() ;
            final long diff = current - lastTime ;
            start() ;
            return diff/1000 ;
        }
    }

    private interface Args {
        @DefaultValue( "1" )
        @Help( "The number of times to repeat the ORB ping")
        int count() ;

        @DefaultValue( "localhost" )
        @Help( "The host running the ORB")
        String host() ;

        @DefaultValue( "3037")
        @Help( "The port on which the ORB listens for clear text requests")
        int port() ;

        @DefaultValue( "false" )
        @Help( "Display extra information, including timing information" )
        boolean verbose() ;
    }

    private static Args args ;
    private static ORB orb ;
    private static IntervalTimer timer = new IntervalTimer() ;

    private static void ping( String host, int port ) {
        final String url = String.format( "corbaname:iiop:1.2@%s:%d/NameService",
            host, port ) ;

        org.omg.CORBA.Object cobject = null ;
        try {
            timer.start() ;
            cobject = orb.string_to_object( url ) ;
        } catch (Exception exc) {
            msg( "Exception in string_to_object call: %s\n", exc ) ;
        } finally {
            if (args.verbose()) {
                msg( "string_to_object call took %d microseconds\n",
                    timer.interval() ) ;
            }
        }

        NamingContext nctx ;

        try {
            timer.start() ;
            nctx = NamingContextHelper.narrow(cobject);
        } catch (Exception exc) {
            msg( "Exception in naming narrow call: %s\n", exc ) ;
        } finally {
            if (args.verbose()) {
                msg( "naming narrow call took %d microseconds\n",
                    timer.interval() ) ;
            }
        }
    }

    private static void msg( String str, Object... args ) {
        System.out.printf( str, args ) ;
    }

    public static void main( String[] params ) {
        args = (new ArgParser( Args.class )).parse( params, Args.class ) ;

        try {
            timer.start() ;
            orb = ORB.init( params, null ) ;
        } catch (Exception exc) {
            msg( "Exception in ORB.init: %s\n", exc ) ;
        } finally {
            if (args.verbose()) {
                msg( "ORB.init call took %d microseconds\n", timer.interval() ) ;
            }
        }

        for (int ctr=0; ctr<args.count(); ctr++ ) {
            ping( args.host(), args.port() ) ;
        }
    }
}
