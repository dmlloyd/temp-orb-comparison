


package com.sun.corba.ee.impl.ior.iiop;



public final class IIOPAddressClosureImpl extends IIOPAddressBase
{
    private NullaryFunction<String> host;
    private NullaryFunction<Integer> port;
    
    public IIOPAddressClosureImpl( NullaryFunction<String> host,
        NullaryFunction<Integer> port ) {
        this.host = host ;
        this.port = port ;
    }

    public String getHost()
    {
        return host.evaluate() ;
    }

    public int getPort()
    {
        return port.evaluate() ;
    }
}
