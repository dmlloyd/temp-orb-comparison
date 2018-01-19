


package com.sun.corba.ee.impl.txpoa;


public class TSIdentificationImpl extends org.omg.CORBA.LocalObject
        implements TSIdentification {

    private org.omg.CosTSPortability.Sender sender=null;
    private org.omg.CosTSPortability.Receiver receiver=null;

    
    public void
        identify_sender(org.omg.CosTSPortability.Sender senderOTS)
        throws org.omg.CORBA.TSIdentificationPackage.NotAvailable,
               org.omg.CORBA.TSIdentificationPackage.AlreadyIdentified
    {
        if ( sender == null )
            sender = senderOTS;
        else
            throw new org.omg.CORBA.TSIdentificationPackage.AlreadyIdentified();
    }


    
    public void
        identify_receiver(org.omg.CosTSPortability.Receiver receiverOTS)
        throws org.omg.CORBA.TSIdentificationPackage.NotAvailable,
               org.omg.CORBA.TSIdentificationPackage.AlreadyIdentified
    {
        if ( receiver == null )
            receiver = receiverOTS;
        else
            throw new org.omg.CORBA.TSIdentificationPackage.AlreadyIdentified();
    }


    
    public org.omg.CosTSPortability.Sender
        getSender()
    {
        return sender;
    }

    
    public org.omg.CosTSPortability.Receiver
        getReceiver()
    {
        return receiver;
    }
}
