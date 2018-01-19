

package com.sun.corba.se.spi.ior;




public interface ObjectKey extends Writeable
{
    
    ObjectId getId() ;

    
    ObjectKeyTemplate getTemplate()  ;

    byte[] getBytes( org.omg.CORBA.ORB orb ) ;

    CorbaServerRequestDispatcher getServerRequestDispatcher( ORB orb ) ;
}
