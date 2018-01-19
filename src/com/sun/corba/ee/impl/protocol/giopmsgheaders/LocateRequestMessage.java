


package com.sun.corba.ee.impl.protocol.giopmsgheaders;

import com.sun.corba.ee.spi.orb.ObjectKeyCacheEntry;



public interface LocateRequestMessage extends Message {
    int getRequestId();
    ObjectKeyCacheEntry getObjectKeyCacheEntry();
}
