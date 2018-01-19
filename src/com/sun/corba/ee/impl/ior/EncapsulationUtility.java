


package com.sun.corba.ee.impl.ior;

import java.util.List;

import org.omg.CORBA_2_3.portable.OutputStream ;
import org.omg.CORBA_2_3.portable.InputStream ;

import com.sun.corba.ee.spi.ior.Identifiable ;
import com.sun.corba.ee.spi.ior.IdentifiableFactoryFinder ;
import com.sun.corba.ee.spi.ior.WriteContents ;

import com.sun.corba.ee.spi.orb.ORB ;

import com.sun.corba.ee.impl.encoding.CDROutputObject ;
import com.sun.corba.ee.impl.encoding.EncapsOutputStream ;
import com.sun.corba.ee.impl.encoding.OutputStreamFactory;
import com.sun.corba.ee.impl.encoding.EncapsInputStream ;
import com.sun.corba.ee.impl.encoding.EncapsInputStreamFactory;


public final class EncapsulationUtility 
{
    private EncapsulationUtility()
    {
    }

    
    public static <E extends Identifiable> void readIdentifiableSequence( 
        List<E> container,
        IdentifiableFactoryFinder<E> finder, InputStream istr) 
    {
        int count = istr.read_long() ;
        for (int ctr = 0; ctr<count; ctr++) {
            int id = istr.read_long() ;
            E obj = finder.create( id, istr ) ;
            container.add( obj ) ;
        }
    }

    
    public static <E extends Identifiable> void writeIdentifiableSequence( 
        List<E> container, OutputStream os) 
    {
        os.write_long( container.size() ) ;
        for (Identifiable obj : container) {
            os.write_long( obj.getId() ) ;
            obj.write( os ) ;
        }
    }

    
    public static void writeOutputStream( OutputStream dataStream,
        OutputStream os ) 
    {
        byte[] data = ((CDROutputObject)dataStream).toByteArray() ;
        os.write_long( data.length ) ;
        os.write_octet_array( data, 0, data.length ) ;
    }

    
    public static InputStream getEncapsulationStream( ORB orb, InputStream is )
    {
        byte[] data = readOctets( is ) ;
        EncapsInputStream result = EncapsInputStreamFactory.newEncapsInputStream( orb, data, 
            data.length ) ;
        result.consumeEndian() ;
        return result ;
    } 

    
    public static byte[] readOctets( InputStream is ) 
    {
        int len = is.read_ulong() ;
        byte[] data = new byte[len] ;
        is.read_octet_array( data, 0, len ) ;
        return data ;
    }

    public static void writeEncapsulation( WriteContents obj,
        OutputStream os )
    {
        EncapsOutputStream out = OutputStreamFactory.newEncapsOutputStream( (ORB)os.orb() ) ;

        out.putEndian() ;

        obj.writeContents( out ) ;

        writeOutputStream( out, os ) ;
    }
}
