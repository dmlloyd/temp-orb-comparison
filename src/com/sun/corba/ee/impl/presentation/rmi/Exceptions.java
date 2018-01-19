


package xxxx;



@ExceptionWrapper( idPrefix="ORBPRES" )
public interface Exceptions {

    Exceptions self = WrapperGenerator.makeWrapper( Exceptions.class,
        StandardLogger.self ) ;

    int EXCEPTIONS_PER_CLASS = 100 ;


    int JSFI_START = 0 ;

    @Message( "No stub could be created" )
    @Log( level=LogLevel.FINE, id = JSFI_START + 1 )
    void noStub(@Chain Exception exc);

    @Message( "Could not connect stub" )
    @Log( level=LogLevel.FINE, id = JSFI_START + 2 )
    void couldNotConnect(@Chain Exception exc);

    @Message( "Could not get ORB from naming context" )
    @Log( level=LogLevel.FINE, id = JSFI_START + 2 )
    void couldNotGetORB(@Chain Exception exc, Context nc );


    int DSI_START = JSFI_START + EXCEPTIONS_PER_CLASS ;

    @Message( "ClassNotFound exception in readResolve on class {0}")
    @Log( level=LogLevel.FINE, id = DSI_START + 1 )
    void readResolveClassNotFound(@Chain ClassNotFoundException exc, String cname);
}
