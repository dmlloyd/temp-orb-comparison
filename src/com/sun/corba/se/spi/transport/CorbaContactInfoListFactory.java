

package com.sun.corba.se.spi.transport ;




public interface CorbaContactInfoListFactory {
    
    public void setORB(ORB orb);

    public CorbaContactInfoList create( IOR ior ) ;
}
