

package com.sun.corba.ee.impl.ior.iiop;

import org.omg.CORBA_2_3.portable.OutputStream;

import com.sun.corba.ee.spi.misc.ORBConstants;
import com.sun.corba.ee.impl.misc.ORBUtility;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.ior.TaggedComponentBase;

 
public class JavaSerializationComponent extends TaggedComponentBase {

    private byte version;

    private static JavaSerializationComponent singleton;

    static {
        singleton = new JavaSerializationComponent(
                                               ORBConstants.JAVA_ENC_VERSION);
    }

    public static JavaSerializationComponent singleton() {
        return singleton;
    }

    public JavaSerializationComponent(byte version) {
        this.version = version;
    }

    public byte javaSerializationVersion() {
        return this.version;
    }

    public void writeContents(OutputStream os) {
        os.write_octet(version);
    }
    
    public int getId() {
        return ORBConstants.TAG_JAVA_SERIALIZATION_ID;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof JavaSerializationComponent)) {
            return false;
        }
        JavaSerializationComponent other = (JavaSerializationComponent) obj;
        return this.version == other.version;
    }

    public int hashCode() {
        return this.version;
    }
}
