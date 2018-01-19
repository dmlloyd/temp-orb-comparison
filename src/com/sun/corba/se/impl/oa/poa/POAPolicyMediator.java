

package com.sun.corba.se.impl.oa.poa ;




public interface POAPolicyMediator {
    
    Policies getPolicies() ;

    
    int getScid() ;

    
    int getServerId() ;

    
    java.lang.Object getInvocationServant( byte[] id,
        String operation ) throws ForwardRequest ;

    
    void returnServant() ;

    
    void etherealizeAll() ;

    
    void clearAOM() ;

    
    ServantManager getServantManager() throws WrongPolicy ;

    
    void setServantManager( ServantManager servantManager ) throws WrongPolicy ;

    
    Servant getDefaultServant() throws NoServant, WrongPolicy ;

    
    void setDefaultServant( Servant servant ) throws WrongPolicy ;

    void activateObject( byte[] id, Servant servant )
        throws ObjectAlreadyActive, ServantAlreadyActive, WrongPolicy ;

    
    Servant deactivateObject( byte[] id ) throws ObjectNotActive, WrongPolicy ;

    
    byte[] newSystemId() throws WrongPolicy ;

    byte[] servantToId( Servant servant ) throws ServantNotActive, WrongPolicy ;

    Servant idToServant( byte[] id ) throws ObjectNotActive, WrongPolicy ;
}
