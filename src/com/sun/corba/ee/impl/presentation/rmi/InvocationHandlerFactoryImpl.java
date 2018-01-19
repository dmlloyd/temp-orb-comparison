


package xxxx;





public class InvocationHandlerFactoryImpl implements InvocationHandlerFactory 
{
    private final PresentationManager.ClassData classData ;
    private final PresentationManager pm ;
    private Class<?>[] proxyInterfaces ;

    public InvocationHandlerFactoryImpl( PresentationManager pm,
        PresentationManager.ClassData classData ) 
    {
        this.classData = classData ;
        this.pm = pm ;

        Class<?>[] remoteInterfaces =
            classData.getIDLNameTranslator().getInterfaces() ;
        proxyInterfaces = new Class<?>[ remoteInterfaces.length + 1 ] ;
        System.arraycopy(remoteInterfaces, 0, proxyInterfaces, 0,
            remoteInterfaces.length);

        proxyInterfaces[remoteInterfaces.length] = DynamicStub.class ;
    }

    private static class CustomCompositeInvocationHandlerImpl extends
        CompositeInvocationHandlerImpl implements LinkedInvocationHandler, 
        Serializable
    {
        private transient DynamicStub stub ;

        public void setProxy( Proxy proxy ) 
        {
            if (proxy instanceof DynamicStub) {
                ((DynamicStubImpl)stub).setSelf( (DynamicStub)proxy ) ;
            } else {
                throw new RuntimeException(
                    "Proxy not instance of DynamicStub" ) ;
            }
        }

        public Proxy getProxy()
        {
            return (Proxy)((DynamicStubImpl)stub).getSelf() ;
        }

        public CustomCompositeInvocationHandlerImpl( DynamicStub stub )
        {
            this.stub = stub ;
        }

        
        public Object writeReplace() throws ObjectStreamException
        {
            return stub ;
        }
    }

    public InvocationHandler getInvocationHandler() 
    {
        final DynamicStub stub = new DynamicStubImpl( 
            classData.getTypeIds() ) ; 

        return getInvocationHandler( stub ) ;
    }

    
    InvocationHandler getInvocationHandler( DynamicStub stub ) 
    {
        
        
        
        
        InvocationHandler dynamicStubHandler = 
            DelegateInvocationHandlerImpl.create( stub ) ;

        
        
        InvocationHandler stubMethodHandler = new StubInvocationHandlerImpl( 
            pm, classData, stub ) ;

        
        
        final CompositeInvocationHandler handler = 
            new CustomCompositeInvocationHandlerImpl( stub ) ;
        handler.addInvocationHandler( DynamicStub.class,
            dynamicStubHandler ) ;
        handler.addInvocationHandler( org.omg.CORBA.Object.class,
            dynamicStubHandler ) ;
        handler.addInvocationHandler( Object.class,
            dynamicStubHandler ) ;

        
        
        
        
        
        
        
        
        
        
        
        
        
        handler.setDefaultHandler( stubMethodHandler ) ;

        return handler ;
    }

    public Class[] getProxyInterfaces()
    {
        return proxyInterfaces ;
    }
}
