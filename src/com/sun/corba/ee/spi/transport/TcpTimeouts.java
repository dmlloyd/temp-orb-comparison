


package com.sun.corba.ee.spi.transport;



public interface TcpTimeouts {
    
    int get_initial_time_to_wait();

    
    int get_max_time_to_wait();

    
    int get_max_single_wait_time() ;

    
    int get_backoff_factor();

    
    public interface Waiter {
        
        void advance() ;

        
        void reset() ;

        
        int getTimeForSleep() ;

        
        int getTime() ;

        
        int timeWaiting() ;

        
        boolean sleepTime() ;

        
        boolean isExpired() ;
    }

    
    Waiter waiter() ;

    
    public interface Factory {
        
        TcpTimeouts create( int initial_time_to_wait,
            int max_time_to_wait, int backoff_value ) ;

        
        TcpTimeouts create( int initial_time_to_wait,
            int max_time_to_wait, int backoff_value, int max_single_wait ) ;

        
        TcpTimeouts create( String args ) ;
    }

    Factory factory = new Factory() {
        public TcpTimeouts create( int initial_time_to_wait,
            int max_time_to_wait, int backoff_value ) {

            return new TcpTimeoutsImpl( initial_time_to_wait,
                max_time_to_wait, backoff_value ) ;
        }

        public TcpTimeouts create( int initial_time_to_wait,
            int max_time_to_wait, int backoff_value, int max_single_wait ) {

            return new TcpTimeoutsImpl( initial_time_to_wait,
                max_time_to_wait, backoff_value, max_single_wait ) ;
        }

        public TcpTimeouts create( String args ) {
            return new TcpTimeoutsImpl( args ) ;
        }
    } ;
}


