


package com.sun.corba.ee.impl.ior;





public class IORTemplateListImpl extends FreezableList<IORTemplate> 
    implements IORTemplateList
{
    public IORTemplateListImpl()
    {
        super( new ArrayList<IORTemplate>() ) ;
    }

    public IORTemplateListImpl( InputStream is ) 
    {
        this() ;
        int size = is.read_long() ;
        for (int ctr=0; ctr<size; ctr++) {
            IORTemplate iortemp = IORFactories.makeIORTemplate( is ) ;
            add( iortemp ) ;
        }

        makeImmutable() ;
    }

    @Override
    public void makeImmutable()
    {
        makeElementsImmutable() ;
        super.makeImmutable() ;
    }

    public void write( OutputStream os ) 
    {
        os.write_long( size() ) ;
        for (IORTemplate iortemp : this) {
            iortemp.write( os ) ;
        }
    }

    public IOR makeIOR( ORB orb, String typeid, ObjectId oid ) 
    {
        return new IORImpl( orb, typeid, this, oid ) ;
    }

    public boolean isEquivalent( IORFactory other ) 
    {
        if (!(other instanceof IORTemplateList))
            return false ;

        IORTemplateList list = (IORTemplateList)other ;

        Iterator<IORTemplate> thisIterator = iterator() ;
        Iterator<IORTemplate> listIterator = list.iterator() ;
        while (thisIterator.hasNext() && listIterator.hasNext()) {
            IORTemplate thisTemplate = thisIterator.next() ;
            IORTemplate listTemplate = listIterator.next() ;
            if (!thisTemplate.isEquivalent( listTemplate ))
                return false ;
        }

        return thisIterator.hasNext() == listIterator.hasNext() ;
    }
}
