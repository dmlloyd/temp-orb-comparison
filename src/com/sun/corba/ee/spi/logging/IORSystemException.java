


package xxxx;




@ExceptionWrapper( idPrefix="IOP" )
@ORBException( omgException=false, group=CorbaExtension.IORGroup )
public interface IORSystemException {
    IORSystemException self = WrapperGenerator.makeWrapper( 
        IORSystemException.class, CorbaExtension.self ) ;
    
    @Log( level=LogLevel.WARNING, id=1 )
    @Message( "ObjectReferenceTemplate is not initialized" )
    INTERNAL ortNotInitialized(  ) ;
    
    @Log( level=LogLevel.WARNING, id=2 )
    @Message( "Null POA" )
    INTERNAL nullPoa(  ) ;
    
    @Log( level=LogLevel.WARNING, id=3 )
    @Message( "Bad magic number {0} in ObjectKeyTemplate" )
    INTERNAL badMagic( int magic ) ;
    
    @Log( level=LogLevel.WARNING, id=4 )
    @Message( "Error while stringifying an object reference" )
    INTERNAL stringifyWriteError( @Chain IOException exc ) ;
    
    @Log( level=LogLevel.WARNING, id=5 )
    @Message( "Could not find a TaggedProfileTemplateFactory for id {0}" )
    INTERNAL taggedProfileTemplateFactoryNotFound( int arg0 ) ;
    
    @Log( level=LogLevel.WARNING, id=6 )
    @Message( "Found a JDK 1.3.1 patch level indicator with value {0} "
        + "less than JDK 1.3.1_01 value of 1" )
    INTERNAL invalidJdk131PatchLevel( int arg0 ) ;
    
    @Log( level=LogLevel.FINE, id=7 )
    @Message( "Exception occurred while looking for ObjectAdapter {0} "
        + "in IIOPProfileImpl.getServant" )
    INTERNAL getLocalServantFailure( @Chain Exception exc, 
        ObjectAdapterId oaid ) ;

    @Log( level=LogLevel.WARNING, id=8 )
    @Message( "Exception occurred while closing an IO stream object" )
    INTERNAL ioexceptionDuringStreamClose( @Chain Exception exc ) ;
    
    @Log( level=LogLevel.WARNING, id=1 )
    @Message( "Adapter ID not available" )
    BAD_OPERATION adapterIdNotAvailable(  ) ;
    
    @Log( level=LogLevel.WARNING, id=2 )
    @Message( "Server ID not available" )
    BAD_OPERATION serverIdNotAvailable(  ) ;
    
    @Log( level=LogLevel.WARNING, id=3 )
    @Message( "ORB ID not available" )
    BAD_OPERATION orbIdNotAvailable(  ) ;
    
    @Log( level=LogLevel.WARNING, id=4 )
    @Message( "Object adapter ID not available" )
    BAD_OPERATION objectAdapterIdNotAvailable(  ) ;
    
    @Log( level=LogLevel.WARNING, id=1 )
    @Message( "Profiles in IOR do not all have the same Object ID, " +
        "so conversion to IORTemplateList is impossible" )
    BAD_PARAM badOidInIorTemplateList(  ) ;
    
    @Log( level=LogLevel.WARNING, id=2 )
    @Message( "Error in reading IIOP TaggedProfile" )
    BAD_PARAM invalidTaggedProfile(  ) ;
    
    @Log( level=LogLevel.WARNING, id=3 )
    @Message( "Attempt to create IIOPAddress with port {0}, which is out of range" )
    BAD_PARAM badIiopAddressPort( int arg0 ) ;
    
    @Log( level=LogLevel.WARNING, id=1 )
    @Message( "IOR must have at least one IIOP profile" )
    INV_OBJREF iorMustHaveIiopProfile(  ) ;

    @Log( level=LogLevel.FINE, id=1 )
    @Message( "MARSHAL error while attempting to create an ObjectKeyTemplate "
        + "from an input stream")
    UNKNOWN createMarshalError(@Chain MARSHAL mexc);
}
