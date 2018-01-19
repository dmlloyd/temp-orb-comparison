

package com.sun.jndi.cosnaming;

import javax.naming.*;
import javax.naming.spi.StateFactory;
import java.util.Hashtable;

import org.omg.CORBA.ORB;

import java.rmi.Remote;
import java.rmi.server.ExportException;

import com.sun.jndi.toolkit.corba.CorbaUtils;  



public class RemoteToCorba implements StateFactory {
    public RemoteToCorba() {
    }

    
    public Object getStateToBind(Object orig, Name name, Context ctx,
        Hashtable<?,?> env) throws NamingException {
        if (orig instanceof org.omg.CORBA.Object) {
            
            return null;
        }

        if (orig instanceof Remote) {
            
            
            
            
            return CorbaUtils.remoteToCorba((Remote)orig, ((CNCtx)ctx)._orb);
        }
        return null; 
    }
}
