


package com.sun.corba.ee.spi.transport;

import java.util.Iterator ;

import com.sun.corba.ee.spi.ior.IOR ;

import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcher ;



public abstract interface ContactInfoList
{
    public Iterator<ContactInfo> iterator() ; 
    public void setTargetIOR(IOR ior);
    public IOR getTargetIOR();

    public void setEffectiveTargetIOR(IOR locatedIor);
    public IOR getEffectiveTargetIOR();

    public LocalClientRequestDispatcher getLocalClientRequestDispatcher();

    public int hashCode();
}


