


package com.sun.corba.ee.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.orb.ORBVersion ;
import com.sun.corba.ee.spi.orb.ORB ;

import com.sun.corba.ee.spi.protocol.ServerRequestDispatcher ;

import org.glassfish.gmbal.ManagedData ;
import org.glassfish.gmbal.ManagedAttribute ;
import org.glassfish.gmbal.Description ;


@ManagedData
@Description( "The template used to represent all IORs created by the same Object adapter" )
public interface ObjectKeyTemplate extends Writeable
{
    @ManagedAttribute
    @Description( "The ORB version that created this template" )
    public ORBVersion getORBVersion() ;

    
    @ManagedAttribute
    @Description( "The subcontract ID which identifies a particular type-independent " 
        + " implementation of an IOR" )
    public int getSubcontractId();

    
    @ManagedAttribute
    @Description( "The ID of the server that handles requests to this IOR" )
    public int getServerId() ;

    
    @ManagedAttribute
    @Description( "the ORB ID that created this IOR" )
    public String getORBId() ;

    
    @ManagedAttribute
    @Description( "The ObjectAdapterId that identifies the ObjectAdapter that created this IOR" )
    public ObjectAdapterId getObjectAdapterId() ;

    
    public byte[] getAdapterId() ;

    public void write(ObjectId objectId, OutputStream os);
    
    public ServerRequestDispatcher getServerRequestDispatcher( ObjectId id ) ;
}
