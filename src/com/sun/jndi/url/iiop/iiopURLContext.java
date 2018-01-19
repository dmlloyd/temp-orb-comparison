

package com.sun.jndi.url.iiop;

import javax.naming.spi.ResolveResult;
import javax.naming.*;
import java.util.Hashtable;
import java.net.MalformedURLException;

import com.sun.jndi.cosnaming.IiopUrl;
import com.sun.jndi.cosnaming.CorbanameUrl;



public class iiopURLContext
        extends GenericURLContext {

    iiopURLContext(Hashtable<?,?> env) {
        super(env);
    }

    


    protected ResolveResult getRootURLContext(String name, Hashtable<?,?> env)
    throws NamingException {
        return iiopURLContextFactory.getUsingURLIgnoreRest(name, env);
    }

    
    protected Name getURLSuffix(String prefix, String url)
        throws NamingException {
        try {
            if (url.startsWith("iiop://") || url.startsWith("iiopname://")) {
                IiopUrl parsedUrl = new IiopUrl(url);
                return parsedUrl.getCosName();
            } else if (url.startsWith("corbaname:")) {
                CorbanameUrl parsedUrl = new CorbanameUrl(url);
                return parsedUrl.getCosName();
            } else {
                throw new MalformedURLException("Not a valid URL: " + url);
            }
        } catch (MalformedURLException e) {
            throw new InvalidNameException(e.getMessage());
        }
    }
}
