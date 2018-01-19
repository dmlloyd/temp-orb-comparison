


package com.sun.corba.ee.impl.interceptors;







public final class CDREncapsCodec 
    extends org.omg.CORBA.LocalObject 
    implements Codec 
{
    
    private transient ORB orb;
    static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    
    private transient GIOPVersion giopVersion;

    

    
    public CDREncapsCodec( ORB orb, int major, int minor ) {
        this.orb = orb;

        giopVersion = GIOPVersion.getInstance( (byte)major, (byte)minor );
    }

    
    public byte[] encode( Any data ) 
        throws InvalidTypeForEncoding 
    {
        if ( data == null ) 
            throw wrapper.nullParamNoComplete() ;
        return encodeImpl( data, true );
    }

    
    public Any decode ( byte[] data ) 
        throws FormatMismatch 
    {
        if( data == null ) 
            throw wrapper.nullParamNoComplete() ;
        return decodeImpl( data, null );
    }

    
    public byte[] encode_value( Any data ) 
        throws InvalidTypeForEncoding 
    {
        if( data == null ) 
            throw wrapper.nullParamNoComplete() ;
        return encodeImpl( data, false );
    }

    
    public Any decode_value( byte[] data, TypeCode tc ) 
        throws FormatMismatch, TypeMismatch
    {
        if( data == null ) 
            throw wrapper.nullParamNoComplete() ;
        if( tc == null ) 
            throw  wrapper.nullParamNoComplete() ;
        return decodeImpl( data, tc );
    }

    
    private byte[] encodeImpl( Any data, boolean sendTypeCode ) 
        throws InvalidTypeForEncoding 
    {
        if( data == null ) 
            throw wrapper.nullParamNoComplete() ;

        
        
        
        
        
        
        
        
        

        byte[] retValue;

        
        
        

        boolean pop = false;
        if (ORBUtility.getEncodingVersion() !=
            ORBConstants.CDR_ENC_VERSION) {
            ORBUtility.pushEncVersionToThreadLocalState(ORBConstants.CDR_ENC_VERSION);
            pop = true;
        }

        try {

            
            EncapsOutputStream cdrOut =
                OutputStreamFactory.newEncapsOutputStream((com.sun.corba.ee.spi.orb.ORB)orb,
                                       giopVersion);

            
            cdrOut.putEndian();

            
            if( sendTypeCode ) {
                cdrOut.write_TypeCode( data.type() );
            }

            
            data.write_value( cdrOut );

            retValue = cdrOut.toByteArray();

        } finally {
            if (pop) {
                ORBUtility.popEncVersionFromThreadLocalState();
            }
        }
        
        return retValue;
    }

    
    private Any decodeImpl( byte[] data, TypeCode tc ) 
        throws FormatMismatch 
    {
        if( data == null ) 
            throw wrapper.nullParamNoComplete() ;

        AnyImpl any = null;  

        
        
        
        

        
        
        

        boolean pop = false;
        if (ORBUtility.getEncodingVersion() !=
            ORBConstants.CDR_ENC_VERSION) {
            ORBUtility.pushEncVersionToThreadLocalState(ORBConstants.CDR_ENC_VERSION);
            pop = true;
        }

        try {

            EncapsInputStream cdrIn = EncapsInputStreamFactory.newEncapsInputStream( orb, data, 
                data.length, giopVersion );

            cdrIn.consumeEndian();

            
            if( tc == null ) {
                tc = cdrIn.read_TypeCode();
            }

            
            any = new AnyImpl( (com.sun.corba.ee.spi.orb.ORB)orb );
            any.read_value( cdrIn, tc );

        } catch( RuntimeException e ) {
            
            throw new FormatMismatch();
        } finally {
            if (pop) {
                ORBUtility.popEncVersionFromThreadLocalState();
            }
        }

        return any;
    }
}
