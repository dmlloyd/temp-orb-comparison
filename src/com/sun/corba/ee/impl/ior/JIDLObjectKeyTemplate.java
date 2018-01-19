


package com.sun.corba.ee.impl.ior;

import org.omg.CORBA_2_3.portable.InputStream ;
import org.omg.CORBA_2_3.portable.OutputStream ;

import org.omg.CORBA.OctetSeqHolder ;

import com.sun.corba.ee.spi.ior.ObjectId ;
import com.sun.corba.ee.spi.ior.ObjectKeyFactory ;

import com.sun.corba.ee.spi.orb.ORB ;
import com.sun.corba.ee.spi.orb.ORBVersion ;
import com.sun.corba.ee.spi.orb.ORBVersionFactory ;

import com.sun.corba.ee.impl.ior.ObjectKeyFactoryImpl ;


public final class JIDLObjectKeyTemplate extends NewObjectKeyTemplateBase
{
    
    public JIDLObjectKeyTemplate( ORB orb, int magic, int scid, InputStream is ) 
    {
        super( orb, magic, scid, is.read_long(), JIDL_ORB_ID, JIDL_OAID );

        setORBVersion( is ) ;
    }

    
    public JIDLObjectKeyTemplate( ORB orb, int magic, int scid, InputStream is,
        OctetSeqHolder osh ) 
    {
        super( orb, magic, scid, is.read_long(), JIDL_ORB_ID, JIDL_OAID );

        osh.value = readObjectKey( is ) ;

        setORBVersion( is ) ;
    }
    
    public JIDLObjectKeyTemplate( ORB orb, int scid, int serverid ) 
    {
        super( orb, ObjectKeyFactoryImpl.JAVAMAGIC_NEWER, scid, serverid, 
            JIDL_ORB_ID, JIDL_OAID ) ; 

        setORBVersion( ORBVersionFactory.getORBVersion() ) ;
    }
   
    protected void writeTemplate( OutputStream os )
    {
        os.write_long( getMagic() ) ;
        os.write_long( getSubcontractId() ) ;
        os.write_long( getServerId() ) ;
    }
}
