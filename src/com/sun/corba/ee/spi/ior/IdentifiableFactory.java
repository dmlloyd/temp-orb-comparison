


package com.sun.corba.ee.spi.ior ;





public interface IdentifiableFactory<E extends Identifiable> {
    
    public int getId() ;

    
    public E create( ORB orb, InputStream in ) ;
}
