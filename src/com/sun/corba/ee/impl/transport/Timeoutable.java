package com.sun.corba.ee.impl.transport;




public interface Timeoutable {

    
    void checkForTimeout(long timeSinceLastActivity);
}
