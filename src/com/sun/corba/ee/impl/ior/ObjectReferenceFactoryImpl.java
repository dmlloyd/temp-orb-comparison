


package com.sun.corba.ee.impl.ior ;

import org.omg.CORBA.portable.InputStream ;
import org.omg.CORBA.portable.OutputStream ;
import org.omg.CORBA.portable.StreamableValue ;

import org.omg.CORBA.TypeCode ;

import org.omg.PortableInterceptor.ObjectReferenceFactory ;
import org.omg.PortableInterceptor.ObjectReferenceFactoryHelper ;


import com.sun.corba.ee.spi.ior.IORFactory;
import com.sun.corba.ee.spi.ior.IORTemplateList;
import com.sun.corba.ee.spi.ior.IORFactories;

import com.sun.corba.ee.spi.orb.ORB ;


public class ObjectReferenceFactoryImpl extends ObjectReferenceProducerBase
    implements ObjectReferenceFactory, StreamableValue 
{
    

    transient private IORTemplateList iorTemplates ;

    public ObjectReferenceFactoryImpl( InputStream is )
    {
        super( (ORB)(is.orb()) ) ;
        _read( is ) ;
    }

    public ObjectReferenceFactoryImpl( ORB orb, IORTemplateList iortemps ) 
    {
        super( orb ) ;
        iorTemplates = iortemps ;
    }

    @Override
    public boolean equals( Object obj )
    {
        if (!(obj instanceof ObjectReferenceFactoryImpl))
            return false ;

        ObjectReferenceFactoryImpl other = (ObjectReferenceFactoryImpl)obj ;

        return (iorTemplates != null) && 
            iorTemplates.equals( other.iorTemplates ) ;
    }

    @Override
    public int hashCode()
    {
        return iorTemplates.hashCode() ;
    }

    
    
    
    
    
    public static final String repositoryId = 
        "IDL:com/sun/corba/ee/impl/ior/ObjectReferenceFactoryImpl:1.0" ;

    public String[] _truncatable_ids() 
    {
        return new String[] { repositoryId } ;
    }

    public TypeCode _type() 
    {
        return ObjectReferenceFactoryHelper.type() ;
    }

    
    public void _read( InputStream is ) 
    {
        org.omg.CORBA_2_3.portable.InputStream istr = 
            (org.omg.CORBA_2_3.portable.InputStream)is ;

        iorTemplates = IORFactories.makeIORTemplateList( istr ) ;
    }

    
    public void _write( OutputStream os ) 
    {
        org.omg.CORBA_2_3.portable.OutputStream ostr = 
            (org.omg.CORBA_2_3.portable.OutputStream)os ;

        iorTemplates.write( ostr ) ;
    }

    public IORFactory getIORFactory()
    {
        return iorTemplates ;
    }

    public IORTemplateList getIORTemplateList() 
    {
        return iorTemplates ;
    }
}
