


package xxxx;







public class ObjectKeyImpl implements ObjectKey 
{
    private static final IORSystemException wrapper =
        IORSystemException.self ;

    private ObjectKeyTemplate oktemp;
    private ObjectId id;
    private byte[] array;
    
    public ObjectKeyImpl( ObjectKeyTemplate oktemp, ObjectId id) {
        this.oktemp = oktemp ;
        this.id = id ;
    }

    @Override
    public boolean equals( Object obj )
    {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ObjectKeyImpl)) {
            return false;
        }

        ObjectKeyImpl other = (ObjectKeyImpl)obj ;

        return oktemp.equals( other.oktemp ) &&
            id.equals( other.id ) ;
    }

    @Override
    public int hashCode()
    {
        return oktemp.hashCode() ^ id.hashCode() ;
    }

    public ObjectKeyTemplate getTemplate() 
    {
        return oktemp ;
    }

    public ObjectId getId()
    {
        return id ;
    }

    public void write( OutputStream os ) 
    {
        oktemp.write( id, os ) ;
    }

    public synchronized byte[] getBytes(org.omg.CORBA.ORB orb) 
    {
        if (array == null) {        
            EncapsOutputStream os = OutputStreamFactory.newEncapsOutputStream((ORB)orb);
            try {
                write(os);
                array = os.toByteArray();
            } finally {
                try {
                    os.close();
                } catch (java.io.IOException e) {
                    wrapper.ioexceptionDuringStreamClose(e);    
                }
            }
        }

        return array.clone() ;
    }

    public ServerRequestDispatcher getServerRequestDispatcher()
    {
        return oktemp.getServerRequestDispatcher( id ) ;
    }
}
