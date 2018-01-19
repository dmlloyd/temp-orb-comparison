

package com.sun.corba.se.spi.transport;






public interface CorbaAcceptor
    extends
        Acceptor
{
    public String getObjectAdapterId();
    public String getObjectAdapterManagerId();
    public void addToIORTemplate(IORTemplate iorTemplate, Policies policies,
                                 String codebase);
    public String getMonitoringName();
}


