


package com.sun.corba.ee.impl.protocol;



public class RequestIdImpl implements RequestId {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    final private int value;
    final private boolean defined;
    final static private String UNDEFINED = "?";
    final static public 
            RequestId UNKNOWN_CORBA_REQUEST_ID = new RequestIdImpl();

    
    public RequestIdImpl(int requestId) {
        this.value = requestId;
        this.defined = true;
    }

    
    private RequestIdImpl() {
        this.defined = false;
        
        
        this.value = -1;
    }

    
    public int getValue() {
        if (defined) {
            return this.value;
        } else {
            throw wrapper.undefinedCorbaRequestIdNotAllowed();
        }
    }

    
    public boolean isDefined() {
        return defined;
    }

    
    @Override
    public boolean equals(Object requestId) {

        if (requestId == null || !(requestId instanceof RequestId)) {
            return false;
        }
        
        if (this.isDefined()) {
            if (((RequestId)requestId).isDefined()) {
                return this.value == ((RequestId)requestId).getValue();
            } else { 
                return false;
            }
        } else {
            
            
            return !((RequestId)requestId).isDefined();
        }
    }

    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    
    @Override
    public String toString() {
        if (defined) {
            return Integer.toString(this.value);
        } else {
            return UNDEFINED;
        }
    }
}
