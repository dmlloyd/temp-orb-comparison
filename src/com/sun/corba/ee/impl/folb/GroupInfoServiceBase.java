






package xxxx;






@Folb
public abstract class GroupInfoServiceBase
    extends org.omg.CORBA.LocalObject
    implements GroupInfoService
{
    private List<GroupInfoServiceObserver> observers =
        new LinkedList<GroupInfoServiceObserver>();

    @Folb
    public boolean addObserver(GroupInfoServiceObserver x) {
        return observers.add(x);
    }

    @InfoMethod
    private void observerInfo( GroupInfoServiceObserver obs ) { }

    @Folb
    public void notifyObservers() {
        for (GroupInfoServiceObserver observer : observers) {
            observerInfo( observer ) ;
            observer.membershipChange();
        }
    }

    @Folb
    public List<ClusterInstanceInfo> getClusterInstanceInfo(
        String[] adapterName) {

        
        return new ArrayList( internalClusterInstanceInfo() ) ;
    }

    public List<ClusterInstanceInfo> getClusterInstanceInfo(
        String[] adapterName, List<String> endpoints ) {

        
        return new ArrayList( internalClusterInstanceInfo( endpoints ) ) ;
    }

    @Folb
    public boolean shouldAddAddressesToNonReferenceFactory(
        String[] adapterName) {
        return false ;
    }

    @Folb
    public boolean shouldAddMembershipLabel (String[] adapterName) {
        return true ;
    }

    public List<ClusterInstanceInfo> internalClusterInstanceInfo() {
        final List<String> endpoints = new ArrayList<String>() ;
        return internalClusterInstanceInfo( endpoints ) ;
    }

    public abstract List<ClusterInstanceInfo> internalClusterInstanceInfo( List<String> endpoints ) ;
}


