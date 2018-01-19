


package xxxx;






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
