

package com.sun.corba.ee.spi.transport ;




public interface ContactInfoListFactory {
    
    public void setORB(ORB orb);

    public ContactInfoList create( IOR ior ) ;
}
