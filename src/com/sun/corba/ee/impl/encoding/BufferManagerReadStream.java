

package xxxx;





@Transport
public class BufferManagerReadStream
        implements BufferManagerRead, MarkAndResetHandler {
    private static final ORBUtilSystemException wrapper =
            ORBUtilSystemException.self;

    private volatile boolean receivedCancel = false;
    private int cancelReqId = 0;

    
    private boolean endOfStream = true;
    private final BufferQueue fragmentQueue = new BufferQueue();
    
    
    
    
    private final ORB orb;

    BufferManagerReadStream(ORB orb) {
        this.orb = orb;
    }

    public void cancelProcessing(int requestId) {
        synchronized (fragmentQueue) {
            receivedCancel = true;
            cancelReqId = requestId;
            fragmentQueue.notify();
        }
    }

    @InfoMethod
    private void bufferMessage(String msg, int bbAddr, String tail) {}

    @Transport
    public void processFragment(ByteBuffer byteBuffer, FragmentMessage msg) {
        byteBuffer.position(msg.getHeaderLength());

        synchronized (fragmentQueue) {
            if (orb.transportDebugFlag) {
                logBufferMessage("processFragment() - queuing ByteByffer id (", byteBuffer, ") to fragment queue.");
            }
            fragmentQueue.enqueue(byteBuffer);
            endOfStream = !msg.moreFragmentsToFollow();
            fragmentQueue.notify();
        }
    }

    @InfoMethod
    private void underflowMessage(String msg, int rid) {
    }

    @Transport
    public ByteBuffer underflow(ByteBuffer byteBuffer) {

        ByteBuffer result;

        synchronized (fragmentQueue) {

            if (receivedCancel) {
                underflowMessage("underflow() - Cancel request id:", cancelReqId);
                throw new RequestCanceledException(cancelReqId);
            }

            while (fragmentQueue.size() == 0) {

                if (endOfStream) {
                    throw wrapper.endOfStream();
                }

                boolean interrupted = false;
                try {
                    fragmentQueue.wait(orb.getORBData().fragmentReadTimeout());
                } catch (InterruptedException e) {
                    interrupted = true;
                }

                if (!interrupted && fragmentQueue.size() == 0) {
                    throw wrapper.bufferReadManagerTimeout();
                }

                if (receivedCancel) {
                    underflowMessage("underflow() - Cancel request id after wait:", cancelReqId);
                    throw new RequestCanceledException(cancelReqId);
                }
            }

            result = fragmentQueue.dequeue();

            
            
            
            if (!markEngaged && byteBuffer != null) {
                getByteBufferPool().releaseByteBuffer(byteBuffer);
            }
        }
        return result;
    }

    @Override
    public boolean isFragmentOnUnderflow() {
        return true;
    }

    public void init(Message msg) {
        if (msg != null) {
            endOfStream = !msg.moreFragmentsToFollow();
        }
    }

    
    @Transport
    public void close(ByteBuffer byteBuffer) {
        int inputBbAddress = 0;

        if (byteBuffer != null) {
            inputBbAddress = System.identityHashCode(byteBuffer);
        }
        ByteBufferPool byteBufferPool = getByteBufferPool();

        
        synchronized (fragmentQueue) {
            
            
            
            
            
            
            

            ByteBuffer aBuffer;
            while (fragmentQueue.size() != 0) {
                aBuffer = fragmentQueue.dequeue();
                if (aBuffer != null) {
                    byteBufferPool.releaseByteBuffer(aBuffer);
                }
            }
        }
        fragmentQueue.clear();

        
        if (fragmentStack != null && fragmentStack.size() != 0) {
            
            
            
            
            
            
            

            for (ByteBuffer aBuffer : fragmentStack) {
                if (aBuffer != null) {
                    if (inputBbAddress != System.identityHashCode(aBuffer)) {
                        byteBufferPool.releaseByteBuffer(aBuffer);
                    }
                }
            }

            fragmentStack = null;
        }
    }

    private void logBufferMessage(String prefix, ByteBuffer byteBuffer, String suffix) {
        bufferMessage(prefix, System.identityHashCode(byteBuffer), suffix);
    }

    protected ByteBufferPool getByteBufferPool() {
        return orb.getByteBufferPool();
    }

    

    private boolean markEngaged = false;

    
    
    private LinkedList<ByteBuffer> fragmentStack = null;
    private RestorableInputStream inputStream = null;

    
    private Object streamMemento = null;

    public void mark(RestorableInputStream inputStream) {
        this.inputStream = inputStream;
        markEngaged = true;

        
        
        streamMemento = inputStream.createStreamMemento();

        if (fragmentStack != null) {
            fragmentStack.clear();
        }
    }

    
    public void fragmentationOccured(ByteBuffer newFrament) {
        if (!markEngaged) {
            return;
        }

        if (fragmentStack == null) {
            fragmentStack = new LinkedList<ByteBuffer>();
        }

        fragmentStack.addFirst(newFrament.duplicate());
    }

    public void reset() {
        if (!markEngaged) {
            
            return;
        }

        markEngaged = false;

        
        
        
        if (fragmentStack != null && fragmentStack.size() != 0) {

            synchronized (fragmentQueue) {
                for (ByteBuffer aBuffer : fragmentStack) {
                    fragmentQueue.push(aBuffer);
                }
            }

            fragmentStack.clear();
        }

        
        
        inputStream.restoreInternalState(streamMemento);
    }

    public MarkAndResetHandler getMarkAndResetHandler() {
        return this;
    }
}
