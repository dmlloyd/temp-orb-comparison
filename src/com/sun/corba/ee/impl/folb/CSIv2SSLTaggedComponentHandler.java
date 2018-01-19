






package com.sun.corba.ee.impl.folb;



public interface CSIv2SSLTaggedComponentHandler
{
    
    public TaggedComponent insert(IORInfo iorInfo, 
                                  List<ClusterInstanceInfo> clusterInstanceInfo);

    
    public List<SocketInfo> extract(IOR ior); 
}




