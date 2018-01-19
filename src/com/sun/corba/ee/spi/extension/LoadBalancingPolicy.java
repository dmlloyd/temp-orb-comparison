


package xxxx;




public class LoadBalancingPolicy extends LocalObject implements Policy
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private final int value;

    public LoadBalancingPolicy( int value ) 
    {
        if (value < ORBConstants.FIRST_LOAD_BALANCING_VALUE ||
            value > ORBConstants.LAST_LOAD_BALANCING_VALUE) {
            throw wrapper.invalidLoadBalancingPolicyValue(
                  value, ORBConstants.FIRST_LOAD_BALANCING_VALUE,
                  ORBConstants.LAST_LOAD_BALANCING_VALUE);
        }
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public int policy_type()
    {
        return ORBConstants.LOAD_BALANCING_POLICY ;
    }

    public org.omg.CORBA.Policy copy()
    {
        return this;
    }

    public void destroy()
    {
        
    }

    @Override
    public String toString() 
    {
        return "LoadBalancingPolicy[" + value + "]" ;
    }
}
