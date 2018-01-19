

package com.sun.corba.se.pept.protocol;



public interface ClientRequestDispatcher
{
    
    public OutputObject beginRequest(Object self,
                                     String methodName,
                                     boolean isOneWay,
                                     ContactInfo contactInfo);

    
    public InputObject marshalingComplete(java.lang.Object self,
                                          OutputObject outputObject)
    
        throws
            org.omg.CORBA.portable.ApplicationException,
            org.omg.CORBA.portable.RemarshalException;

    
    public void endRequest(Broker broker,
                           java.lang.Object self,
                           InputObject inputObject);
}


