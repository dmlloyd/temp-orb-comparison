


package xxxx;


public class UnknownServiceContextImpl extends ServiceContextBase 
    implements UnknownServiceContext
{
    private int id = -1 ;
    private byte[] data = null ;

    public UnknownServiceContextImpl( int id, byte[] data ) 
    {
        this.id = id ;
        this.data = data.clone() ;
    }

    public UnknownServiceContextImpl( int id, InputStream is ) 
    {
        this.id = id ;

        int len = is.read_long();
        data = new byte[len];
        is.read_octet_array(data,0,len);
    }

    public int getId() { return id ; }

    public void writeData( OutputStream os ) 
    {
        
        
    }

    public void write( OutputStream os, GIOPVersion gv) 
    {
        os.write_long( id ) ;
        os.write_long( data.length ) ;
        os.write_octet_array( data, 0, data.length ) ;
    }

    public byte[] getData() 
    {
        return data.clone() ;
    }
}

