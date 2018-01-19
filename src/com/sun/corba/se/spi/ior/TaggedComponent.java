

package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA.ORB ;


public interface TaggedComponent extends Identifiable
{
    org.omg.IOP.TaggedComponent getIOPComponent( ORB orb ) ;
}
