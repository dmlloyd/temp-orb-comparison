



package com.sun.corba.ee.impl.corba;

import com.sun.corba.ee.spi.orb.ORB ;






public class AsynchInvoke implements Runnable {

    private final RequestImpl _req;
    private final ORB         _orb;
    private final boolean     _notifyORB;

    public AsynchInvoke (ORB o, RequestImpl reqToInvokeOn, boolean n)
    {
        _orb = o;
        _req = reqToInvokeOn;
        _notifyORB = n;
    };


    

    public void run() 
    {
        synchronized (_req) {
            
            _req.doInvocation();
        }
    
        
        
        synchronized (_req) {
            
            _req.gotResponse = true;

            
            _req.notify();
        }
      
        if (_notifyORB == true) {
            _orb.notifyORB() ;
        }
    }

};


