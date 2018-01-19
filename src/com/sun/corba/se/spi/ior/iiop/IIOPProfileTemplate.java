

package com.sun.corba.se.spi.ior.iiop;

import java.util.List ;
import java.util.Iterator ;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate ;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion ;
import com.sun.corba.se.spi.orb.ORB ;


public interface IIOPProfileTemplate extends TaggedProfileTemplate
{
    
    public GIOPVersion getGIOPVersion() ;

    
    public IIOPAddress getPrimaryAddress()  ;
}
