


package xxxx;




public class RequestPartitioningPolicy extends LocalObject implements Policy
{
    private static ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public final static int DEFAULT_VALUE = 0;
    private final int value;

    public RequestPartitioningPolicy( int value ) 
    {
        if (value < ORBConstants.REQUEST_PARTITIONING_MIN_THREAD_POOL_ID ||
            value > ORBConstants.REQUEST_PARTITIONING_MAX_THREAD_POOL_ID) {
            throw wrapper.invalidRequestPartitioningPolicyValue(
                      value,
                      ORBConstants.REQUEST_PARTITIONING_MIN_THREAD_POOL_ID,
                      ORBConstants.REQUEST_PARTITIONING_MAX_THREAD_POOL_ID);
        }
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public int policy_type()
    {
        return ORBConstants.REQUEST_PARTITIONING_POLICY;
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
        return "RequestPartitioningPolicy[" + value + "]" ;
    }
}
