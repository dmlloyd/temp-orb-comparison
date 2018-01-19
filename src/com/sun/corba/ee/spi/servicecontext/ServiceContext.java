


package com.sun.corba.ee.spi.servicecontext;



public interface ServiceContext {
    public interface Factory {
        int getId() ;

        ServiceContext create( InputStream s, GIOPVersion gv ) ;        
    }

    int getId() ;

    void write(OutputStream s, GIOPVersion gv )  ;
}
