

package com.sun.corba.ee.spi.protocol ;

import com.sun.corba.ee.spi.transport.ContactInfoList ;

import com.sun.corba.ee.spi.protocol.ClientDelegate ;


public interface ClientDelegateFactory {
    ClientDelegate create( ContactInfoList list ) ;
}
