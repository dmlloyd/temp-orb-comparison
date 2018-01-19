


package com.sun.corba.ee.impl.ior;






public class IORTemplateImpl 
    extends IdentifiableContainerBase<TaggedProfileTemplate>
    implements IORTemplate
{
    private ObjectKeyTemplate oktemp ;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder() ;
        sb.append( "IORTemplate[oktemp=") ;
        sb.append( oktemp.toString() ) ;
        sb.append( " profile templates:") ;
        sb.append( super.toString() ) ;
        sb.append( ']' ) ;
        return sb.toString() ;
    }

    @Override
    public boolean equals( Object obj )
    {
        if (obj == null)
            return false ;

        if (!(obj instanceof IORTemplateImpl))
            return false ;

        IORTemplateImpl other = (IORTemplateImpl)obj ;

        return super.equals( obj ) && oktemp.equals( other.getObjectKeyTemplate() ) ;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() ^ oktemp.hashCode() ;
    }

    public ObjectKeyTemplate getObjectKeyTemplate()
    {
        return oktemp ;
    }

    public IORTemplateImpl( ObjectKeyTemplate oktemp )
    {
        this.oktemp = oktemp ;
    }

    public IOR makeIOR( ORB orb, String typeid, ObjectId oid ) 
    {
        return new IORImpl( orb, typeid, this, oid ) ;
    }

    public boolean isEquivalent( IORFactory other ) 
    {
        if (!(other instanceof IORTemplate))
            return false ;

        IORTemplate list = (IORTemplate)other ;

        Iterator<TaggedProfileTemplate> thisIterator = iterator() ;
        Iterator<TaggedProfileTemplate> listIterator = list.iterator() ;
        while (thisIterator.hasNext() && listIterator.hasNext()) {
            TaggedProfileTemplate thisTemplate = thisIterator.next() ;
            TaggedProfileTemplate listTemplate = listIterator.next() ;
            if (!thisTemplate.isEquivalent( listTemplate ))
                return false ;
        }

        return (thisIterator.hasNext() == listIterator.hasNext()) &&
            getObjectKeyTemplate().equals( list.getObjectKeyTemplate() ) ;
    }

    
    @Override
    public void makeImmutable()
    {
        makeElementsImmutable() ;
        super.makeImmutable() ;
    }

    public void write( OutputStream os ) 
    {
        oktemp.write( os ) ;
        EncapsulationUtility.writeIdentifiableSequence( this, os ) ;
    }

    public IORTemplateImpl( InputStream is ) 
    {
        ORB orb = (ORB)(is.orb()) ;
        IdentifiableFactoryFinder<TaggedProfileTemplate> finder = 
            orb.getTaggedProfileTemplateFactoryFinder() ;

        oktemp = orb.getObjectKeyFactory().createTemplate( is ) ;
        EncapsulationUtility.readIdentifiableSequence( this, finder, is ) ;

        makeImmutable() ;
    }
}
