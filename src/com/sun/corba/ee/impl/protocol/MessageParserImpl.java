


package xxxx;






@Transport
@Giop
public class MessageParserImpl implements MessageParser {

    private static final int NUM_BYTES_IN_INTEGER = 4;
    private final static int MESSAGE_LENGTH_INDEX = Message.GIOPMessageHeaderLength - NUM_BYTES_IN_INTEGER;
    private final static int MESSAGE_FLAG_INDEX = 6;
    final private ORB orb;
    private boolean expectingMoreData;
    private boolean moreBytesToParse;
    private int nextMsgStartPos;
    private int sizeNeeded;
    
    private List<RequestId> fragmentList;
    private ByteBuffer msgByteBuffer;

    
    private ByteBuffer remainderBuffer;

    
    private MessageMediator messageMediator;
    private Connection connection;
    private boolean expectingFragments;

    
    public MessageParserImpl(ORB orb) {
        this.orb = orb;
        this.expectingMoreData = false;
        this.moreBytesToParse = false;
        this.nextMsgStartPos = 0;
        this.fragmentList = new LinkedList<RequestId>();
        this.sizeNeeded = orb.getORBData().getReadByteBufferSize();
    }

    public MessageParserImpl(ORB orb, Connection connection) {
        this(orb);
        this.connection = connection;
    }

    @Transport
    public ByteBuffer getNewBufferAndCopyOld(ByteBuffer byteBuffer) {
        ByteBuffer newByteBuffer = null;
        
        
        byteBuffer.position(getNextMessageStartPosition());
        newByteBuffer = orb.getByteBufferPool().reAllocate(byteBuffer,
                getSizeNeeded());
        setNextMessageStartPosition(0);
        return newByteBuffer;
    }

    
    public boolean isExpectingMoreData() {
        return expectingMoreData;
    }

    @Override
    public boolean isExpectingFragments() {
        return expectingFragments;
    }

    @Override
    public ByteBuffer getMsgByteBuffer() {
        return msgByteBuffer;
    }

    @Override
    public void offerBuffer(ByteBuffer buffer) {
        msgByteBuffer = null;
        messageMediator = null;
        if (buffer == null) return;

        if (!containsFullHeader(buffer) || !containsFullMessage(buffer))
            remainderBuffer = buffer;
        else {
            remainderBuffer = splitAndReturnRemainder(buffer, getTotalMessageLength(buffer));
            MessageBase message = MessageBase.parseGiopHeader(orb, connection, buffer, 0);
            messageMediator = new MessageMediatorImpl(orb, connection, message, buffer);
            msgByteBuffer = buffer;
            expectingFragments = message.moreFragmentsToFollow();
        }

    }

    
    private ByteBuffer splitAndReturnRemainder(ByteBuffer buffer, int splitPosition) {
        assert splitPosition <= buffer.limit();

        if (buffer.limit() == splitPosition)
            return null;
        else {
            final int oldPosition = buffer.position();
            buffer.position(splitPosition);
            ByteBuffer remainderBuffer = buffer.slice();
            buffer.position(oldPosition);
            buffer.limit(splitPosition);
            return remainderBuffer;
        }
    }

    private boolean containsFullHeader(ByteBuffer buffer) {
        return buffer.remaining() >= Message.GIOPMessageHeaderLength;
    }

    private boolean containsFullMessage(ByteBuffer buffer) {
        return containsFullHeader(buffer) && buffer.remaining() >= getTotalMessageLength(buffer);
    }

    private int getTotalMessageLength(ByteBuffer buffer) {
        return Message.GIOPMessageHeaderLength + getMessageBodyLength(buffer);
    }

    private int getMessageBodyLength(ByteBuffer buffer) {
        buffer.order(getByteOrder(buffer.get(MESSAGE_FLAG_INDEX)));
        return buffer.getInt(MESSAGE_LENGTH_INDEX);
    }

    private ByteOrder getByteOrder(byte messageFlag) {
        return (messageFlag & Message.LITTLE_ENDIAN_BIT) == 0 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    @Override
    public ByteBuffer getRemainderBuffer() {
        return remainderBuffer;
    }

    @Override
    public MessageMediator getMessageMediator() {
        return messageMediator;
    }

    @Override
    public void checkTimeout(long timeSinceLastInput) {
        if (isMidMessage() && timeLimitExceeded(timeSinceLastInput)) throw new COMM_FAILURE();
    }

    private boolean timeLimitExceeded(long timeSinceLastInput) {
        return timeSinceLastInput > ORBConstants.TRANSPORT_TCP_MAX_TIME_TO_WAIT;
    }

    private boolean isMidMessage() {
        return expectingFragments || (remainderBuffer != null && !containsFullMessage(remainderBuffer));
    }

    @Transport
    public Message parseBytes(ByteBuffer byteBuffer, Connection connection) {
        expectingMoreData = false;
        remainderBuffer  = byteBuffer;
        Message message = null;
        int bytesInBuffer = byteBuffer.limit() - nextMsgStartPos;
        
        if (bytesInBuffer >= Message.GIOPMessageHeaderLength) {
            
            message = MessageBase.parseGiopHeader(orb, connection, byteBuffer, nextMsgStartPos);
            
            
            if (bytesInBuffer >= message.getSize()) {

                
                int savedLimit = byteBuffer.limit();
                byteBuffer.position(nextMsgStartPos).
                        limit(nextMsgStartPos + message.getSize());
                msgByteBuffer = byteBuffer.slice();
                
                nextMsgStartPos = byteBuffer.limit();
                byteBuffer.position(nextMsgStartPos).limit(savedLimit);

                if (message.supportsFragments()) {
                    if (message.moreFragmentsToFollow()) {
                        addRequestIdToFragmentList(message, msgByteBuffer);
                    } else if (isEndOfFragmentList(message)) {
                        removeRequestIdFromFragmentList(message, msgByteBuffer);
                    }
                    expectingMoreData = stillLookingForFragments();
                }

                moreBytesToParse = byteBuffer.hasRemaining();
                if (!moreBytesToParse) byteBuffer.limit(byteBuffer.capacity());
                sizeNeeded = orb.getORBData().getReadByteBufferSize();
            } else {
                
                moreBytesToParse = false;
                expectingMoreData = true;
                
                byteBuffer.position(byteBuffer.limit()).limit(byteBuffer.capacity());
                sizeNeeded = message.getSize();
                message = null;
            }
        } else {
            
            
            moreBytesToParse = false;
            expectingMoreData = true;
            
            byteBuffer.position(byteBuffer.limit()).limit(byteBuffer.capacity());
            sizeNeeded = orb.getORBData().getReadByteBufferSize();
        }
        return message;
    }

    private boolean stillLookingForFragments() {
        return fragmentList.size() > 0;
    }

    private boolean isEndOfFragmentList(Message message) {
        return message.getType() == MessageBase.GIOPFragment ||
            message.getType() == MessageBase.GIOPCancelRequest;
    }

    private void removeRequestIdFromFragmentList(Message message, ByteBuffer byteBuffer) {
        
        RequestId requestId = MessageBase.getRequestIdFromMessageBytes(message, byteBuffer);
        if (fragmentList.size() > 0 &&
            fragmentList.remove(requestId)) {
        }
    }

    private void addRequestIdToFragmentList(Message message, ByteBuffer byteBuffer) {
        
        RequestId requestId = MessageBase.getRequestIdFromMessageBytes(message, byteBuffer);
        if (!fragmentList.contains(requestId)) {
            fragmentList.add(requestId);
        }
    }

    
    public boolean hasMoreBytesToParse() {
        return moreBytesToParse;
    }
    
    
    public void setNextMessageStartPosition(int position) {
        this.nextMsgStartPos = position;
    }
    
    
    public int getNextMessageStartPosition() {
        return this.nextMsgStartPos;
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toStringPrefix()).append("]");
        return sb.toString();
    }

    
    private String toStringPrefix() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessageParserImpl[nextMsgStartPos=").append(nextMsgStartPos).append(", expectingMoreData=").append(expectingMoreData);
        sb.append(", moreBytesToParse=").append(moreBytesToParse).append(", fragmentList size=").append(fragmentList.size());
        sb.append(", size needed=").append(sizeNeeded).append("]");
        return sb.toString();
    }

    
    public int getSizeNeeded() {
        return sizeNeeded;
    }
}
