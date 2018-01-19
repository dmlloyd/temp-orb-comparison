




package com.sun.corba.ee.impl.protocol;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.omg.CORBA.portable.ApplicationException;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.protocol.MessageMediator;

import com.sun.corba.ee.impl.encoding.CDRInputObject;
import com.sun.corba.ee.impl.encoding.CDROutputObject;
import com.sun.corba.ee.spi.trace.Subcontract;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;


@Subcontract
public class SharedCDRClientRequestDispatcherImpl
    extends
        ClientRequestDispatcherImpl
{

    @InfoMethod
    private void operationAndId( String msg, int rid ) { }

    
    
    
    
    
    
    

    @Override
    @Subcontract
    public CDRInputObject marshalingComplete(java.lang.Object self,
                                          CDROutputObject outputObject)
        throws 
            ApplicationException, 
            org.omg.CORBA.portable.RemarshalException
    {
        MessageMediator messageMediator = null;
        messageMediator = (MessageMediator)
            outputObject.getMessageMediator();
        operationAndId( messageMediator.getOperationName(),
            messageMediator.getRequestId() ) ;
        final ORB orb = (ORB) messageMediator.getBroker();
        operationAndId(messageMediator.getOperationName(), 
            messageMediator.getRequestId());

        CDROutputObject cdrOutputObject = outputObject;
        final CDROutputObject fCDROutputObject = cdrOutputObject;

        
        
        

        CDRInputObject cdrInputObject = AccessController.doPrivileged(
        		new PrivilegedAction<CDRInputObject>() {
					@Override
					public CDRInputObject run() {
						return fCDROutputObject.createInputObject(orb);
					}
        		});
        		
        messageMediator.setInputObject(cdrInputObject);
        cdrInputObject.setMessageMediator(messageMediator);

        
        
        

        
        ((MessageMediatorImpl)messageMediator).handleRequestRequest(
            messageMediator);

        
        
        
        try {
            cdrInputObject.close();
        } catch (IOException ex) {
            
            
            
            
        }

        
        
        

        cdrOutputObject = messageMediator.getOutputObject();
        final CDROutputObject fCDROutputObject2 = cdrOutputObject;
        cdrInputObject = AccessController.doPrivileged(
        		new PrivilegedAction<CDRInputObject>() {

					@Override
					public CDRInputObject run() {
						
						return fCDROutputObject2.createInputObject(orb);
					}
        			
        		});
        messageMediator.setInputObject(cdrInputObject);
        cdrInputObject.setMessageMediator(messageMediator);

        cdrInputObject.unmarshalHeader();

        CDRInputObject inputObject = cdrInputObject;

        return processResponse(orb, messageMediator, inputObject);
    }

}


