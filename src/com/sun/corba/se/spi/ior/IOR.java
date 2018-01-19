

package com.sun.corba.se.spi.ior;






public interface IOR extends List, Writeable, MakeImmutable
{
    ORB getORB() ;

    
    String getTypeId() ;

    
    Iterator iteratorById( int id ) ;

    
    String stringify() ;

    
    org.omg.IOP.IOR getIOPIOR() ;

    
    boolean isNil() ;

    
    boolean isEquivalent(IOR ior) ;

    
    IORTemplateList getIORTemplates() ;

    
    IIOPProfile getProfile() ;
}
