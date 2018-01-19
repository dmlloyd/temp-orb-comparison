

package com.sun.corba.ee.impl.encoding;




interface MarkAndResetHandler
{
    void mark(RestorableInputStream inputStream);

    void fragmentationOccured(ByteBuffer byteBuffer);

    void reset();
}
