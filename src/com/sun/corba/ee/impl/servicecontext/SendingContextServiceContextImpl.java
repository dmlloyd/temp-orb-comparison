


package xxxx;






public class SendingContextServiceContextImpl extends ServiceContextBase
    implements SendingContextServiceContext
{
    private IOR ior = null ;

    public SendingContextServiceContextImpl( IOR ior )
    {
        this.ior = ior ;
    }

    public SendingContextServiceContextImpl(InputStream is, GIOPVersion gv)
    {
        super(is) ;
        ior = IORFactories.makeIOR( (ORB)is.orb(), is ) ;       
    }

    public int getId() 
    { 
        return SERVICE_CONTEXT_ID ; 
    }

    public void writeData( OutputStream os ) 
    {
        ior.write( os ) ;
    }

    public IOR getIOR() 
    {
        return ior ;
    }

    public String toString() 
    {
        return "SendingContexServiceContextImpl[ ior=" + ior + " ]" ;
    }
}
