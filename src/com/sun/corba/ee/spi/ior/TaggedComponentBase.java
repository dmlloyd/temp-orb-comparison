


package com.sun.corba.ee.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.ee.impl.encoding.EncapsOutputStream ;
import com.sun.corba.ee.impl.encoding.OutputStreamFactory;
import com.sun.corba.ee.spi.orb.ORB ;



public abstract class TaggedComponentBase extends IdentifiableBase 
    implements TaggedComponent 
{
    public org.omg.IOP.TaggedComponent getIOPComponent( 
        org.omg.CORBA.ORB orb )
    {
        EncapsOutputStream os = OutputStreamFactory.newEncapsOutputStream( (ORB)orb ) ;
        os.write_ulong( getId() ) ; 
        write( os ) ;
        InputStream is = (InputStream)(os.create_input_stream() ) ;
        return org.omg.IOP.TaggedComponentHelper.read( is ) ;
    }
}
