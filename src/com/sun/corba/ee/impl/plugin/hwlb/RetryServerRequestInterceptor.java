






package com.sun.corba.ee.impl.plugin.hwlb ;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.TRANSIENT;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ServerRequestInfo;


public class RetryServerRequestInterceptor
    extends  org.omg.CORBA.LocalObject
    implements ORBInitializer, ServerRequestInterceptor
{
    private static final String baseMsg = 
        RetryServerRequestInterceptor.class.getName();

    private static boolean rejectingRequests = false;

    private static boolean debug = true;

    
    
    
    

    public static boolean getRejectingRequests() 
    {
        return rejectingRequests;
    }

    public static void setRejectingRequests(boolean x)
    {
        rejectingRequests = x;
    }

    
    
    
    

    public String name() 
    {
        return baseMsg; 
    }

    public void destroy() 
    {
    }

    
    
    
    

    public void receive_request_service_contexts(ServerRequestInfo ri)
    {
        if (rejectingRequests) {
            if (debug) {
                System.out.println(baseMsg 
                                   + ".receive_request_service_contexts:" 
                                   + " rejecting request: "
                                   + ri.operation());
            }
            throw new TRANSIENT();
        }
        if (debug) {
            System.out.println(baseMsg
                               + ".receive_request_service_contexts:"
                               + " accepting request: "
                               + ri.operation());
        }
    }

    public void receive_request(ServerRequestInfo ri)
    {
    }

    public void send_reply(ServerRequestInfo ri)
    {
    }

    public void send_exception(ServerRequestInfo ri)
    {
    }

    public void send_other(ServerRequestInfo ri)
    {
    }

    
    
    
    

    public void pre_init(ORBInitInfo info) 
    {
    }

    public void post_init(ORBInitInfo info) 
    {
        try {
            if (debug) {
                System.out.println(".post_init: registering: " + this);
            }
            info.add_server_request_interceptor(this);
        } catch (DuplicateName e) {
            
            if (debug) {
                System.out.println(".post_init: exception: " + e);
            }
        }
    }
}


