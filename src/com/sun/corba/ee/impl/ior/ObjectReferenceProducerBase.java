


package com.sun.corba.ee.impl.ior ;

import com.sun.corba.ee.spi.orb.ORB ;

import com.sun.corba.ee.spi.ior.IORFactory ;
import com.sun.corba.ee.spi.ior.IOR ;
import com.sun.corba.ee.spi.ior.IORFactories ;
import com.sun.corba.ee.spi.ior.IORTemplateList ;
import com.sun.corba.ee.spi.ior.ObjectId ;

import com.sun.corba.ee.impl.misc.ORBUtility ;
import java.io.Serializable;






public abstract class ObjectReferenceProducerBase implements Serializable {
    private static final long serialVersionUID = 6478965304620421549L;
    transient protected ORB orb ;

    public abstract IORFactory getIORFactory() ;

    public abstract IORTemplateList getIORTemplateList() ;

    public ObjectReferenceProducerBase( ORB orb ) 
    {
        this.orb = orb ;
    }

    public org.omg.CORBA.Object make_object (String repositoryId, 
        byte[] objectId)
    {
        ObjectId oid = IORFactories.makeObjectId( objectId ) ;
        IOR ior = getIORFactory().makeIOR( orb, repositoryId, oid ) ;

        return ORBUtility.makeObjectReference( ior ) ;
    }
}

