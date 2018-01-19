

package com.sun.jndi.cosnaming;







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
