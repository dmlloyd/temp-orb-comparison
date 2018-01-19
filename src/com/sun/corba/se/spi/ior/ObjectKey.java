

package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher ;

import com.sun.corba.se.spi.orb.ORB ;


public interface ObjectKey extends Writeable
{
    
    ObjectId getId() ;

    
    ObjectKeyTemplate getTemplate()  ;

    byte[] getBytes( org.omg.CORBA.ORB orb ) ;

    CorbaServerRequestDispatcher getServerRequestDispatcher( ORB orb ) ;
}
