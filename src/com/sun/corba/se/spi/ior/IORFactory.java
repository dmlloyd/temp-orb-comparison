

package com.sun.corba.se.spi.ior ;

import com.sun.corba.se.spi.orb.ORB ;


public interface IORFactory extends Writeable, MakeImmutable {
    
    IOR makeIOR( ORB orb, String typeid, ObjectId oid ) ;

    
    boolean isEquivalent( IORFactory other ) ;
}
