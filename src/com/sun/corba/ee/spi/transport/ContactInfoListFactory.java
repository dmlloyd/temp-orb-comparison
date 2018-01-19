

package com.sun.corba.ee.spi.transport ;


import com.sun.corba.ee.spi.ior.IOR ;
import com.sun.corba.ee.spi.orb.ORB;


public interface ContactInfoListFactory {
    
    public void setORB(ORB orb);

    public ContactInfoList create( IOR ior ) ;
}
