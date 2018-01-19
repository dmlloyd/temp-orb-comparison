


package com.sun.corba.ee.impl.servicecontext;





public class ORBVersionServiceContextImpl extends ServiceContextBase
    implements ORBVersionServiceContext 
{
    
    private ORBVersion version = ORBVersionFactory.getORBVersion() ;

    public static final ORBVersionServiceContext singleton =
        new ORBVersionServiceContextImpl() ;

    public ORBVersionServiceContextImpl( )
    {
        version = ORBVersionFactory.getORBVersion() ;
    }

    public ORBVersionServiceContextImpl( ORBVersion ver )
    {
        this.version = ver ;
    }

    public ORBVersionServiceContextImpl(InputStream is, GIOPVersion gv)
    {
        super(is) ;
        
        
        
        

        version = ORBVersionFactory.create( in ) ;
    }

    public int getId() 
    { 
        return SERVICE_CONTEXT_ID ; 
    }

    public void writeData( OutputStream os ) throws SystemException
    {
        version.write( os ) ;
    }

    public ORBVersion getVersion() 
    {
        return version ;
    }

    public String toString() 
    {
        return "ORBVersionServiceContextImpl[ version=" + version + " ]" ;
    }
}
