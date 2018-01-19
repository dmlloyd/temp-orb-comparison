

package com.sun.corba.se.spi.transport ;

import com.sun.corba.se.spi.transport.CorbaContactInfoList ;

import com.sun.corba.se.spi.ior.IOR ;
import com.sun.corba.se.spi.orb.ORB;


public interface CorbaContactInfoListFactory {
    
    public void setORB(ORB orb);

    public CorbaContactInfoList create( IOR ior ) ;
}
