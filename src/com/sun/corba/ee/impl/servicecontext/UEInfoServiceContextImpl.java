


package xxxx;


public class UEInfoServiceContextImpl extends ServiceContextBase
    implements UEInfoServiceContext
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private Throwable unknown = null ;

    public UEInfoServiceContextImpl( Throwable ex )
    {
        unknown = ex ;
    }

    public UEInfoServiceContextImpl(InputStream is, GIOPVersion gv)
    {
        super(is) ;

        try { 
            unknown = (Throwable) in.read_value() ;
        } catch (Exception e) {
            unknown = wrapper.couldNotReadInfo( e ) ;
        }
    }

    public int getId() 
    { 
        return SERVICE_CONTEXT_ID ; 
    }

    public void writeData( OutputStream os ) 
    {
        os.write_value( (Serializable)unknown ) ;
    }

    public Throwable getUE() { return unknown ; } 

    @Override
    public String toString()
    {
        return "UEInfoServiceContextImpl[ unknown=" + unknown.toString() + " ]" ;
    }
}


