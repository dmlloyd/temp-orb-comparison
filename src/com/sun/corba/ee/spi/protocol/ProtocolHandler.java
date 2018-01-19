


package xxxx;





public abstract interface ProtocolHandler
{
    public void handleRequest(RequestMessage header, 
                              MessageMediator messageMediator);

    public void handleRequest(LocateRequestMessage header, 
                              MessageMediator messageMediator);

    public MessageMediator createResponse(
        MessageMediator messageMediator,
        ServiceContexts svc);
    public MessageMediator createUserExceptionResponse(
        MessageMediator messageMediator,
        ServiceContexts svc);
    public MessageMediator createUnknownExceptionResponse(
        MessageMediator messageMediator,
        UnknownException ex);
    public MessageMediator createSystemExceptionResponse(
        MessageMediator messageMediator,
        SystemException ex,
        ServiceContexts svc);
    public MessageMediator createLocationForward(
        MessageMediator messageMediator,
        IOR ior, 
        ServiceContexts svc);

    public void handleThrowableDuringServerDispatch( 
        MessageMediator request,
        Throwable exception,
        CompletionStatus completionStatus);

    public boolean handleRequest(MessageMediator messageMediator);

}


