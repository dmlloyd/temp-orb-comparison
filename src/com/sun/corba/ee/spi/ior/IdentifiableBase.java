


package com.sun.corba.ee.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;

import com.sun.corba.ee.spi.ior.Writeable ;
import com.sun.corba.ee.spi.ior.WriteContents ;
import com.sun.corba.ee.spi.orb.ORB ;

import com.sun.corba.ee.impl.ior.EncapsulationUtility ;


public abstract class IdentifiableBase implements Identifiable,
    WriteContents
{
    
    final public void write( OutputStream os )
    {
        EncapsulationUtility.writeEncapsulation( (WriteContents)this, os ) ;
    }
}
