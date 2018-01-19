


package xxxx;



public interface ExceptionHandler 
{
    
    boolean isDeclaredException( Class cls ) ;

    
    void writeException( OutputStream os, Exception ex ) ;

    
    Exception readException( ApplicationException ae ) ;
}
