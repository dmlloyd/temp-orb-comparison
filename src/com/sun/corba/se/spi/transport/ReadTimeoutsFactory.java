

package com.sun.corba.se.spi.transport ;


public interface ReadTimeoutsFactory {
   
   public ReadTimeouts create(int initial_wait_time,
                              int max_wait_time,
                              int max_giop_hdr_wait_time,
                              int backoff_percent_factor);
}
