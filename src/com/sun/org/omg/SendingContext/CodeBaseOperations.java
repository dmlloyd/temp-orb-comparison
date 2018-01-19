
package xxxx;






public interface CodeBaseOperations  extends org.omg.SendingContext.RunTimeOperations
{

    
    com.sun.org.omg.CORBA.Repository get_ir ();

    
    String implementation (String x);
    String[] implementations (String[] x);

    
    com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription meta (String x);
    com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription[] metas (String[] x);

    
    String[] bases (String x);
} 
