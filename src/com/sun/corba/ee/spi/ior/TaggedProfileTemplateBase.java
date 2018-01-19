


package com.sun.corba.ee.spi.ior;





public abstract class TaggedProfileTemplateBase 
    extends IdentifiableContainerBase<TaggedComponent> 
    implements TaggedProfileTemplate
{   
    public void write( OutputStream os )
    {
        EncapsulationUtility.writeEncapsulation( this, os ) ;
    }

    public org.omg.IOP.TaggedComponent[] getIOPComponents( ORB orb, int id )
    {
        int count = 0 ;
        Iterator<TaggedComponent> iter = iteratorById( id ) ;
        while (iter.hasNext()) {
            iter.next() ;
            count++ ;
        }

        org.omg.IOP.TaggedComponent[] result = new
            org.omg.IOP.TaggedComponent[count] ;

        int index = 0 ;
        iter = iteratorById( id ) ;
        while (iter.hasNext()) {
            TaggedComponent comp = iter.next() ;
            result[index++] = comp.getIOPComponent( orb ) ;
        }

        return result ;
    }

    public <T extends TaggedComponent> Iterator<T> iteratorById( int id,
        Class<T> cls ) {

        return (Iterator<T>)iteratorById( id ) ;
    }
}
