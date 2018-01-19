


package com.sun.corba.ee.impl.transport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock ;
import java.util.concurrent.locks.ReadWriteLock ;

import com.sun.corba.ee.spi.ior.IOR ;
import com.sun.corba.ee.spi.ior.iiop.IIOPAddress ;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfile ;
import com.sun.corba.ee.spi.ior.iiop.IIOPProfileTemplate ;
import com.sun.corba.ee.spi.ior.iiop.LoadBalancingComponent ;
import com.sun.corba.ee.spi.ior.TaggedProfile ;
import com.sun.corba.ee.spi.ior.TaggedProfileTemplate ;
import com.sun.corba.ee.spi.ior.TaggedComponent ;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.ee.spi.transport.ContactInfoList ;
import com.sun.corba.ee.spi.transport.SocketInfo;
import com.sun.corba.ee.spi.transport.ContactInfo;

import com.sun.corba.ee.spi.misc.ORBConstants;
import com.sun.corba.ee.impl.protocol.NotLocalLocalCRDImpl;
import com.sun.corba.ee.spi.trace.IsLocal;
import com.sun.corba.ee.spi.trace.Transport;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException ;
import org.glassfish.pfl.basic.func.UnaryPredicate;
import org.glassfish.pfl.tf.spi.annotation.InfoMethod;


@Transport
@IsLocal
public class ContactInfoListImpl implements ContactInfoList {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    protected ORB orb;
    private ReadWriteLock lcrdLock = new ReentrantReadWriteLock() ;
    protected LocalClientRequestDispatcher localClientRequestDispatcher;
    protected IOR targetIOR;
    protected IOR effectiveTargetIOR;
    protected List<ContactInfo> effectiveTargetIORContactInfoList;
    protected ContactInfo primaryContactInfo;
    private boolean usePerRequestLoadBalancing = false ;

    private int startCount = 0 ;

    private UnaryPredicate<ContactInfo> testPred =
        new UnaryPredicate<ContactInfo>() {
            public boolean evaluate( ContactInfo arg ) {
                return !arg.getType().equals( SocketInfo.IIOP_CLEAR_TEXT ) ;
            }
        } ;

    private <T> List<T> filter( List<T> arg, UnaryPredicate<T> pred ) {
        List<T> result = new ArrayList<T>() ;
        for (T elem : arg ) {
            if (pred.evaluate( elem )) {
                result.add( elem ) ;
            }
        }

        return result ;
    }

    private static ThreadLocal<Boolean> skipRotate = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false ;
        }
    } ;

    
    
    
    
    public static void setSkipRotate() {
        skipRotate.set( true ) ;
    }

    @InfoMethod
    private void display( String msg, int value ) { }

    @InfoMethod
    private void display( String msg, Object value ) { }

    
    
    
    
    @Transport
    private synchronized List<ContactInfo> rotate( List<ContactInfo> arg ) {
        if (skipRotate.get()) {
            skipRotate.set( false ) ;
            return arg ;
        }

        if (usePerRequestLoadBalancing) {
            display( "startCount", startCount ) ;
            LinkedList<ContactInfo> tempList = null ;

            
            
            
            
            tempList = new LinkedList<ContactInfo>( filter( arg, testPred ) ) ;

            
            

            if (startCount >= tempList.size()) {
                startCount = 0 ;
            }

            for (int ctr=0; ctr<startCount; ctr++) {
                ContactInfo element = tempList.removeLast() ;
                tempList.addFirst( element ) ;
            }

            startCount++ ;

            return tempList ;
        } else {
            return arg ;
        }
    }

    
    public ContactInfoListImpl(ORB orb)
    {
        this.orb = orb;
    }

    public ContactInfoListImpl(ORB orb, IOR targetIOR)
    {
        this(orb);
        setTargetIOR(targetIOR);
    }
    
    public synchronized Iterator<ContactInfo> iterator()
    {
        createContactInfoList();
        Iterator<ContactInfo> result = new ContactInfoListIteratorImpl(
            orb, this, primaryContactInfo, 
            rotate( effectiveTargetIORContactInfoList ),
            usePerRequestLoadBalancing );

        
        



        return result ;
    }

    
    
    
    

    public synchronized void setTargetIOR(IOR targetIOR)
    {
        this.targetIOR = targetIOR;
        setEffectiveTargetIOR(targetIOR);
    }

    public synchronized IOR getTargetIOR()
    {
        return targetIOR;
    }
    
    private IIOPAddress getPrimaryAddress( IOR ior ) {
        if (ior != null) {
            for (TaggedProfile tprof : ior) {
                TaggedProfileTemplate tpt = tprof.getTaggedProfileTemplate() ;
                if (tpt instanceof IIOPProfileTemplate) {
                    IIOPProfileTemplate ipt = (IIOPProfileTemplate)tpt ;
                    return ipt.getPrimaryAddress() ;
                }
            }
        }

        return null ;
    }

    @InfoMethod
    private void changingEffectiveAddress( IIOPAddress oldAddr, IIOPAddress newAddr ) { }

    @Transport
    public synchronized void setEffectiveTargetIOR(IOR newIOR)
    {
        if (targetIOR != null) {
            final String oldTypeId = targetIOR.getTypeId() ;
            final String newTypeId = newIOR.getTypeId() ;
            if (!oldTypeId.isEmpty() && !oldTypeId.equals( newTypeId )) {
                
                
                wrapper.changedTypeIdOnSetEffectiveTargetIOR( oldTypeId,
                    newTypeId ) ;
                
                
                return ;
            }
        }

        final IIOPAddress oldAddress = getPrimaryAddress( this.effectiveTargetIOR ) ;
        final IIOPAddress newAddress = getPrimaryAddress( newIOR ) ;
        if ((oldAddress != null) && !oldAddress.equals( newAddress )) {
            changingEffectiveAddress( oldAddress, newAddress ) ;
        }

        this.effectiveTargetIOR = newIOR;

        effectiveTargetIORContactInfoList = null;
        if (primaryContactInfo != null &&
            orb.getORBData().getIIOPPrimaryToContactInfo() != null)
        {
            orb.getORBData().getIIOPPrimaryToContactInfo()
                .reset(primaryContactInfo);
        }
        primaryContactInfo = null;
        setLocalSubcontract();

        
        IIOPProfile prof = newIOR.getProfile() ;
        TaggedProfileTemplate temp = prof.getTaggedProfileTemplate() ;
        Iterator<TaggedComponent> lbcomps = 
            temp.iteratorById( ORBConstants.TAG_LOAD_BALANCING_ID ) ;
        if (lbcomps.hasNext()) {
            LoadBalancingComponent lbcomp = null ;
            lbcomp = (LoadBalancingComponent)(lbcomps.next()) ;
            usePerRequestLoadBalancing = 
                lbcomp.getLoadBalancingValue() == ORBConstants.PER_REQUEST_LOAD_BALANCING ; 
        }
    }

    public synchronized IOR getEffectiveTargetIOR()
    {
        return effectiveTargetIOR;
    }

    public synchronized LocalClientRequestDispatcher getLocalClientRequestDispatcher()
    {
        lcrdLock.readLock().lock() ;
        try {
            return localClientRequestDispatcher;
        } finally {
            lcrdLock.readLock().unlock() ;
        }
    }

    
    
    
    

    

    
    
    
    

    @Override
    public synchronized int hashCode()
    {
        return targetIOR.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContactInfoListImpl other = (ContactInfoListImpl) obj;
        if (this.targetIOR != other.targetIOR &&
            (this.targetIOR == null || !this.targetIOR.equals(other.targetIOR))) {
            return false;
        }
        return true;
    }

    
    
    
    

    @Transport
    private void createContactInfoList() {
        IIOPProfile iiopProfile = effectiveTargetIOR.getProfile();
        final boolean isLocal = iiopProfile.isLocal() ;

        if (effectiveTargetIORContactInfoList == null) {
            effectiveTargetIORContactInfoList = 
                new ArrayList<ContactInfo>();

            String hostname = 
                ((IIOPProfileTemplate)iiopProfile.getTaggedProfileTemplate())
                    .getPrimaryAddress().getHost().toLowerCase();
            int    port     = 
                ((IIOPProfileTemplate)iiopProfile.getTaggedProfileTemplate())
                    .getPrimaryAddress().getPort();
            
            primaryContactInfo = 
                createContactInfo(SocketInfo.IIOP_CLEAR_TEXT, hostname, port);

            if (isLocal) {
                
                
                
                
                
                ContactInfo contactInfo = new SharedCDRContactInfoImpl(
                    orb, this, effectiveTargetIOR, 
                    orb.getORBData().getGIOPAddressDisposition());
                effectiveTargetIORContactInfoList.add(contactInfo);
            } else {
                addRemoteContactInfos(effectiveTargetIOR,
                                      effectiveTargetIORContactInfoList);
            }
            display( "First time for iiopProfile", iiopProfile ) ;
        } else {
            if (!isLocal) {
                display( "Subsequent time for iiopProfile", iiopProfile ) ;
                
                
                addRemoteContactInfos(effectiveTargetIOR,
                                      effectiveTargetIORContactInfoList);
            } else {
                display( "Subsequent time for (colocated) iiopProfile",
                    iiopProfile ) ;
            }
        }

        display( "effective list", effectiveTargetIORContactInfoList ) ;
    }

    @Transport
    private void addRemoteContactInfos( IOR  effectiveTargetIOR,
        List<ContactInfo> effectiveTargetIORContactInfoList) {

        ContactInfo contactInfo;
        List<? extends SocketInfo> socketInfos = orb.getORBData()
            .getIORToSocketInfo().getSocketInfo(
                effectiveTargetIOR,
                
                effectiveTargetIORContactInfoList);

        if (socketInfos == effectiveTargetIORContactInfoList) {
            display( "socketInfos", socketInfos ) ;
            return;
        }

        for (SocketInfo socketInfo : socketInfos) {
            String type = socketInfo.getType();
            String host = socketInfo.getHost().toLowerCase();
            int    port = socketInfo.getPort();
            contactInfo = createContactInfo(type, host, port);
            effectiveTargetIORContactInfoList.add(contactInfo);
        }
    }

    protected ContactInfo createContactInfo(String type, String hostname,
        int port) {

        return new ContactInfoImpl(
            orb, this, 
            
            effectiveTargetIOR,
            orb.getORBData().getGIOPAddressDisposition(),
            type, hostname, port);
    }

    
    @IsLocal
    protected void setLocalSubcontract() {
        lcrdLock.writeLock().lock() ;
        try {
            if (!effectiveTargetIOR.getProfile().isLocal()) {
                localClientRequestDispatcher = new NotLocalLocalCRDImpl();
                return;
            }

            
            int scid = effectiveTargetIOR.getProfile().getObjectKeyTemplate().
                getSubcontractId() ;
            LocalClientRequestDispatcherFactory lcsf = 
                orb.getRequestDispatcherRegistry().
                    getLocalClientRequestDispatcherFactory( scid ) ;
            if (lcsf != null) {
                localClientRequestDispatcher = lcsf.create( scid, effectiveTargetIOR ) ;
            }
        } finally {
            lcrdLock.writeLock().unlock() ;
        }
    }

    
    public ContactInfo getPrimaryContactInfo() {
        return primaryContactInfo;
    }
}


