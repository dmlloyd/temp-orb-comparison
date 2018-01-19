


package xxxx;




public class SocketFactoryContactInfoListImpl 
    extends
        ContactInfoListImpl
{
    
    public SocketFactoryContactInfoListImpl(ORB orb)
    {
        super(orb);
    }

    public SocketFactoryContactInfoListImpl(ORB orb, IOR targetIOR)
    {
        super(orb, targetIOR);
    }

    public Iterator iterator()
    {
        return new SocketFactoryContactInfoListIteratorImpl(orb, this);
    }
}


