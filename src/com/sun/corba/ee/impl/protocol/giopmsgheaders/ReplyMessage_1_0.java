


package xxxx;








public final class ReplyMessage_1_0 extends Message_1_0
        implements ReplyMessage {

    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    

    private ORB orb = null;
    private ServiceContexts service_contexts = null;
    private int request_id = (int) 0;
    private int reply_status = (int) 0;
    private IOR ior = null;
    private String exClassName = null;
    private int minorCode = (int) 0;
    private CompletionStatus completionStatus = null;

    

    ReplyMessage_1_0(ORB orb) {
        this.service_contexts = ServiceContextDefaults.makeServiceContexts( orb ) ;
        this.orb = orb;
    }

    ReplyMessage_1_0(ORB orb, ServiceContexts _service_contexts,
            int _request_id, int _reply_status, IOR _ior) {
        super(Message.GIOPBigMagic, false, Message.GIOPReply, 0);
        this.orb = orb;
        service_contexts = _service_contexts;
        request_id = _request_id;
        reply_status = _reply_status;
        ior = _ior;
    }

    

    public int getRequestId() {
        return this.request_id;
    }

    public int getReplyStatus() {
        return this.reply_status;
    }
    
    public short getAddrDisposition() {
        return KeyAddr.value;
    }

    public ServiceContexts getServiceContexts() {
        return this.service_contexts;
    }

    public SystemException getSystemException(String message) {
        return MessageBase.getSystemException(
            exClassName, minorCode, completionStatus, message, wrapper);
    }

    public IOR getIOR() {
        return this.ior;
    }

    public void setIOR( IOR ior ) {
        this.ior = ior;
    }

    

    public void read(org.omg.CORBA.portable.InputStream istream) {
        super.read(istream);
        this.service_contexts = ServiceContextDefaults.makeServiceContexts(
            (org.omg.CORBA_2_3.portable.InputStream) istream);
        this.request_id = istream.read_ulong();
        this.reply_status = istream.read_long();
        isValidReplyStatus(this.reply_status); 

        
        
        if (this.reply_status == SYSTEM_EXCEPTION) {

            String reposId = istream.read_string();
            this.exClassName = ORBUtility.classNameOf(reposId);
            this.minorCode = istream.read_long();
            int status = istream.read_long();

            switch (status) {
            case CompletionStatus._COMPLETED_YES:
                this.completionStatus = CompletionStatus.COMPLETED_YES;
                break;
            case CompletionStatus._COMPLETED_NO:
                this.completionStatus = CompletionStatus.COMPLETED_NO;
                break;
            case CompletionStatus._COMPLETED_MAYBE:
                this.completionStatus = CompletionStatus.COMPLETED_MAYBE;
                break;
            default:
                throw wrapper.badCompletionStatusInReply( status );
            }

        } else if (this.reply_status == USER_EXCEPTION) {
            
        } else if (this.reply_status == LOCATION_FORWARD) {
            CDRInputObject cdr = (CDRInputObject) istream;
            this.ior = IORFactories.makeIOR( orb, (InputStream)cdr ) ;
        }
    }

    
    
    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        super.write(ostream);
        service_contexts.write(
            (org.omg.CORBA_2_3.portable.OutputStream) ostream,
            GIOPVersion.V1_0);
        ostream.write_ulong(this.request_id);
        ostream.write_long(this.reply_status);
    }

    

    public static void isValidReplyStatus(int replyStatus) {
        switch (replyStatus) {
        case NO_EXCEPTION :
        case USER_EXCEPTION :
        case SYSTEM_EXCEPTION :
        case LOCATION_FORWARD :
            break;
        default :
            throw wrapper.illegalReplyStatus();
        }
    }

    public void callback(MessageHandler handler)
        throws java.io.IOException
    {
        handler.handleInput(this);
    }
} 
