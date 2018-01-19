


package xxxx;





@ManagedObject
@Description( "The Transient Object Adapter")
public class TOAImpl extends ObjectAdapterBase implements TOA 
{
    private static AtomicLong currentId = new AtomicLong( 0 );

    private TransientObjectManager servants ;
    private long id ;
    private String codebase ;

    @NameValue
    private long getId() {
        return id ;
    }

    @ManagedAttribute
    @Description( "The codebase used to create this TOA")
    private String getCodebase() {
        return codebase ;
    }

    @ManagedAttribute
    @Description( "The TransientObjectManager")
    private TransientObjectManager getTransientObjectManager() {
        return servants ;
    }

    public TOAImpl( ORB orb, TransientObjectManager tom, String codebase ) 
    {
        super( orb ) ;
        servants = tom ;
        this.codebase = codebase ;
        id = currentId.getAndIncrement() ;

        
        int serverid = (getORB()).getTransientServerId();
        int scid = ORBConstants.TOA_SCID ;

        ObjectKeyTemplate oktemp = new JIDLObjectKeyTemplate( orb, scid, serverid ) ;

        
        Policies policies = Policies.defaultPolicies;

        
        initializeTemplate( oktemp, true,
                            policies, 
                            codebase,
                            null, 
                            oktemp.getObjectAdapterId()
                            ) ;
    }

    

    public ObjectCopierFactory getObjectCopierFactory()
    {
        CopierManager cm = getORB().getCopierManager() ;
        return cm.getDefaultObjectCopierFactory() ;
    }

    public org.omg.CORBA.Object getLocalServant( byte[] objectId ) 
    {
        return (org.omg.CORBA.Object)(servants.lookupServant( objectId ) ) ;
    }

    
    public void getInvocationServant( OAInvocationInfo info ) 
    {
        java.lang.Object servant = servants.lookupServant( info.id() ) ;
        if (servant == null) {
            servant =
                new NullServantImpl(wrapper.nullServant());
        }
        info.setServant( servant ) ;
    }

    public void returnServant()
    {
        
    }

    
    public String[] getInterfaces( Object servant, byte[] objectId ) 
    {
        return StubAdapter.getTypeIds( servant ) ;
    }

    public Policy getEffectivePolicy( int type ) 
    {
        return null ;
    }

    public int getManagerId() 
    {
        return -1 ;
    }

    public short getState() 
    {
        return ACTIVE.value ;
    }

    public void enter() throws OADestroyed
    {
    }

    public void exit() 
    {
    }
 
    

    public void connect( org.omg.CORBA.Object objref) 
    {
        
        
        byte[] key = servants.storeServant(objref, null);

        
        String id = StubAdapter.getTypeIds( objref )[0] ;

        
        ObjectReferenceFactory orf = getCurrentFactory() ;
        org.omg.CORBA.Object obj = orf.make_object( id, key ) ;

        
        org.omg.CORBA.portable.Delegate delegate = StubAdapter.getDelegate( 
            obj ) ;
        ContactInfoList ccil = ((ClientDelegate) delegate).getContactInfoList() ;
        LocalClientRequestDispatcher lcs = 
            ccil.getLocalClientRequestDispatcher() ;

        if (lcs instanceof JIDLLocalCRDImpl) {
            JIDLLocalCRDImpl jlcs = (JIDLLocalCRDImpl)lcs ;
            jlcs.setServant( objref ) ;
        } else {        
            throw new RuntimeException( 
                "TOAImpl.connect can not be called on " + lcs ) ;
        }

        StubAdapter.setDelegate( objref, delegate ) ;
    }

    public void disconnect( org.omg.CORBA.Object objref ) 
    {
        
        org.omg.CORBA.portable.Delegate del = StubAdapter.getDelegate( 
            objref ) ; 
        ContactInfoList ccil = ((ClientDelegate) del).getContactInfoList() ;
        LocalClientRequestDispatcher lcs = 
            ccil.getLocalClientRequestDispatcher() ;

        if (lcs instanceof JIDLLocalCRDImpl) {
            JIDLLocalCRDImpl jlcs = (JIDLLocalCRDImpl)lcs ;
            byte[] oid = jlcs.getObjectId() ;
            servants.deleteServant(oid);
            jlcs.unexport() ;
        } else {        
            throw new RuntimeException( 
                "TOAImpl.disconnect can not be called on " + lcs ) ;
        }
    }
} 
