

package com.sun.corba.ee.impl.ior.iiop;




public class RequestPartitioningComponentImpl extends TaggedComponentBase 
    implements RequestPartitioningComponent
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private int partitionToUse;

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof RequestPartitioningComponentImpl)) {
            return false;
        }

        RequestPartitioningComponentImpl other = 
            (RequestPartitioningComponentImpl)obj ;

        return partitionToUse == other.partitionToUse ;
    }

    @Override
    public int hashCode()
    {
        return partitionToUse;
    }

    @Override
    public String toString()
    {
        return "RequestPartitioningComponentImpl[partitionToUse=" + partitionToUse + "]" ;
    }

    public RequestPartitioningComponentImpl()
    {
        partitionToUse = 0;
    }

    public RequestPartitioningComponentImpl(int thePartitionToUse) {
        if (thePartitionToUse < ORBConstants.REQUEST_PARTITIONING_MIN_THREAD_POOL_ID ||
            thePartitionToUse > ORBConstants.REQUEST_PARTITIONING_MAX_THREAD_POOL_ID) {
            throw wrapper.invalidRequestPartitioningComponentValue(
                    thePartitionToUse,
                    ORBConstants.REQUEST_PARTITIONING_MIN_THREAD_POOL_ID,
                    ORBConstants.REQUEST_PARTITIONING_MAX_THREAD_POOL_ID);
        }
        partitionToUse = thePartitionToUse;
    }

    public int getRequestPartitioningId()
    {
        return partitionToUse;
    }

    public void writeContents(OutputStream os) 
    {
        os.write_ulong(partitionToUse);
    }
    
    public int getId() 
    {
        return ORBConstants.TAG_REQUEST_PARTITIONING_ID;
    }
}
