


package com.sun.corba.ee.spi.transport;






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


