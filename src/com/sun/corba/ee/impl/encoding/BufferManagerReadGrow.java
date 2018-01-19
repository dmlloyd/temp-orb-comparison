


package xxxx;



public class BufferManagerReadGrow
    implements BufferManagerRead, MarkAndResetHandler
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    public void processFragment (ByteBuffer byteBuffer, FragmentMessage header)
    {
        
        
    }

    public void init(Message msg) {}

    public ByteBuffer underflow(ByteBuffer byteBuffer) {
        throw wrapper.unexpectedEof() ;
    }

    @Override
    public boolean isFragmentOnUnderflow() {
        return false;
    }

    public void cancelProcessing(int requestId) {}
    
    

    private Object streamMemento;
    private RestorableInputStream inputStream;
    private boolean markEngaged = false;

    public MarkAndResetHandler getMarkAndResetHandler() {
        return this;
    }

    public void mark(RestorableInputStream is) {
        markEngaged = true;
        inputStream = is;
        streamMemento = inputStream.createStreamMemento();
    }

    
    public void fragmentationOccured(ByteBuffer byteBuffer) {}

    public void reset() {

        if (!markEngaged)
            return;

        markEngaged = false;
        inputStream.restoreInternalState(streamMemento);
        streamMemento = null;
    }

    
    public void close(ByteBuffer byteBuffer) {}
}
