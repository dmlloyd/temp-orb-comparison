


package com.sun.corba.ee.impl.transport;

import java.nio.ByteBuffer ;

import java.util.List ;
import java.util.ArrayList ;

import com.sun.corba.ee.spi.transport.MessageTraceManager ;

public class MessageTraceManagerImpl implements MessageTraceManager
{
    
    
    
    private List  dataSent ;
    private List  dataReceived ;
    private boolean enabled ;
    private boolean RHRCalled ; 
    private byte[] header ;

    public MessageTraceManagerImpl()
    {
        init() ;
        enabled = false ;
    }

    public void clear()
    {
        init() ;
    }

    private void init() 
    {
        dataSent = new ArrayList() ;
        dataReceived = new ArrayList() ;
        initHeaderRecorder() ;
    }

    public boolean isEnabled() 
    {
        return enabled ;
    }

    public void enable( boolean flag ) 
    {
        enabled = flag ;
    }

    public byte[][] getDataSent() 
    {
        return (byte[][])dataSent.toArray(
            new byte[dataSent.size()][] ) ;
    }

    public byte[][] getDataReceived() 
    {
        return (byte[][])dataReceived.toArray(
            new byte[dataReceived.size()][] ) ;
    }

    
   
    private void initHeaderRecorder()
    {
        RHRCalled = false ;
        header = null ;
    }

    
    public byte[] getBytes( ByteBuffer bb, int offset ) 
    {
        ByteBuffer view = bb.asReadOnlyBuffer() ;
        view.flip() ;
        int len = view.remaining() ;
        byte[] buffer = new byte[ len + offset ] ;
        view.get( buffer, offset, len ) ;

        return buffer ; 
    }

    @Override
    public void recordDataSent(ByteBuffer message)
    {
        byte[] buffer = getBytes( message, 0 ) ;
        dataSent.add( buffer ) ;
    }
    
    public void recordHeaderReceived( ByteBuffer message ) 
    {
        if (RHRCalled) {
            
            dataReceived.add( header ) ;
            initHeaderRecorder() ;
        }

        RHRCalled = true ;
        header = getBytes( message, 0 ) ;
    }

    public void recordBodyReceived( ByteBuffer message ) 
    {
        if (!RHRCalled)
            
            
            
            header = "NO HEADER!!!".getBytes() ;

        byte[] buffer = getBytes( message, header.length ) ;
        System.arraycopy( header, 0, buffer, header.length,
            message.remaining() ) ;
        dataReceived.add( buffer ) ;    

        initHeaderRecorder() ;
    }
}
