



package xxxx;








public class SharedCDRClientRequestDispatcherImpl
    extends
        CorbaClientRequestDispatcherImpl
{
    
    
    
    
    
    
    

    public InputObject marshalingComplete(java.lang.Object self,
                                          OutputObject outputObject)
        throws
            ApplicationException,
            org.omg.CORBA.portable.RemarshalException
    {
      ORB orb = null;
      CorbaMessageMediator messageMediator = null;
      try {
        messageMediator = (CorbaMessageMediator)
            outputObject.getMessageMediator();

        orb = (ORB) messageMediator.getBroker();

        if (orb.subcontractDebugFlag) {
            dprint(".marshalingComplete->: " + opAndId(messageMediator));
        }

        CDROutputObject cdrOutputObject = (CDROutputObject) outputObject;

        
        
        

        ByteBufferWithInfo bbwi = cdrOutputObject.getByteBufferWithInfo();
        cdrOutputObject.getMessageHeader().setSize(bbwi.byteBuffer, bbwi.getSize());
        final ORB inOrb = orb;
        final ByteBuffer inBuffer = bbwi.byteBuffer;
        final Message inMsg = cdrOutputObject.getMessageHeader();
        CDRInputObject cdrInputObject = AccessController
                .doPrivileged(new PrivilegedAction<CDRInputObject>() {
                    @Override
                    public CDRInputObject run() {
                        return new CDRInputObject(inOrb, null, inBuffer,
                                inMsg);
                    }
                });
        messageMediator.setInputObject(cdrInputObject);
        cdrInputObject.setMessageMediator(messageMediator);

        
        
        

        
        ((CorbaMessageMediatorImpl)messageMediator).handleRequestRequest(
            messageMediator);

        
        
        
        try { cdrInputObject.close(); }
        catch (IOException ex) {
            
            
            

            if (orb.transportDebugFlag) {
               dprint(".marshalingComplete: ignoring IOException - " + ex.toString());
            }
        }

        
        
        

        cdrOutputObject = (CDROutputObject) messageMediator.getOutputObject();
        bbwi = cdrOutputObject.getByteBufferWithInfo();
        cdrOutputObject.getMessageHeader().setSize(bbwi.byteBuffer, bbwi.getSize());
        final ORB inOrb2 = orb;
        final ByteBuffer inBuffer2 = bbwi.byteBuffer;
        final Message inMsg2 = cdrOutputObject.getMessageHeader();
        cdrInputObject = AccessController
                .doPrivileged(new PrivilegedAction<CDRInputObject>() {
                    @Override
                    public CDRInputObject run() {
                        return new CDRInputObject(inOrb2, null, inBuffer2,
                                inMsg2);
                    }
                });
        messageMediator.setInputObject(cdrInputObject);
        cdrInputObject.setMessageMediator(messageMediator);

        cdrInputObject.unmarshalHeader();

        InputObject inputObject = cdrInputObject;

        return processResponse(orb, messageMediator, inputObject);

      } finally {
        if (orb.subcontractDebugFlag) {
            dprint(".marshalingComplete<-: " + opAndId(messageMediator));
        }
      }
    }

    protected void dprint(String msg)
    {
        ORBUtility.dprint("SharedCDRClientRequestDispatcherImpl", msg);
    }
}


