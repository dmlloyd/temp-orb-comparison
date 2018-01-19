

package com.sun.corba.ee.impl.ior.iiop;




public class LoadBalancingComponentImpl extends TaggedComponentBase 
    implements LoadBalancingComponent
{

    private static ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private int loadBalancingValue;

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof LoadBalancingComponentImpl)) {
            return false;
        }

        LoadBalancingComponentImpl other = 
            (LoadBalancingComponentImpl)obj ;

        return loadBalancingValue == other.loadBalancingValue ;
    }

    @Override
    public int hashCode()
    {
        return loadBalancingValue;
    }

    @Override
    public String toString()
    {
        return "LoadBalancingComponentImpl[loadBalancingValue=" + loadBalancingValue + "]" ;
    }

    public LoadBalancingComponentImpl()
    {
        loadBalancingValue = 0;
    }

    public LoadBalancingComponentImpl(int theLoadBalancingValue) {
        if (theLoadBalancingValue < ORBConstants.FIRST_LOAD_BALANCING_VALUE ||
            theLoadBalancingValue > ORBConstants.LAST_LOAD_BALANCING_VALUE) {
            throw wrapper.invalidLoadBalancingComponentValue(
                  theLoadBalancingValue,
                  ORBConstants.FIRST_LOAD_BALANCING_VALUE,
                  ORBConstants.LAST_LOAD_BALANCING_VALUE );
        }
        loadBalancingValue = theLoadBalancingValue;
    }

    public int getLoadBalancingValue()
    {
        return loadBalancingValue;
    }

    public void writeContents(OutputStream os) 
    {
        os.write_ulong(loadBalancingValue);
    }
    
    public int getId() 
    {
        return ORBConstants.TAG_LOAD_BALANCING_ID;
    }
}

