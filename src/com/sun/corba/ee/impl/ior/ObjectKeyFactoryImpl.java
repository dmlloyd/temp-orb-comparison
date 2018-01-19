


package com.sun.corba.ee.impl.ior;

import org.omg.CORBA.MARSHAL ;
import org.omg.CORBA.OctetSeqHolder ;
import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.ee.spi.ior.ObjectId ;
import com.sun.corba.ee.spi.ior.ObjectKey ;
import com.sun.corba.ee.spi.ior.ObjectKeyFactory ;
import com.sun.corba.ee.spi.ior.ObjectKeyTemplate ;

import com.sun.corba.ee.spi.orb.ORB ;

import com.sun.corba.ee.spi.misc.ORBConstants ;

import com.sun.corba.ee.spi.logging.IORSystemException ;

import com.sun.corba.ee.impl.encoding.EncapsInputStream ;
import com.sun.corba.ee.impl.encoding.EncapsInputStreamFactory;


interface Handler {
    ObjectKeyTemplate handle( int magic, int scid, 
        InputStream is, OctetSeqHolder osh ) ;
}


public class ObjectKeyFactoryImpl implements ObjectKeyFactory
{
    private static final IORSystemException wrapper =
        IORSystemException.self ;

    public static final int MAGIC_BASE                  = 0xAFABCAFE ;

    
    
    public static final int JAVAMAGIC_OLD               = MAGIC_BASE ;

    
    public static final int JAVAMAGIC_NEW               = MAGIC_BASE + 1 ;

    
    
    
    public static final int JAVAMAGIC_NEWER             = MAGIC_BASE + 2 ;

    public static final int MAX_MAGIC                   = JAVAMAGIC_NEWER ;

    
    
    
    public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;  

    private final ORB orb ;

    public ObjectKeyFactoryImpl( ORB orb ) 
    {
        this.orb = orb ;
    }
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    private Handler fullKey = new Handler() {
        public ObjectKeyTemplate handle( int magic, int scid, 
            InputStream is, OctetSeqHolder osh ) {
                ObjectKeyTemplate oktemp = null ;

                if ((scid >= ORBConstants.FIRST_POA_SCID) && 
                    (scid <= ORBConstants.MAX_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER) {
                        oktemp = new POAObjectKeyTemplate(orb, magic, scid,
                            is, osh);
                    } else {
                        oktemp = new OldPOAObjectKeyTemplate(orb, magic, scid,
                            is, osh);
                    }
                } else if ((scid >= 0) && (scid < ORBConstants.FIRST_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER) {
                        oktemp =
                            new JIDLObjectKeyTemplate(orb, magic, scid,
                                is, osh);
                    } else {
                        oktemp =
                            new OldJIDLObjectKeyTemplate(orb, magic, scid,
                                is, osh);
                    }
                }

                return oktemp ;
            }
        } ;

    
    private Handler oktempOnly = new Handler() {
        public ObjectKeyTemplate handle( int magic, int scid, 
            InputStream is, OctetSeqHolder osh ) {
                ObjectKeyTemplate oktemp = null ;

                if ((scid >= ORBConstants.FIRST_POA_SCID) && 
                    (scid <= ORBConstants.MAX_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER) {
                        oktemp = new POAObjectKeyTemplate(orb, magic, scid, is);
                    } else {
                        oktemp =
                            new OldPOAObjectKeyTemplate(orb, magic, scid, is);
                    }
                } else if ((scid >= 0) && (scid < ORBConstants.FIRST_POA_SCID)) {
                    if (magic >= JAVAMAGIC_NEWER) {
                        oktemp =
                            new JIDLObjectKeyTemplate(orb, magic, scid, is);
                    } else {
                        oktemp =
                            new OldJIDLObjectKeyTemplate(orb, magic, scid, is);
                    }
                }

                return oktemp ;
            }
        } ;

    
    private boolean validMagic( int magic )
    {
        return (magic >= MAGIC_BASE) && (magic <= MAX_MAGIC) ;
    }

    
    private ObjectKeyTemplate create( InputStream is, Handler handler, 
        OctetSeqHolder osh ) 
    {
        ObjectKeyTemplate oktemp = null ;
        
        try {
            int magic = is.read_long() ;
                    
            if (validMagic( magic )) {
                int scid = is.read_long() ;
                oktemp = handler.handle( magic, scid, is, osh ) ;
            }
        } catch (MARSHAL mexc) {
            wrapper.createMarshalError( mexc ) ;
        }

        return oktemp ;
    }

    public ObjectKey create(byte[] key) {
        
        OctetSeqHolder osh = new OctetSeqHolder();
        EncapsInputStream is = EncapsInputStreamFactory.newEncapsInputStream(orb, key, key.length);

        ObjectKeyTemplate oktemp;
        try {
            oktemp = create(is, fullKey, osh);
        } finally {
            try {
                is.close();
            } catch (java.io.IOException e) {
                wrapper.ioexceptionDuringStreamClose(e);
            }
        }
        if (oktemp == null) {
            oktemp = orb.getWireObjectKeyTemplate(); 
            osh.value = key;
        }

        ObjectId oid = new ObjectIdImpl( osh.value ) ;
        return new ObjectKeyImpl( oktemp, oid ) ;
    }

    public ObjectKeyTemplate createTemplate( InputStream is ) 
    {
        ObjectKeyTemplate oktemp = create( is, oktempOnly, null ) ;
        if (oktemp == null) {
            oktemp = orb.getWireObjectKeyTemplate(); 
        }

        return oktemp ;
    }
}
