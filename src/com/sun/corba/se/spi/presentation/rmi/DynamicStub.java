

package xxxx;





public interface DynamicStub extends org.omg.CORBA.Object
{
    
    void setDelegate( Delegate delegate ) ;

    
    Delegate getDelegate() ;

    
    ORB getORB() ;

    
    String[] getTypeIds() ;

    
    void connect( ORB orb ) throws RemoteException ;

    boolean isLocal() ;

    OutputStream request( String operation, boolean responseExpected ) ;
}
