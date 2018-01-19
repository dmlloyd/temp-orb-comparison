

package xxxx;




interface MarkAndResetHandler
{
    void mark(RestorableInputStream inputStream);

    void fragmentationOccured(ByteBuffer byteBuffer);

    void reset();
}
