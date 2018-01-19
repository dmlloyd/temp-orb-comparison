

package com.sun.corba.se.impl.ior;

import java.util.HashMap;
import java.util.Map;

import com.sun.corba.se.spi.ior.Identifiable ;
import com.sun.corba.se.spi.ior.IdentifiableFactory ;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder ;
import com.sun.corba.se.spi.ior.TaggedComponent ;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder ;

import com.sun.corba.se.impl.ior.GenericTaggedComponent ;
import com.sun.corba.se.impl.ior.IdentifiableFactoryFinderBase ;

import com.sun.corba.se.impl.encoding.EncapsOutputStream ;

import com.sun.corba.se.spi.orb.ORB ;

import org.omg.CORBA_2_3.portable.InputStream ;


public class TaggedComponentFactoryFinderImpl extends
    IdentifiableFactoryFinderBase implements TaggedComponentFactoryFinder
{
    public TaggedComponentFactoryFinderImpl( ORB orb )
    {
        super( orb ) ;
    }

    public Identifiable handleMissingFactory( int id, InputStream is ) {
        return new GenericTaggedComponent( id, is ) ;
    }

    public TaggedComponent create( org.omg.CORBA.ORB orb,
        org.omg.IOP.TaggedComponent comp )
    {
        EncapsOutputStream os =
            sun.corba.OutputStreamFactory.newEncapsOutputStream((ORB)orb);
        org.omg.IOP.TaggedComponentHelper.write( os, comp ) ;
        InputStream is = (InputStream)(os.create_input_stream() ) ;
        
        is.read_ulong() ;

        return (TaggedComponent)create( comp.tag, is ) ;
    }
}
