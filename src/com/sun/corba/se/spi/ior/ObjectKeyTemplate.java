

package com.sun.corba.se.spi.ior;





public interface ObjectKeyTemplate extends Writeable
{
    public ORBVersion getORBVersion() ;

    
    public int getSubcontractId();

    
    public int getServerId() ;

    
    public String getORBId() ;

    
    public ObjectAdapterId getObjectAdapterId() ;

    
    public byte[] getAdapterId() ;

    public void write(ObjectId objectId, OutputStream os);

    public CorbaServerRequestDispatcher getServerRequestDispatcher( ORB orb, ObjectId id ) ;
}
