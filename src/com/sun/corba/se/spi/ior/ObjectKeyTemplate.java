

package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.orb.ORBVersion ;
import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher ;


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
