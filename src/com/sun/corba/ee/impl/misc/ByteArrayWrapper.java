


package com.sun.corba.ee.impl.misc;

import java.util.Arrays;

public class ByteArrayWrapper {

    private byte[] objKey;

    public ByteArrayWrapper(byte[] objKey) {
        this.objKey = objKey;
    }

    public byte[] getObjKey() {
        return objKey;
    }

    public boolean equals( Object obj ) {  
        if (obj == null)
            return false ;

        if (obj instanceof ByteArrayWrapper) {   
            return Arrays.equals(objKey, ((ByteArrayWrapper)obj).getObjKey());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(objKey);

    }


}
