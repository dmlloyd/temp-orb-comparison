


package xxxx;





public interface ServiceContexts {
    
    void write( OutputStream os, GIOPVersion gv ) ;
    
    
    public void put( ServiceContext sc ) ;

    
    public void delete( int scId ) ;

    
    public ServiceContext get( int scId) ;

    public ServiceContexts copy() ;
}

