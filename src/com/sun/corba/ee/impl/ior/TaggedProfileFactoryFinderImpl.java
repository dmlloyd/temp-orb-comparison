


package com.sun.corba.ee.impl.ior;

import com.sun.corba.ee.spi.ior.TaggedProfile ;

import com.sun.corba.ee.spi.orb.ORB ;


import org.omg.CORBA_2_3.portable.InputStream ;


public class TaggedProfileFactoryFinderImpl extends
    IdentifiableFactoryFinderBase<TaggedProfile>
{
    public TaggedProfileFactoryFinderImpl( ORB orb ) 
    {
        super( orb ) ;
    }

    public TaggedProfile handleMissingFactory( int id, InputStream is) 
    {
        return new GenericTaggedProfile( id, is ) ;
    }
}
