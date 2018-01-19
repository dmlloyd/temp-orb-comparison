

package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.se.spi.ior.Writeable ;
import com.sun.corba.se.spi.ior.WriteContents ;
import com.sun.corba.se.spi.orb.ORB ;

import com.sun.corba.se.impl.ior.EncapsulationUtility ;


public abstract class IdentifiableBase implements Identifiable,
    WriteContents
{
    
    final public void write( OutputStream os )
    {
        EncapsulationUtility.writeEncapsulation( (WriteContents)this, os ) ;
    }
}
