

package com.sun.corba.ee.spi.ior.iiop;

import com.sun.corba.ee.spi.ior.TaggedComponent;
import com.sun.corba.ee.spi.folb.ClusterInstanceInfo;

public interface ClusterInstanceInfoComponent extends TaggedComponent
{
    public ClusterInstanceInfo getClusterInstanceInfo();
}

