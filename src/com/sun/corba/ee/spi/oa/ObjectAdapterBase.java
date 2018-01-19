

package com.sun.corba.ee.spi.oa ;





abstract public class ObjectAdapterBase extends org.omg.CORBA.LocalObject 
    implements ObjectAdapter
{
    protected static final POASystemException wrapper =
        POASystemException.self ;

    private ORB orb;

    
    
    private IORTemplate iortemp;
    private byte[] adapterId ;
    private ObjectReferenceTemplate adapterTemplate ;
    private ObjectReferenceFactory currentFactory ;
    private boolean isNameService = false ;
   
    public ObjectAdapterBase( ORB orb ) {
        this.orb = orb ;
    }

    public final POASystemException wrapper() {
        return wrapper ;
    }

    
    final public void initializeTemplate( ObjectKeyTemplate oktemp,
        boolean notifyORB, Policies policies, String codebase,
        String objectAdapterManagerId, ObjectAdapterId objectAdapterId)
    {
        adapterId = oktemp.getAdapterId() ;

        iortemp = IORFactories.makeIORTemplate(oktemp) ;

        
        
        orb.getCorbaTransportManager().addToIORTemplate(
            iortemp, policies,
            codebase, objectAdapterManagerId, objectAdapterId);

        adapterTemplate = IORFactories.makeObjectReferenceTemplate( orb, 
            iortemp ) ;
        currentFactory = adapterTemplate ;

        if (notifyORB) {
            PIHandler pih = orb.getPIHandler() ;
            if (pih != null) {
                pih.objectAdapterCreated(this);
            }
        }

        iortemp.makeImmutable() ;
    }

    final public org.omg.CORBA.Object makeObject( String repId, byte[] oid )
    {
        if (repId == null) {
            throw wrapper.nullRepositoryId();
        }
        return currentFactory.make_object( repId, oid ) ;
    }

    final public byte[] getAdapterId() 
    {
        return adapterId ;
    }

    final public ORB getORB() 
    {
        return orb ;
    }

    abstract public Policy getEffectivePolicy( int type ) ;

    final public IORTemplate getIORTemplate() 
    {
        return iortemp ;
    }

    abstract public int getManagerId() ;

    abstract public short getState() ; 

    @ManagedAttribute( id="State" )
    @Description( "The current Adapter state")
    private String getDisplayState( ) {
        final short state = getState() ;
        switch (state) {
            case HOLDING.value : return "HOLDING" ;
            case ACTIVE.value : return "ACTIVE" ;
            case DISCARDING.value : return "DISCARDING" ;
            case INACTIVE.value : return "INACTIVE" ;
            case NON_EXISTENT.value : return "NON_EXISTENT" ;
            default : return "<INVALID>" ;
        }
    }

    final public ObjectReferenceTemplate getAdapterTemplate()
    {
        return adapterTemplate ;
    }

    final public ObjectReferenceFactory getCurrentFactory()
    {
        return currentFactory ;
    }

    final public void setCurrentFactory( ObjectReferenceFactory factory )
    {
        currentFactory = factory ;
    }

    abstract public org.omg.CORBA.Object getLocalServant( byte[] objectId ) ;

    abstract public void getInvocationServant( OAInvocationInfo info ) ;

    abstract public void returnServant() ;

    abstract public void enter() throws OADestroyed ;

    abstract public void exit() ;

    abstract protected ObjectCopierFactory getObjectCopierFactory() ;

    
    
    public OAInvocationInfo makeInvocationInfo( byte[] objectId )
    {
        OAInvocationInfo info = new OAInvocationInfo( this, objectId ) ;
        info.setCopierFactory( getObjectCopierFactory() ) ;
        return info ;
    }

    abstract public String[] getInterfaces( Object servant, byte[] objectId ) ;

    public boolean isNameService() {
        return isNameService ;
    }

    public void setNameService( boolean flag ) {
        isNameService = flag ;
    }
} 
