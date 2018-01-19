

package com.sun.corba.ee.impl.encoding;


import java.nio.ByteBuffer;


interface MarkAndResetHandler
{
    void mark(RestorableInputStream inputStream);

    void fragmentationOccured(ByteBuffer byteBuffer);

    void reset();
}
