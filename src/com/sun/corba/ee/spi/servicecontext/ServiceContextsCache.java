

package com.sun.corba.ee.spi.servicecontext;

import java.util.EnumMap;
import com.sun.corba.ee.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.ee.spi.orb.ORB;

public class ServiceContextsCache {

    public static enum CASE {
                    CLIENT_INITIAL, 
                    CLIENT_SUBSEQUENT, 
                    SERVER_INITIAL, 
                    SERVER_SUBSEQUENT};
  
    private EnumMap<CASE, ServiceContexts> data;
    private ORB orb;

    public ServiceContextsCache (com.sun.corba.ee.spi.orb.ORB orb) {

        data = new EnumMap<CASE, ServiceContexts>(CASE.class);
        this.orb = orb;

    }

    public synchronized ServiceContexts get(CASE c) {

        if (data.size() == 0) {

            
            ServiceContexts scContainer = ServiceContextDefaults.makeServiceContexts(orb);
            scContainer.put(ServiceContextDefaults.getMaxStreamFormatVersionServiceContext());
            scContainer.put(ServiceContextDefaults.getORBVersionServiceContext());      
            scContainer.put(ServiceContextDefaults.makeSendingContextServiceContext(orb.getFVDCodeBaseIOR()));

            data.put(CASE.CLIENT_INITIAL, scContainer);
            
            
            scContainer = ServiceContextDefaults.makeServiceContexts(orb);
            scContainer.put(ServiceContextDefaults.getMaxStreamFormatVersionServiceContext());
            scContainer.put(ServiceContextDefaults.getORBVersionServiceContext());      
            
            data.put(CASE.CLIENT_SUBSEQUENT, scContainer);
            
            
            scContainer = ServiceContextDefaults.makeServiceContexts(orb);
            scContainer.put(ServiceContextDefaults.getORBVersionServiceContext());      
            scContainer.put(ServiceContextDefaults.makeSendingContextServiceContext(orb.getFVDCodeBaseIOR()));
            
            data.put(CASE.SERVER_INITIAL, scContainer);
            
            
            scContainer = ServiceContextDefaults.makeServiceContexts(orb);
            scContainer.put(ServiceContextDefaults.getORBVersionServiceContext());      
            
            data.put(CASE.SERVER_SUBSEQUENT, scContainer); 
            
        }

        return (data.get(c)).copy();
    }
}
