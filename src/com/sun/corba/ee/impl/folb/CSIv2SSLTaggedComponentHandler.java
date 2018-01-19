






package com.sun.corba.ee.impl.folb;

import java.util.List;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.IORInfo;
import com.sun.corba.ee.spi.folb.ClusterInstanceInfo;
import com.sun.corba.ee.spi.folb.ClusterInstanceInfo;
import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.transport.SocketInfo;


public interface CSIv2SSLTaggedComponentHandler
{
    
    public TaggedComponent insert(IORInfo iorInfo, 
                                  List<ClusterInstanceInfo> clusterInstanceInfo);

    
    public List<SocketInfo> extract(IOR ior); 
}




