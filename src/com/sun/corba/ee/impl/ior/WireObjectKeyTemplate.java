


package com.sun.corba.ee.impl.ior;









public class WireObjectKeyTemplate implements ObjectKeyTemplate 
{
    private ORB orb ;
    private static final IORSystemException wrapper =
        IORSystemException.self ;
    private static ObjectAdapterId NULL_OBJECT_ADAPTER_ID = 
        new ObjectAdapterIdArray( new String[0] ) ;

    @Override
    public boolean equals( Object obj )
    {
        if (obj == null) {
            return false ;
        }

        return obj instanceof WireObjectKeyTemplate ;
    }

    @Override
    public int hashCode()
    {
        return 53 ; 
                    
    }

    public WireObjectKeyTemplate( ORB orb )
    {
        initORB( orb ) ;
    }

    private void initORB( ORB orb ) 
    {
        this.orb = orb ;
    }

    public void write( ObjectId id, OutputStream os ) 
    {
        byte[] key = id.getId() ;
        os.write_octet_array( key, 0, key.length ) ;
    }

    public void write( OutputStream os ) 
    {
        
    }

    public int getSubcontractId()
    {
        return ORBConstants.DEFAULT_SCID ;
    }

    
    
    
    
    public int getServerId() 
    {
        return -1 ;
    }

    public String getORBId()
    {
        throw wrapper.orbIdNotAvailable() ;
    }

    public ObjectAdapterId getObjectAdapterId() 
    {
        return NULL_OBJECT_ADAPTER_ID ;

        
    }

    
    
    public byte[] getAdapterId()
    {
        throw wrapper.adapterIdNotAvailable() ;
    }

    public ORBVersion getORBVersion() 
    {
        return ORBVersionFactory.getFOREIGN() ;
    }

    public ServerRequestDispatcher getServerRequestDispatcher( ObjectId id )
    {
        byte[] bid = id.getId() ;
        String str = new String( bid ) ;
        return orb.getRequestDispatcherRegistry().getServerRequestDispatcher(
            str ) ;
    }
}
