


package com.sun.corba.ee.impl.ior;



public final class ObjectIdImpl implements ObjectId
{
    private byte[] id;
    
    @Override
    public boolean equals( Object obj )
    {
        if (!(obj instanceof ObjectIdImpl))
            return false ;

        ObjectIdImpl other = (ObjectIdImpl)obj ;

        return Arrays.equals( this.id, other.id ) ;
    }

    @Override
    public int hashCode() 
    {
        int result = 17 ;
        for (int ctr=0; ctr<id.length; ctr++)
            result = 37*result + id[ctr] ;
        return result ;
    }

    public ObjectIdImpl( byte[] id ) 
    {
        if (id == null) {
            this.id = null ;
        } else {
            this.id = (byte[])id.clone() ;
        }
    }

    public String getIdString() {
        return ORBUtility.dumpBinary( id ) ;
    }

    public String toString() {
        return "ObjectIdImpl[" + getIdString() + "]" ;
    }

    public byte[] getId()
    {
        return (byte[])id.clone() ;
    }

    public void write( OutputStream os )
    {
        os.write_long( id.length ) ;
        os.write_octet_array( id, 0, id.length ) ;
    }
}
