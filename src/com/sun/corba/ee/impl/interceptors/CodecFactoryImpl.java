


package com.sun.corba.ee.impl.interceptors;

import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.Encoding;
import org.omg.IOP.ENCODING_CDR_ENCAPS;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;

import org.omg.CORBA.ORB;


public final class CodecFactoryImpl 
    extends org.omg.CORBA.LocalObject
    implements CodecFactory 
{
    
    private transient ORB orb;
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    
    
    private static final int MAX_MINOR_VERSION_SUPPORTED = 2;

    
    
    private Codec codecs[] = new Codec[MAX_MINOR_VERSION_SUPPORTED + 1];

    
    public CodecFactoryImpl( ORB orb ) {
        this.orb = orb;

        
        
        
        
        
        for( int minor = 0; minor <= MAX_MINOR_VERSION_SUPPORTED; minor++ ) {
            codecs[minor] = new CDREncapsCodec( orb, 1, minor );
        }
    }

    
    public Codec create_codec ( Encoding enc ) 
        throws UnknownEncoding 
    {
        if( enc == null ) nullParam();

        Codec result = null;

        
        if( (enc.format == ENCODING_CDR_ENCAPS.value) &&
            (enc.major_version == 1) ) 
        {
            if( (enc.minor_version >= 0) && 
                (enc.minor_version <= MAX_MINOR_VERSION_SUPPORTED) ) 
            {
                result = codecs[enc.minor_version];
            }
        }

        if( result == null ) {
            throw new UnknownEncoding();
        }

        return result;
    }

    
    private void nullParam() 
    {
        throw wrapper.nullParamNoComplete() ;
    }
}
