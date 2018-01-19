


package com.sun.corba.ee.impl.transport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS ;

import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate ;
import com.sun.corba.ee.spi.ior.iiop.IIOPAddress ;
import com.sun.corba.ee.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.ee.spi.transport.IORToSocketInfo;
import com.sun.corba.ee.spi.transport.SocketInfo;

public class DefaultIORToSocketInfoImpl
    implements IORToSocketInfo
{
    public List<? extends SocketInfo> getSocketInfo(IOR ior, 
        List<? extends SocketInfo> previous) {

        
        if (! previous.isEmpty()) {
            return previous;
        }

        SocketInfo socketInfo;
        List<SocketInfo> result = new ArrayList<SocketInfo>();

        IIOPProfileTemplate iiopProfileTemplate = (IIOPProfileTemplate)
            ior.getProfile().getTaggedProfileTemplate() ;
        IIOPAddress primary = iiopProfileTemplate.getPrimaryAddress() ;
        String hostname = primary.getHost().toLowerCase();
        int    port     = primary.getPort();
        
        
        
        
        
        socketInfo = createSocketInfo(hostname, port);
        result.add(socketInfo);

        Iterator iterator = iiopProfileTemplate.iteratorById(
            TAG_ALTERNATE_IIOP_ADDRESS.value);

        while (iterator.hasNext()) {
            AlternateIIOPAddressComponent alternate =
                (AlternateIIOPAddressComponent) iterator.next();
            hostname = alternate.getAddress().getHost().toLowerCase();
            port     = alternate.getAddress().getPort();
            socketInfo= createSocketInfo(hostname, port);
            result.add(socketInfo);
        }
        return result;
    }

    private SocketInfo createSocketInfo(final String hostname, final int port)
    {
        return new SocketInfo() {
            public String getType() { return SocketInfo.IIOP_CLEAR_TEXT; }
            public String getHost() { return hostname; }
            public int    getPort() { return port; }};
    }
}


