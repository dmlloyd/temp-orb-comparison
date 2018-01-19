


package com.sun.corba.ee.impl.ior;





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
