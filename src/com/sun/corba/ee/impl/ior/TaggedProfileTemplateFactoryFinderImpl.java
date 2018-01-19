


package com.sun.corba.ee.impl.ior;

import com.sun.corba.ee.spi.ior.TaggedProfileTemplate ;

import com.sun.corba.ee.spi.orb.ORB ;

import org.omg.CORBA_2_3.portable.InputStream ;


public class TaggedProfileTemplateFactoryFinderImpl extends
    IdentifiableFactoryFinderBase<TaggedProfileTemplate> 
{
    public TaggedProfileTemplateFactoryFinderImpl( ORB orb )
    { 
        super( orb ) ;
    }

    public TaggedProfileTemplate handleMissingFactory( int id, InputStream is) 
    {
        throw wrapper.taggedProfileTemplateFactoryNotFound( id ) ;
    }
}
