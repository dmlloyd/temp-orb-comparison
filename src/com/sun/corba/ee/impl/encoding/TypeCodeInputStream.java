


package com.sun.corba.ee.impl.encoding;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;



import org.omg.CORBA_2_3.portable.InputStream;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.impl.corba.TypeCodeImpl;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.impl.encoding.CDRInputObject;

public class TypeCodeInputStream extends EncapsInputStream implements TypeCodeReader
{
    private Map<Integer,TypeCodeImpl> typeMap = null;
    private InputStream enclosure = null;

    public TypeCodeInputStream(org.omg.CORBA.ORB orb, byte[] data, int size) {
        super(orb, data, size);
    }

    public TypeCodeInputStream(org.omg.CORBA.ORB orb,
                               byte[] data,
                               int size,
                               ByteOrder byteOrder,
                               GIOPVersion version) {
        super(orb, data, size, byteOrder, version);
    }

    TypeCodeInputStream(org.omg.CORBA.ORB orb,
                               ByteBuffer byteBuffer,
                               int size,
                               ByteOrder byteOrder,
                               GIOPVersion version) {
        super(orb, byteBuffer, size, byteOrder, version);
    }

    public void addTypeCodeAtPosition(TypeCodeImpl tc, int position) {
        if (typeMap == null) {
            typeMap = new HashMap<Integer,TypeCodeImpl>(16);
        }
        typeMap.put(position, tc);
    }

    public TypeCodeImpl getTypeCodeAtPosition(int position) {
        if (typeMap == null)
            return null;
        return typeMap.get(position);
    }

    public void setEnclosingInputStream(InputStream enclosure) {
        this.enclosure = enclosure;
    }

    public TypeCodeReader getTopLevelStream() {
        if (enclosure == null)
            return this;
        if (enclosure instanceof TypeCodeReader)
            return ((TypeCodeReader)enclosure).getTopLevelStream();
        return this;
    }

    public int getTopLevelPosition() {
        if (enclosure != null && enclosure instanceof TypeCodeReader) {
            
            
            
            int topPos = ((TypeCodeReader)enclosure).getTopLevelPosition();
            
            
            return topPos - getBufferLength() + getPosition();
        }
        return getPosition();
    }

    public static TypeCodeInputStream readEncapsulation(InputStream is, org.omg.CORBA.ORB _orb) {
        
        TypeCodeInputStream encap;

        int encapLength = is.read_long();

        
        byte[] encapBuffer = new byte[encapLength];
        is.read_octet_array(encapBuffer, 0, encapBuffer.length);

        
        if (is instanceof CDRInputObject) {
            encap = EncapsInputStreamFactory.newTypeCodeInputStream(_orb, encapBuffer, encapBuffer.length,
                                            ((CDRInputObject)is).getByteOrder(),
                                            ((CDRInputObject)is).getGIOPVersion());
        } else {
            encap = EncapsInputStreamFactory.newTypeCodeInputStream(_orb, encapBuffer, encapBuffer.length);
        }
        encap.setEnclosingInputStream(is);
        encap.makeEncapsulation();
        return encap;
    }

    protected void makeEncapsulation() {
        
        consumeEndian();
    }

    public void printTypeMap() {
        System.out.println("typeMap = {");
        for (Integer pos : typeMap.keySet() ) {
            System.out.println( "  key = " + pos + ", value = " +
                typeMap.get(pos).description() ) ;
        }
        System.out.println("}") ;
    }
}
