


package com.sun.corba.ee.impl.transport;

import com.sun.corba.ee.spi.protocol.ClientRequestDispatcher;
import com.sun.corba.ee.spi.transport.Connection;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import com.sun.corba.ee.spi.transport.ContactInfo;
import com.sun.corba.ee.spi.transport.ContactInfoList;
import com.sun.corba.ee.spi.transport.SocketInfo;

import com.sun.corba.ee.impl.encoding.BufferManagerFactory;
import com.sun.corba.ee.impl.encoding.CDROutputObject;
import com.sun.corba.ee.impl.encoding.OutputStreamFactory;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.impl.protocol.MessageMediatorImpl;
import com.sun.corba.ee.impl.protocol.SharedCDRClientRequestDispatcherImpl;

public class SharedCDRContactInfoImpl
    extends 
        ContactInfoBase
{
    
    
    
    private static int requestId = 0;

    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public SharedCDRContactInfoImpl(
        ORB orb,
        ContactInfoList contactInfoList,
        IOR effectiveTargetIOR,
        short addressingDisposition)
    {
        this.orb = orb;
        this.contactInfoList = contactInfoList;
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;
    }

    public String getType()
    {
        throw wrapper.undefinedSocketinfoOperation() ;
    }

    public String getHost()
    {
        throw wrapper.undefinedSocketinfoOperation() ;
    }

    public int getPort()
    {
        throw wrapper.undefinedSocketinfoOperation() ;
    }

    public ClientRequestDispatcher getClientRequestDispatcher()
    {
        
        return new SharedCDRClientRequestDispatcherImpl();
    }

    public boolean isConnectionBased()
    {
        return false;
    }

    public boolean shouldCacheConnection()
    {
        return false;
    }

    public String getConnectionCacheType()
    {
        throw wrapper.methodShouldNotBeCalled();
    }
    
    public Connection createConnection()
    {
        throw wrapper.methodShouldNotBeCalled();
    }

    
    @Override
    public MessageMediator createMessageMediator(ORB broker,
                                                 ContactInfo contactInfo,
                                                 Connection connection,
                                                 String methodName,
                                                 boolean isOneWay)
    {
        if (connection != null) {
            throw wrapper.connectionNotNullInCreateMessageMediator( connection ) ;
        }

        MessageMediator messageMediator =
            new MessageMediatorImpl(
                (ORB) broker,
                (ContactInfo)contactInfo,
                null, 
                GIOPVersion.chooseRequestVersion( (ORB)broker,
                     effectiveTargetIOR),
                effectiveTargetIOR,
                requestId++, 
                getAddressingDisposition(),
                methodName,
                isOneWay);

        return messageMediator;
    }

    public CDROutputObject createOutputObject(MessageMediator messageMediator)
    {
        MessageMediator corbaMessageMediator = (MessageMediator)
            messageMediator;
        
        CDROutputObject outputObject =
            OutputStreamFactory.newCDROutputObject(orb, messageMediator, 
                                corbaMessageMediator.getRequestHeader(),
                                corbaMessageMediator.getStreamFormatVersion(),
                                BufferManagerFactory.GROW);
        messageMediator.setOutputObject(outputObject);
        return outputObject;
    }

    
    
    
    

    public String getMonitoringName()
    {
        throw wrapper.methodShouldNotBeCalled();
    }

    
    
    
    

    
    

    
    private int hashCode = 
        SocketInfo.IIOP_CLEAR_TEXT.hashCode() + "localhost".hashCode() ^ -1;

    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        return obj instanceof SharedCDRContactInfoImpl;
    }

    public String toString()
    {
        return
            "SharedCDRContactInfoImpl[" 
            + "]";
    }

    
    
    
    
}


