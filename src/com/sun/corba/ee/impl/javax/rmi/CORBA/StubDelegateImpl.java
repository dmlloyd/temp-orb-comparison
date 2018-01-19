



package xxxx;







public class StubDelegateImpl implements javax.rmi.CORBA.StubDelegate 
{
    private static final UtilSystemException wrapper =
        UtilSystemException.self ;

    private StubIORImpl ior ;

    public synchronized StubIORImpl getIOR() 
    {
        return ior ;
    }
    
    public synchronized void setIOR( StubIORImpl ior ) 
    {
        this.ior = ior ;
    }

    public StubDelegateImpl() 
    {
        ior = null ;
    }

    
    private synchronized void init (javax.rmi.CORBA.Stub self) 
    {
        
        
        if (ior == null) {
            ior = new StubIORImpl(self);
        }
    }
        
    
    public synchronized int hashCode(javax.rmi.CORBA.Stub self) 
    {
        init(self);
        return ior.hashCode() ;
    }

    
    public synchronized boolean equals(javax.rmi.CORBA.Stub self, java.lang.Object obj) 
    {
        if (self == obj) {
            return true;    
        }
        
        if (!(obj instanceof javax.rmi.CORBA.Stub)) {
            return false;            
        }
        
        

        javax.rmi.CORBA.Stub other = (javax.rmi.CORBA.Stub) obj;
        if (other.hashCode() != self.hashCode()) {
            return false;
        }

        
        
        
        
        
        return self.toString().equals( other.toString() ) ;
    }

    @Override
    public synchronized boolean equals( Object obj )
    {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof StubDelegateImpl)) {
            return false;
        }

        StubDelegateImpl other = (StubDelegateImpl)obj ;

        if (ior == null) {
            return ior == other.ior;
        } else {
            return ior.equals(other.ior);
        }
    }

    @Override
    public synchronized int hashCode() {
        if (ior == null) {
            return 0;
        } else {
            return ior.hashCode();
        }
    }

    
    public synchronized String toString(javax.rmi.CORBA.Stub self) 
    {
        if (ior == null) {
            return null;
        } else {
            return ior.toString();
        }
    }
    
    
    public synchronized void connect(javax.rmi.CORBA.Stub self, ORB orb) 
        throws RemoteException 
    {
        ior = StubConnectImpl.connect( ior, self, self, orb ) ;
    }

    
    public synchronized void readObject(javax.rmi.CORBA.Stub self, 
        java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException 
    {
        if (ior == null) {
            ior = new StubIORImpl();
        }

        ior.doRead( stream ) ;
    }

    
    public synchronized void writeObject(javax.rmi.CORBA.Stub self, 
        java.io.ObjectOutputStream stream) throws IOException 
    {
        init(self);
        ior.doWrite( stream ) ;
    }
}
