

package com.sun.corba.ee.impl.ior.iiop;





public class ClusterInstanceInfoComponentImpl extends TaggedComponentBase 
    implements ClusterInstanceInfoComponent {

    private final ClusterInstanceInfo clusterInstanceInfoValue;

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ClusterInstanceInfoComponentImpl)) {
            return false;
        }

        ClusterInstanceInfoComponentImpl other = 
            (ClusterInstanceInfoComponentImpl)obj ;

        return clusterInstanceInfoValue.equals( 
            other.clusterInstanceInfoValue ) ;
    }

    @Override
    public int hashCode() {
        return clusterInstanceInfoValue.hashCode() ;
    }

    @Override
    public String toString() {
        return "ClusterInstanceInfoComponentImpl[clusterInstanceInfoValue=" 
            + clusterInstanceInfoValue + "]" ;
    }

    public ClusterInstanceInfoComponentImpl(
        ClusterInstanceInfo theClusterInstanceInfoValue) {
        clusterInstanceInfoValue = theClusterInstanceInfoValue ;
    }

    public ClusterInstanceInfo getClusterInstanceInfo()
    {
        return clusterInstanceInfoValue;
    }

    public void writeContents(OutputStream os) {
        clusterInstanceInfoValue.write(os);
    }
    
    public int getId() {
        return ORBConstants.FOLB_MEMBER_ADDRESSES_TAGGED_COMPONENT_ID ;
    }
}

