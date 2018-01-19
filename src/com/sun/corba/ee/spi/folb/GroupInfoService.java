






package com.sun.corba.ee.spi.folb;




public interface GroupInfoService
{
    
    public boolean addObserver(GroupInfoServiceObserver x);

    
    public void notifyObservers();

    
    public List<ClusterInstanceInfo> getClusterInstanceInfo(
        String[] adapterName);

    
    public List<ClusterInstanceInfo> getClusterInstanceInfo(
        String[] adapterName, List<String> endpoints );

    
    public boolean shouldAddAddressesToNonReferenceFactory(
        String[] adapterName);

    
    public boolean shouldAddMembershipLabel (String[] adapterName);
}


