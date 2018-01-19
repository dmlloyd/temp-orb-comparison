


package com.sun.corba.ee.spi.legacy.connection;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.transport.SocketInfo;



public interface ORBSocketFactory
{
    
    public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";


    
    public ServerSocket createServerSocket(String type, int port)
        throws 
            IOException;



    
    public SocketInfo getEndPointInfo(org.omg.CORBA.ORB orb,
                                        IOR ior,
                                        SocketInfo socketInfo);


    
    public Socket createSocket(SocketInfo socketInfo)
        throws
            IOException,
            GetEndPointInfoAgainException;
}




