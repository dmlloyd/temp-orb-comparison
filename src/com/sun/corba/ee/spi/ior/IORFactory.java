


package com.sun.corba.ee.spi.ior ;

import com.sun.corba.ee.spi.orb.ORB ;


public interface IORFactory extends Writeable, MakeImmutable {
    
    IOR makeIOR( ORB orb, String typeid, ObjectId oid ) ;

    
    boolean isEquivalent( IORFactory other ) ;
}
