

package com.sun.jndi.toolkit.corba;










public class CorbaUtils {
    



    public static org.omg.CORBA.Object remoteToCorba(Remote remoteObj, ORB orb)
        throws ConfigurationException {



            

            Remote stub;

            try {
                stub = PortableRemoteObject.toStub(remoteObj);
            } catch (Throwable t) {
                ConfigurationException ce = new ConfigurationException(
    "Problem with PortableRemoteObject.toStub(); object not exported or stub not found");
                ce.setRootCause(t);
                throw ce;
            }



            if (!(stub instanceof Stub)) {
                return null;  
            }


            try {
                ((Stub) stub).connect(orb);
            } catch (RemoteException e) {
                
                
            } catch (Throwable t) {
                ConfigurationException ce = new ConfigurationException(
                        "Problem invoking javax.rmi.CORBA.Stub.connect()");
                ce.setRootCause(t);
                throw ce;
            }

            return (org.omg.CORBA.Object)stub;
    }

    
    public static ORB getOrb(String server, int port, Hashtable<?,?> env) {
        
        Properties orbProp;

        
        if (env != null) {
            if (env instanceof Properties) {
                
                orbProp = (Properties) env.clone();
            } else {
                
                Enumeration<?> envProp;
                orbProp = new Properties();
                for (envProp = env.keys(); envProp.hasMoreElements();) {
                    String key = (String)envProp.nextElement();
                    Object val = env.get(key);
                    if (val instanceof String) {
                        orbProp.put(key, val);
                    }
                }
            }
        } else {
            orbProp = new Properties();
        }

        if (server != null) {
            orbProp.put("org.omg.CORBA.ORBInitialHost", server);
        }
        if (port >= 0) {
            orbProp.put("org.omg.CORBA.ORBInitialPort", ""+port);
        }

        
        if (env != null) {
            @SuppressWarnings("deprecation")
            Applet applet = (Applet) env.get(Context.APPLET);
            if (applet != null) {
            
                return ORB.init(applet, orbProp);
            }
        }

        return ORB.init(new String[0], orbProp);
    }

    
    public static boolean isObjectFactoryTrusted(Object obj)
        throws NamingException {

        
        Reference ref = null;
        if (obj instanceof Reference) {
            ref = (Reference) obj;
        } else if (obj instanceof Referenceable) {
            ref = ((Referenceable)(obj)).getReference();
        }

        if (ref != null && ref.getFactoryClassLocation() != null &&
                !CNCtx.trustURLCodebase) {
            throw new ConfigurationException(
                "The object factory is untrusted. Set the system property" +
                " 'com.sun.jndi.cosnaming.object.trustURLCodebase' to 'true'.");
        }
        return true;
    }

    
    public static final String decode(String s) throws MalformedURLException {
        try {
            return decode(s, "8859_1");
        } catch (UnsupportedEncodingException e) {
            
            throw new MalformedURLException("ISO-Latin-1 decoder unavailable");
        }
    }

    
    public static final String decode(String s, String enc)
            throws MalformedURLException, UnsupportedEncodingException {
        try {
            return URLDecoder.decode(s, enc);
        } catch (IllegalArgumentException iae) {
            MalformedURLException mue = new MalformedURLException("Invalid URI encoding: " + s);
            mue.initCause(iae);
            throw mue;
        }
    }

}
