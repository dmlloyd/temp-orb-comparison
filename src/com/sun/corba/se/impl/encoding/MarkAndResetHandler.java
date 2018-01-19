
package com.sun.corba.se.impl.encoding;



interface MarkAndResetHandler
{
    void mark(RestorableInputStream inputStream);

    void fragmentationOccured(ByteBufferWithInfo newFragment);

    void reset();
}
