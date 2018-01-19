


package com.sun.corba.ee.impl.protocol.giopmsgheaders;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.IOP.TaggedProfile;

import com.sun.corba.ee.spi.ior.ObjectKey;
import com.sun.corba.ee.spi.ior.IOR;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfile;
import com.sun.corba.ee.spi.ior.iiop.IIOPFactories;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.ior.iiop.RequestPartitioningComponent;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.orb.ObjectKeyCacheEntry;
import com.sun.corba.ee.spi.misc.ORBClassLoader;
import com.sun.corba.ee.spi.protocol.RequestId;
import com.sun.corba.ee.spi.servicecontext.ServiceContexts;
import com.sun.corba.ee.spi.transport.Connection;
import com.sun.corba.ee.spi.transport.TransportManager;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException;
import com.sun.corba.ee.impl.orb.ObjectKeyCacheEntryNoObjectAdapterImpl;
import com.sun.corba.ee.impl.misc.ORBUtility;
import com.sun.corba.ee.spi.misc.ORBConstants;
import com.sun.corba.ee.impl.protocol.AddressingDispositionException;
import com.sun.corba.ee.impl.protocol.RequestIdImpl;
import com.sun.corba.ee.impl.transport.MessageTraceManagerImpl;
import com.sun.corba.ee.spi.trace.Giop;



@Giop
public abstract class MessageBase implements Message {

    private int threadPoolToUse;

    
    
    private byte encodingVersion = ORBConstants.CDR_ENC_VERSION;

    private static final ORBUtilSystemException wrapper =
            ORBUtilSystemException.self;

    

    public static String typeToString(byte type) {
        String result = type + "/";
        switch (type) {
            case GIOPRequest:
                result += "GIOPRequest";
                break;
            case GIOPReply:
                result += "GIOPReply";
                break;
            case GIOPCancelRequest:
                result += "GIOPCancelRequest";
                break;
            case GIOPLocateRequest:
                result += "GIOPLocateRequest";
                break;
            case GIOPLocateReply:
                result += "GIOPLocateReply";
                break;
            case GIOPCloseConnection:
                result += "GIOPCloseConnection";
                break;
            case GIOPMessageError:
                result += "GIOPMessageError";
                break;
            case GIOPFragment:
                result += "GIOPFragment";
                break;
            default:
                result += "Unknown";
                break;
        }
        return result;
    }

    public static MessageBase parseGiopHeader(ORB orb,
                                              Connection connection,
                                              ByteBuffer buf,
                                              int startPosition) {

        TransportManager ctm = orb.getTransportManager();
        MessageTraceManagerImpl mtm =
                (MessageTraceManagerImpl) ctm.getMessageTraceManager();
        if (mtm.isEnabled()) {
            mtm.recordHeaderReceived(buf);
        }

        

        

        byte[] it = new byte[12];
        buf.position(startPosition);
        buf.get(it);

        int b1, b2, b3, b4;
        b1 = (it[0] << 24) & 0xFF000000;
        b2 = (it[1] << 16) & 0x00FF0000;
        b3 = (it[2] << 8) & 0x0000FF00;
        b4 = (it[3]) & 0x000000FF;

        int magic = (b1 | b2 | b3 | b4);

        if (magic != GIOPBigMagic) {
            
            
            throw wrapper.giopMagicError();
        }

        
        
        
        byte requestEncodingVersion = ORBConstants.CDR_ENC_VERSION;
        if ((it[4] == GIOPVersion.V13_XX.getMajor()) &&
                (it[5] <= ORBConstants.JAVA_ENC_VERSION) &&
                (it[5] > ORBConstants.CDR_ENC_VERSION)) {
            
            
            requestEncodingVersion = it[5];
            buf.put(startPosition + 4, GIOPVersion.V1_2.getMajor());
            buf.put(startPosition + 5, GIOPVersion.V1_2.getMinor());
            it[4] = GIOPVersion.V1_2.getMajor();
            it[5] = GIOPVersion.V1_2.getMinor();
        }

        GIOPVersion orbVersion = orb.getORBData().getGIOPVersion();

        if (orb.giopDebugFlag) {
            dprint(".parseGIOPHeader: Message GIOP version: "
                    + it[4] + '.' + it[5]);
            dprint(".parseGIOPHeader: ORB Max GIOP Version: "
                    + orbVersion);
        }

        if ((it[4] > orbVersion.getMajor()) ||
                ((it[4] == orbVersion.getMajor()) && (it[5] > orbVersion.getMinor()))
                ) {
            
            
            
            

            
            

            
            
            if (it[7] != GIOPMessageError) {
                throw wrapper.giopVersionError();
            }
        }

        AreFragmentsAllowed(it[4], it[5], it[6], it[7]);

        

        MessageBase msg;

        switch (it[7]) {

            case GIOPRequest:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: creating RequestMessage");
                }
                
                if ((it[4] == 0x01) && (it[5] == 0x00)) { 
                    msg = new RequestMessage_1_0(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x01)) { 
                    msg = new RequestMessage_1_1(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x02)) { 
                    msg = new RequestMessage_1_2(orb);
                } else {
                    throw wrapper.giopVersionError();
                }
                break;

            case GIOPLocateRequest:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: creating LocateRequestMessage");
                }
                
                if ((it[4] == 0x01) && (it[5] == 0x00)) { 
                    msg = new LocateRequestMessage_1_0(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x01)) { 
                    msg = new LocateRequestMessage_1_1(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x02)) { 
                    msg = new LocateRequestMessage_1_2(orb);
                } else {
                    throw wrapper.giopVersionError();
                }
                break;

            case GIOPCancelRequest:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: creating CancelRequestMessage");
                }
                
                if ((it[4] == 0x01) && (it[5] == 0x00)) { 
                    msg = new CancelRequestMessage_1_0();
                } else if ((it[4] == 0x01) && (it[5] == 0x01)) { 
                    msg = new CancelRequestMessage_1_1();
                } else if ((it[4] == 0x01) && (it[5] == 0x02)) { 
                    msg = new CancelRequestMessage_1_2();
                } else {
                    throw wrapper.giopVersionError();
                }
                break;

            case GIOPReply:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: creating ReplyMessage");
                }
                
                if ((it[4] == 0x01) && (it[5] == 0x00)) { 
                    msg = new ReplyMessage_1_0(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x01)) { 
                    msg = new ReplyMessage_1_1(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x02)) { 
                    msg = new ReplyMessage_1_2(orb);
                } else {
                    throw wrapper.giopVersionError();
                }
                break;

            case GIOPLocateReply:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: creating LocateReplyMessage");
                }
                
                if ((it[4] == 0x01) && (it[5] == 0x00)) { 
                    msg = new LocateReplyMessage_1_0(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x01)) { 
                    msg = new LocateReplyMessage_1_1(orb);
                } else if ((it[4] == 0x01) && (it[5] == 0x02)) { 
                    msg = new LocateReplyMessage_1_2(orb);
                } else {
                    throw wrapper.giopVersionError();
                }
                break;

            case GIOPCloseConnection:
                
                
                
                
                
                
                
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: received CloseConnection message");
                }
                COMM_FAILURE comm_failure = wrapper.connectionRebind();
                connection.purgeCalls(comm_failure, false, true);
                throw comm_failure;

            case GIOPMessageError:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: creating Message for CloseConnection or MessageError");
                }
                
                
                
                
                
                if ((it[4] == 0x01) && (it[5] == 0x00)) { 
                    msg = new Message_1_0();
                } else if ((it[4] == 0x01) && (it[5] == 0x01)) { 
                    msg = new Message_1_1();
                } else if ((it[4] == 0x01) && (it[5] == 0x02)) { 
                    msg = new Message_1_1();
                } else {
                    throw wrapper.giopVersionError();
                }
                break;

            case GIOPFragment:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: creating FragmentMessage");
                }
                
                if ((it[4] == 0x01) && (it[5] == 0x00)) { 
                    
                    
                    
                    throw wrapper.giopVersionError();
                } else if ((it[4] == 0x01) && (it[5] == 0x01)) { 
                    msg = new FragmentMessage_1_1();
                } else if ((it[4] == 0x01) && (it[5] == 0x02)) { 
                    msg = new FragmentMessage_1_2();
                } else {
                    throw wrapper.giopVersionError();
                }
                break;

            default:
                if (orb.giopDebugFlag) {
                    dprint(".parseGIOPHeader: UNKNOWN MESSAGE TYPE: " + it[7]);
                }
                
                
                throw wrapper.giopVersionError();
        }

        
        
        

        if ((it[4] == 0x01) && (it[5] == 0x00)) { 
            Message_1_0 msg10 = (Message_1_0) msg;
            msg10.magic = magic;
            msg10.GIOP_version = new GIOPVersion(it[4], it[5]);
            msg10.byte_order = (it[6] == LITTLE_ENDIAN_BIT);
            
            
            msg.threadPoolToUse = 0;
            msg10.message_type = it[7];
            msg10.message_size = readSize(it[8], it[9], it[10], it[11],
                    msg10.isLittleEndian()) +
                    GIOPMessageHeaderLength;
        } else { 
            Message_1_1 msg11 = (Message_1_1) msg;
            msg11.magic = magic;
            msg11.GIOP_version = new GIOPVersion(it[4], it[5]);
            msg11.flags = (byte) (it[6] & TRAILING_TWO_BIT_BYTE_MASK);
            
            
            
            
            
            
            
            
            
            msg.threadPoolToUse = (it[6] >>> 2) & THREAD_POOL_TO_USE_MASK;
            msg11.message_type = it[7];
            msg11.message_size =
                    readSize(it[8], it[9], it[10], it[11],
                            msg11.isLittleEndian()) + GIOPMessageHeaderLength;

            if (orb.giopSizeDebugFlag) {
                StringBuilder sb = new StringBuilder();
                sb.append(typeToString(msg11.message_type));
                sb.append("(");
                sb.append(msg11.message_size);
                sb.append(" bytes)");
                dprint(sb.toString());
            }
        }

        msg.setEncodingVersion(requestEncodingVersion);

        return msg;
    }

    @SuppressWarnings("deprecation")
    private static RequestMessage createRequest(
            ORB orb, GIOPVersion gv, byte encodingVersion, int request_id,
            boolean response_expected, byte[] object_key, String operation,
            ServiceContexts service_contexts,
            org.omg.CORBA.Principal requesting_principal) {

        if (gv.equals(GIOPVersion.V1_0)) { 
            return new RequestMessage_1_0(orb, service_contexts, request_id,
                    response_expected, object_key,
                    operation, requesting_principal);
        } else if (gv.equals(GIOPVersion.V1_1)) { 
            return new RequestMessage_1_1(orb, service_contexts, request_id,
                    response_expected, new byte[]{0x00, 0x00, 0x00},
                    object_key, operation, requesting_principal);
        } else if (gv.equals(GIOPVersion.V1_2)) { 
            
            
            
            byte response_flags = 0x03;
            if (response_expected) {
                response_flags = 0x03;
            } else {
                response_flags = 0x00;
            }
            







            TargetAddress target = new TargetAddress();
            target.object_key(object_key);
            RequestMessage msg =
                    new RequestMessage_1_2(orb, request_id, response_flags,
                            new byte[]{0x00, 0x00, 0x00},
                            target, operation, service_contexts);
            msg.setEncodingVersion(encodingVersion);
            return msg;
        } else {
            throw wrapper.giopVersionError();
        }
    }

    @SuppressWarnings({"deprecation"})
    public static RequestMessage createRequest(
            ORB orb, GIOPVersion gv, byte encodingVersion, int request_id,
            boolean response_expected, IOR ior,
            short addrDisp, String operation,
            ServiceContexts service_contexts,
            org.omg.CORBA.Principal requesting_principal) {

        RequestMessage requestMessage = null;
        IIOPProfile profile = ior.getProfile();

        if (addrDisp == KeyAddr.value) {
            
            profile = ior.getProfile();
            ObjectKey objKey = profile.getObjectKey();
            byte[] object_key = objKey.getBytes(orb);
            requestMessage =
                    createRequest(orb, gv, encodingVersion, request_id,
                            response_expected, object_key,
                            operation, service_contexts,
                            requesting_principal);
        } else {

            if (!(gv.equals(GIOPVersion.V1_2))) {
                
                
                throw wrapper.giopVersionError();
            }

            
            
            
            byte response_flags = 0x03;
            if (response_expected) {
                response_flags = 0x03;
            } else {
                response_flags = 0x00;
            }

            TargetAddress target = new TargetAddress();
            if (addrDisp == ProfileAddr.value) { 
                profile = ior.getProfile();
                target.profile(profile.getIOPProfile());
            } else if (addrDisp == ReferenceAddr.value) {  
                IORAddressingInfo iorInfo =
                        new IORAddressingInfo(0, 
                                ior.getIOPIOR());
                target.ior(iorInfo);
            } else {
                
                throw wrapper.illegalTargetAddressDisposition();
            }

            requestMessage =
                    new RequestMessage_1_2(orb, request_id, response_flags,
                            new byte[]{0x00, 0x00, 0x00}, target,
                            operation, service_contexts);
            requestMessage.setEncodingVersion(encodingVersion);
        }

        if (gv.supportsIORIIOPProfileComponents()) {
            
            int poolToUse = 0; 
            IIOPProfileTemplate temp =
                    (IIOPProfileTemplate) profile.getTaggedProfileTemplate();
            Iterator iter =
                    temp.iteratorById(ORBConstants.TAG_REQUEST_PARTITIONING_ID);
            if (iter.hasNext()) {
                poolToUse =
                        ((RequestPartitioningComponent) iter.next()).getRequestPartitioningId();
            }

            if (poolToUse < ORBConstants.REQUEST_PARTITIONING_MIN_THREAD_POOL_ID ||
                    poolToUse > ORBConstants.REQUEST_PARTITIONING_MAX_THREAD_POOL_ID) {
                throw wrapper.invalidRequestPartitioningId(poolToUse,
                        ORBConstants.REQUEST_PARTITIONING_MIN_THREAD_POOL_ID,
                        ORBConstants.REQUEST_PARTITIONING_MAX_THREAD_POOL_ID);
            }
            requestMessage.setThreadPoolToUse(poolToUse);
        }

        return requestMessage;
    }

    public static ReplyMessage createReply(
            ORB orb, GIOPVersion gv, byte encodingVersion, int request_id,
            int reply_status, ServiceContexts service_contexts, IOR ior) {

        if (gv.equals(GIOPVersion.V1_0)) { 
            return new ReplyMessage_1_0(orb, service_contexts, request_id,
                    reply_status, ior);
        } else if (gv.equals(GIOPVersion.V1_1)) { 
            return new ReplyMessage_1_1(orb, service_contexts, request_id,
                    reply_status, ior);
        } else if (gv.equals(GIOPVersion.V1_2)) { 
            ReplyMessage msg =
                    new ReplyMessage_1_2(orb, request_id, reply_status,
                            service_contexts, ior);
            msg.setEncodingVersion(encodingVersion);
            return msg;
        } else {
            throw wrapper.giopVersionError();
        }
    }

    public static LocateRequestMessage createLocateRequest(
            ORB orb, GIOPVersion gv, byte encodingVersion,
            int request_id, byte[] object_key) {

        if (gv.equals(GIOPVersion.V1_0)) { 
            return new LocateRequestMessage_1_0(orb, request_id, object_key);
        } else if (gv.equals(GIOPVersion.V1_1)) { 
            return new LocateRequestMessage_1_1(orb, request_id, object_key);
        } else if (gv.equals(GIOPVersion.V1_2)) { 
            TargetAddress target = new TargetAddress();
            target.object_key(object_key);
            LocateRequestMessage msg =
                    new LocateRequestMessage_1_2(orb, request_id, target);
            msg.setEncodingVersion(encodingVersion);
            return msg;
        } else {
            throw wrapper.giopVersionError();
        }
    }

    public static LocateReplyMessage createLocateReply(
            ORB orb, GIOPVersion gv, byte encodingVersion,
            int request_id, int locate_status, IOR ior) {

        if (gv.equals(GIOPVersion.V1_0)) { 
            return new LocateReplyMessage_1_0(orb, request_id,
                    locate_status, ior);
        } else if (gv.equals(GIOPVersion.V1_1)) { 
            return new LocateReplyMessage_1_1(orb, request_id,
                    locate_status, ior);
        } else if (gv.equals(GIOPVersion.V1_2)) { 
            LocateReplyMessage msg =
                    new LocateReplyMessage_1_2(orb, request_id,
                            locate_status, ior);
            msg.setEncodingVersion(encodingVersion);
            return msg;
        } else {
            throw wrapper.giopVersionError();
        }
    }

    public static CancelRequestMessage createCancelRequest(
            GIOPVersion gv, int request_id) {

        if (gv.equals(GIOPVersion.V1_0)) { 
            return new CancelRequestMessage_1_0(request_id);
        } else if (gv.equals(GIOPVersion.V1_1)) { 
            return new CancelRequestMessage_1_1(request_id);
        } else if (gv.equals(GIOPVersion.V1_2)) { 
            return new CancelRequestMessage_1_2(request_id);
        } else {
            throw wrapper.giopVersionError();
        }
    }

    public static Message createCloseConnection(GIOPVersion gv) {
        if (gv.equals(GIOPVersion.V1_0)) { 
            return new Message_1_0(Message.GIOPBigMagic, false,
                    Message.GIOPCloseConnection, 0);
        } else if (gv.equals(GIOPVersion.V1_1)) { 
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_1,
                    FLAG_NO_FRAG_BIG_ENDIAN,
                    Message.GIOPCloseConnection, 0);
        } else if (gv.equals(GIOPVersion.V1_2)) { 
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_2,
                    FLAG_NO_FRAG_BIG_ENDIAN,
                    Message.GIOPCloseConnection, 0);
        } else {
            throw wrapper.giopVersionError();
        }
    }

    public static Message createMessageError(GIOPVersion gv) {
        if (gv.equals(GIOPVersion.V1_0)) { 
            return new Message_1_0(Message.GIOPBigMagic, false,
                    Message.GIOPMessageError, 0);
        } else if (gv.equals(GIOPVersion.V1_1)) { 
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_1,
                    FLAG_NO_FRAG_BIG_ENDIAN,
                    Message.GIOPMessageError, 0);
        } else if (gv.equals(GIOPVersion.V1_2)) { 
            return new Message_1_1(Message.GIOPBigMagic, GIOPVersion.V1_2,
                    FLAG_NO_FRAG_BIG_ENDIAN,
                    Message.GIOPMessageError, 0);
        } else {
            throw wrapper.giopVersionError();
        }
    }

    
    public static void setFlag(ByteBuffer byteBuffer, int flag) {
        byte b = byteBuffer.get(6);
        b |= flag;
        byteBuffer.put(6, b);
    }

    private static void AreFragmentsAllowed(byte major, byte minor, byte flag,
                                            byte msgType) {

        if ((major == 0x01) && (minor == 0x00)) { 
            if (msgType == GIOPFragment) {
                throw wrapper.fragmentationDisallowed();
            }
        }

        if ((flag & MORE_FRAGMENTS_BIT) == MORE_FRAGMENTS_BIT) {
            switch (msgType) {
                case GIOPCancelRequest:
                case GIOPCloseConnection:
                case GIOPMessageError:
                    throw wrapper.fragmentationDisallowed();
                case GIOPLocateRequest:
                case GIOPLocateReply:
                    if ((major == 0x01) && (minor == 0x01)) { 
                        throw wrapper.fragmentationDisallowed();
                    }
                    break;
            }
        }
    }

    
    static ObjectKeyCacheEntry extractObjectKeyCacheEntry(TargetAddress target, ORB orb) {

        short orbTargetAddrPref = orb.getORBData().getGIOPTargetAddressPreference();
        short reqAddrDisp = target.discriminator();

        switch (orbTargetAddrPref) {
            case ORBConstants.ADDR_DISP_OBJKEY:
                if (reqAddrDisp != KeyAddr.value) {
                    throw new AddressingDispositionException(KeyAddr.value);
                }
                break;
            case ORBConstants.ADDR_DISP_PROFILE:
                if (reqAddrDisp != ProfileAddr.value) {
                    throw new AddressingDispositionException(ProfileAddr.value);
                }
                break;
            case ORBConstants.ADDR_DISP_IOR:
                if (reqAddrDisp != ReferenceAddr.value) {
                    throw new AddressingDispositionException(ReferenceAddr.value);
                }
                break;
            case ORBConstants.ADDR_DISP_HANDLE_ALL:
                break;
            default:
                throw wrapper.orbTargetAddrPreferenceInExtractObjectkeyInvalid();
        }

        try {
            switch (reqAddrDisp) {
                case KeyAddr.value:
                    byte[] objKey = target.object_key();
                    if (objKey != null) { 
                        return orb.extractObjectKeyCacheEntry(objKey);
                    }
                    break;
                case ProfileAddr.value:
                    TaggedProfile profile = target.profile();
                    if (profile != null) { 
                        IIOPProfile iiopProfile = IIOPFactories.makeIIOPProfile(orb, profile);
                        ObjectKey objectKey = iiopProfile.getObjectKey();
                        return new ObjectKeyCacheEntryNoObjectAdapterImpl(objectKey);
                    }
                    break;
                case ReferenceAddr.value:
                    IORAddressingInfo iorInfo = target.ior();
                    if (iorInfo != null) { 
                        profile = iorInfo.ior.profiles[iorInfo.selected_profile_index];
                        IIOPProfile iiopProfile = IIOPFactories.makeIIOPProfile(orb, profile);
                        ObjectKey objectKey = iiopProfile.getObjectKey();
                        return new ObjectKeyCacheEntryNoObjectAdapterImpl(objectKey);
                    }
                    break;
                default:
                    
                    
                    
                    
                    break;
            }
        } catch (Exception e) {
            throw wrapper.invalidObjectKey(e);
        }

        
        throw wrapper.invalidObjectKey();
    }

    private static int readSize(byte b1, byte b2, byte b3, byte b4,
                                boolean littleEndian) {

        int a1, a2, a3, a4;

        if (!littleEndian) {
            a1 = (b1 << 24) & 0xFF000000;
            a2 = (b2 << 16) & 0x00FF0000;
            a3 = (b3 << 8) & 0x0000FF00;
            a4 = (b4) & 0x000000FF;
        } else {
            a1 = (b4 << 24) & 0xFF000000;
            a2 = (b3 << 16) & 0x00FF0000;
            a3 = (b2 << 8) & 0x0000FF00;
            a4 = (b1) & 0x000000FF;
        }

        return (a1 | a2 | a3 | a4);
    }

    static void nullCheck(Object obj) {
        if (obj == null) {
            throw wrapper.nullNotAllowed();
        }
    }

    static SystemException getSystemException(
            String exClassName, int minorCode, CompletionStatus completionStatus,
            String message, ORBUtilSystemException wrapper) {
        SystemException sysEx;

        try {
            Class<?> clazz = ORBClassLoader.loadClass(exClassName);
            if (message == null) {
                sysEx = (SystemException) clazz.newInstance();
            } else {
                Class<?>[] types = {String.class};
                Constructor<?> constructor = clazz.getConstructor(types);
                Object[] args = {message};
                sysEx = (SystemException) constructor.newInstance(args);
            }
        } catch (Exception someEx) {
            throw wrapper.badSystemExceptionInReply(someEx);
        }

        sysEx.minor = minorCode;
        sysEx.completed = completionStatus;

        return sysEx;
    }

    public void callback(MessageHandler handler)
            throws java.io.IOException {
        handler.handleInput(this);
    }

    public int getThreadPoolToUse() {
        return threadPoolToUse;
    }

    public byte getEncodingVersion() {
        return this.encodingVersion;
    }

    public void setEncodingVersion(byte version) {
        this.encodingVersion = version;
    }

    
    public RequestId getCorbaRequestId() {
        return RequestIdImpl.UNKNOWN_CORBA_REQUEST_ID;
    }

    
    public boolean supportsFragments() {
        return false;
    }

    
    public static RequestId getRequestIdFromMessageBytes(Message message, ByteBuffer byteBuffer) {
        if (!(message instanceof Message_1_2)) {
            return RequestIdImpl.UNKNOWN_CORBA_REQUEST_ID; 
        } else {
            Message_1_2 message_1_2 = (Message_1_2) message;
            message_1_2.unmarshalRequestID(byteBuffer);
            return new RequestIdImpl(message_1_2.request_id);
        }
    }

    private static void dprint(String msg) {
        ORBUtility.dprint("MessageBase", msg);
    }
}
