

package com.sun.corba.ee.impl.oa.toa ;










@ManagedObject
@Description( "The Factory for the TOA (transient object adapter)")
@AMXMetadata( isSingleton=true )
public class TOAFactory implements ObjectAdapterFactory 
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private ORB orb ;

    private TOAImpl toa ;
    private Map<String,TOAImpl> codebaseToTOA ;
    private TransientObjectManager tom ; 

    @ManagedAttribute
    @Description( "The default TOA used only for dispatch, not objref creation")
    private TOAImpl getDefaultTOA() {
        return toa ;
    }

    @ManagedAttribute
    @Description( "The map from Codebase to TOA")
    private synchronized Map<String,TOAImpl> getCodebaseMap() {
        return new HashMap<String,TOAImpl>( codebaseToTOA ) ;
    }

    public ObjectAdapter find ( ObjectAdapterId oaid ) 
    {
        if (oaid.equals( ObjectKeyTemplateBase.JIDL_OAID )  ) {
            return getTOA();
        } else {
            throw wrapper.badToaOaid();
        }
    }

    public void init( ORB orb )
    {
        this.orb = orb ;
        tom = new TransientObjectManager( orb ) ;
        codebaseToTOA = new HashMap<String,TOAImpl>() ;
        orb.mom().registerAtRoot( this ) ;
    }

    public void shutdown( boolean waitForCompletion )
    {
        if (Util.getInstance() != null) {
            Util.getInstance().unregisterTargetsForORB(orb);
        }
    }

    public synchronized TOA getTOA( String codebase )
    {
        TOAImpl myToa = codebaseToTOA.get( codebase ) ;
        if (myToa == null) {
            myToa = new TOAImpl( orb, tom, codebase ) ;

            codebaseToTOA.put( codebase, myToa ) ;
        }

        return myToa ;
    }

    public synchronized TOA getTOA() 
    {
        if (toa == null) {
            
            
            
            toa = new TOAImpl( orb, tom, null ) ;
        }

        return toa ;
    }

    public ORB getORB() 
    {
        return orb ;
    }
} ;

