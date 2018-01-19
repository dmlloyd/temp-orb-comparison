

package xxxx;


public interface ORBVersion extends Comparable
{
    byte FOREIGN = 0 ;          
    byte OLD = 1 ;              
    byte NEW = 2 ;              
    byte JDK1_3_1_01 = 3;       
    byte NEWER = 10 ;           
    byte PEORB = 20 ;           

    byte getORBType() ;

    void write( OutputStream os ) ;

    public boolean lessThan( ORBVersion version ) ;
}
