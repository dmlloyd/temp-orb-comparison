


package xxxx;



public interface ClientRequestDispatcher
{
    
    public CDROutputObject beginRequest(Object self,
                                     String methodName,
                                     boolean isOneWay,
                                     ContactInfo contactInfo);

    
    public CDRInputObject marshalingComplete(java.lang.Object self,
                                          CDROutputObject outputObject)
    
        throws
            org.omg.CORBA.portable.ApplicationException, 
            org.omg.CORBA.portable.RemarshalException;

    
    public void endRequest(ORB broker,
                           java.lang.Object self, 
                           CDRInputObject inputObject);
}


