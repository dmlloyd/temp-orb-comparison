


package com.sun.corba.ee.impl.legacy.connection;






public class SocketFactoryContactInfoImpl 
    extends
        ContactInfoImpl
{
    protected static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;
    protected SocketInfo socketInfo;

    
    
    
    
    public SocketFactoryContactInfoImpl()
    {
    }

    public SocketFactoryContactInfoImpl(
        ORB orb,
        ContactInfoList contactInfoList,
        IOR effectiveTargetIOR,
        short addressingDisposition,
        SocketInfo cookie)
    {
        super(orb, contactInfoList);
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;

        socketInfo = 
            orb.getORBData().getLegacySocketFactory()
                .getEndPointInfo(orb, effectiveTargetIOR, cookie);

        socketType = socketInfo.getType();
        hostname = socketInfo.getHost();
        port = socketInfo.getPort();
    }

    @Override
    public Connection createConnection()
    {
        Connection connection =
            new SocketFactoryConnectionImpl(
                orb, this,
                orb.getORBData().connectionSocketUseSelectThreadToWait(),
                orb.getORBData().connectionSocketUseWorkerThreadForEvent());
        return connection;
    }

    
    
    
    

    @Override
    public String toString()
    {
        return
            "SocketFactoryContactInfoImpl[" 
            + socketType + " "
            + hostname + " "
            + port
            + "]";
    }
}


