

package com.sun.corba.ee.spi.transport;

import com.sun.corba.ee.impl.protocol.giopmsgheaders.Message ;
import com.sun.corba.ee.impl.encoding.CDRInputObject;

public interface MessageData {
    
    Message[] getMessages() ;

     
    CDRInputObject getStream() ;
}
