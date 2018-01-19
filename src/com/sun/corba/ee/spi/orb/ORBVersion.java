


package com.sun.corba.ee.spi.orb ;



@ManagedData
@Description( "The version of the ORB" )
public interface ORBVersion extends Comparable<ORBVersion>
{
    byte FOREIGN = 0 ;          
    byte OLD = 1 ;              
    byte NEW = 2 ;              
    byte JDK1_3_1_01 = 3;       
    byte NEWER = 10 ;           
    byte PEORB = 20 ;           

    @ManagedAttribute
    @Description( "ORB version (0=FOREIGN,1=OLD,2=NEW,3=JDK1_3_1_01,10=NEWER,20=PEORB)" )
    byte getORBType() ;

    void write( OutputStream os ) ;

    public boolean lessThan( ORBVersion version ) ;
}
