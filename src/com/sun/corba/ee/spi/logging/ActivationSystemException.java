


package xxxx;




@ExceptionWrapper( idPrefix="IOP" )
@ORBException( omgException=false, group=CorbaExtension.ActivationGroup )
public interface ActivationSystemException {
    ActivationSystemException self = WrapperGenerator.makeWrapper( 
        ActivationSystemException.class, CorbaExtension.self ) ;
    
    @Log( level=LogLevel.WARNING, id=1 )
    @Message( "Cannot read repository datastore" )
    INITIALIZE cannotReadRepositoryDb( @Chain Exception exc ) ;
    
    @Log( level=LogLevel.WARNING, id=2 )
    @Message( "Cannot add initial naming" )
    INITIALIZE cannotAddInitialNaming(  ) ;
    
    @Log( level=LogLevel.WARNING, id=1 )
    @Message( "Cannot write repository datastore" )
    INTERNAL cannotWriteRepositoryDb( @Chain Exception exc ) ;
    
    @Log( level=LogLevel.WARNING, id=3 )
    @Message( "Server not expected to register" )
    INTERNAL serverNotExpectedToRegister(  ) ;
    
    @Log( level=LogLevel.WARNING, id=4 )
    @Message( "Unable to start server process" )
    INTERNAL unableToStartProcess(  ) ;
    
    @Log( level=LogLevel.WARNING, id=6 )
    @Message( "Server is not running" )
    INTERNAL serverNotRunning(  ) ;
    
    @Log( level=LogLevel.WARNING, id=1 )
    @Message( "Error in BadServerIdHandler" )
    OBJECT_NOT_EXIST errorInBadServerIdHandler( @Chain Exception exc  ) ;
}
