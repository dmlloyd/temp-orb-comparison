


package xxxx;



public interface ObjectKey extends Writeable
{
    
    ObjectId getId() ;

    
    ObjectKeyTemplate getTemplate()  ;

    byte[] getBytes( org.omg.CORBA.ORB orb ) ;
    
    ServerRequestDispatcher getServerRequestDispatcher() ;
}
