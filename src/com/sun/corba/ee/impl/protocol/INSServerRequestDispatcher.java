



package com.sun.corba.ee.impl.protocol;



 
public class INSServerRequestDispatcher 
    implements ServerRequestDispatcher
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private ORB orb = null;

    public INSServerRequestDispatcher( ORB orb ) {
        this.orb = orb;
    }

    
    public IOR locate(ObjectKey okey) { 
        
        
        String insKey = new String( okey.getBytes(orb) );
        return getINSReference( insKey );
    }

    public void dispatch(MessageMediator request)
    {
        
        
        String insKey = new String( 
            request.getObjectKeyCacheEntry().getObjectKey().getBytes(orb) );
        request.getProtocolHandler()
            .createLocationForward(request, getINSReference( insKey ), null);
        return;
    }

    
    private IOR getINSReference( String insKey ) {
        IOR entry = orb.getIOR( orb.getLocalResolver().resolve( insKey ), false ) ;
        if( entry != null ) {
            
            
            return entry;
        }

        throw wrapper.servantNotFound() ;
    }
}
