

package com.sun.corba.se.spi.ior;

import java.util.List ;
import java.util.Iterator ;

import com.sun.corba.se.spi.orb.ORBVersion ;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion ;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile ;

import com.sun.corba.se.spi.orb.ORB ;


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
