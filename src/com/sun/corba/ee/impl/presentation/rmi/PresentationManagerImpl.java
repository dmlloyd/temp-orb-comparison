


package com.sun.corba.ee.impl.presentation.rmi ;












public final class PresentationManagerImpl implements PresentationManager
{
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private WeakCache<Class<?>,ClassData> classToClassData ;
    private WeakCache<Method,DynamicMethodMarshaller> methodToDMM ;
    private PresentationManager.StubFactoryFactory staticStubFactoryFactory ;
    private PresentationManager.StubFactoryFactory dynamicStubFactoryFactory ;
    private boolean useDynamicStubs ;
    private boolean debug ;
    private PrintStream ps ;

    public PresentationManagerImpl( boolean useDynamicStubs )
    {
        this.useDynamicStubs = useDynamicStubs ;

        classToClassData = new WeakCache<Class<?>,ClassData>() {
            @Override
            protected ClassData lookup(Class<?> key) {
                return new ClassDataImpl( key ) ;
            }
        } ;

        methodToDMM = new WeakCache<Method,DynamicMethodMarshaller>() {
            @Override
            protected DynamicMethodMarshaller lookup(Method key) {
                return new DynamicMethodMarshallerImpl( key ) ;
            }
        } ;
    }





    public synchronized DynamicMethodMarshaller getDynamicMethodMarshaller( 
        Method method ) 
    {
        if (method == null) {
            return null;
        }

        return methodToDMM.get(method) ;
    }

    public synchronized ClassData getClassData( Class<?> cls )
    {
        return classToClassData.get(cls) ;
    }

    private class ClassDataImpl implements PresentationManager.ClassData 
    {
        private Class<?> cls ;
        private IDLNameTranslator nameTranslator ;
        private String[] typeIds ;
        private InvocationHandlerFactory ihfactory ;
        private Map<String,Object> dictionary ;

        ClassDataImpl( Class<?> cls ) {
            this.cls = cls ;
            Graph<NodeImpl> gr = new GraphImpl<NodeImpl>() ;
            NodeImpl root = new NodeImpl( cls ) ;
            Set<NodeImpl> rootSet = getRootSet( cls, root, gr ) ;

            
            
            

            Class<?>[] interfaces = getInterfaces( rootSet ) ;
            nameTranslator = IDLNameTranslatorImpl.get( interfaces ) ;
            typeIds = makeTypeIds( root, gr, rootSet ) ;
            ihfactory = new InvocationHandlerFactoryImpl( 
                PresentationManagerImpl.this, this ) ;
            dictionary = new HashMap<String,Object>() ;
        }

        public Class<?> getMyClass()
        {
            return cls ;
        }

        public IDLNameTranslator getIDLNameTranslator()
        {
            return nameTranslator ;
        }

        public String[] getTypeIds()
        {
            return typeIds.clone() ;
        }

        public InvocationHandlerFactory getInvocationHandlerFactory() 
        {
            return ihfactory ;
        }

        public Map<String,Object> getDictionary()
        {
            return dictionary ;
        }
    }

    public PresentationManager.StubFactoryFactory getStubFactoryFactory( 
        boolean isDynamic ) 
    {
        if (isDynamic) {
            return getDynamicStubFactoryFactory();
        } else {
            return getStaticStubFactoryFactory();
        }
    }

    @Override
    public StubFactoryFactory getStaticStubFactoryFactory() {
        return staticStubFactoryFactory;
    }

    @Override
    public StubFactoryFactory getDynamicStubFactoryFactory() {
        return dynamicStubFactoryFactory;
    }


    
    public void setStaticStubFactoryFactory(StubFactoryFactory sff) {
        staticStubFactoryFactory = sff;
    }

    
    public void setDynamicStubFactoryFactory(StubFactoryFactory sff) {
        dynamicStubFactoryFactory = sff;
    }

    public Tie getTie()
    {
        return dynamicStubFactoryFactory.getTie( null ) ;
    }

    public String getRepositoryId( java.rmi.Remote impl ) 
    {
        
        Tie tie = getTie() ;
        
        
        
        tie.setTarget( impl ) ;

        return Servant.class.cast( tie )._all_interfaces( 
            (POA)null, (byte[])null)[0] ;
    }

    public boolean useDynamicStubs()
    {
        return useDynamicStubs ;
    }

    public void flushClass( final Class<?> cls )
    {
        classToClassData.remove( cls ) ;

        Method[] methods = (Method[])AccessController.doPrivileged(
            new PrivilegedAction<Object>() {
                public Object run() {
                    return cls.getMethods() ;
                }
            } 
        ) ;

        for( int ctr=0; ctr<methods.length; ctr++) {
            methodToDMM.remove( methods[ctr] ) ;
        }
    }





    private Set<NodeImpl> getRootSet( Class<?> target, NodeImpl root,
        Graph<NodeImpl> gr )
    {
        Set<NodeImpl> rootSet = null ;

        if (ClassInfoCache.get(target).isInterface()) {
            gr.add( root ) ;
            rootSet = gr.getRoots() ; 
        } else {
            
            Class<?> superclass = target ;
            Set<NodeImpl> initialRootSet = new HashSet<NodeImpl>() ;
            while ((superclass != null) && !superclass.equals( Object.class )) {
                NodeImpl node = new NodeImpl( superclass ) ;
                gr.add( node ) ;
                initialRootSet.add( node ) ;
                superclass = superclass.getSuperclass() ;
            }

            
            gr.getRoots() ; 

            
            gr.removeAll( initialRootSet ) ;
            rootSet = gr.getRoots() ;    
        }

        return rootSet ;
    }

    private Class<?>[] getInterfaces( Set<NodeImpl> roots )
    {
        int ctr = 0 ;
        Class<?>[] classes = new Class<?>[ roots.size() ] ;
        for (NodeImpl node : roots) {
            classes[ctr] = node.getInterface() ;
            ctr++ ;
        }

        return classes ;
    }

    private String[] makeTypeIds( NodeImpl root, Graph<NodeImpl> gr,
        Set<NodeImpl> rootSet )
    {
        Set<NodeImpl> nonRootSet = new HashSet<NodeImpl>( gr ) ;
        nonRootSet.removeAll( rootSet ) ;

        
        
        if (rootSet.isEmpty()) {
            return new String[]{""};
        }

        
        List<String> result = new ArrayList<String>() ;

        if (rootSet.size() > 1) {
            
            
            
            result.add( root.getTypeId() ) ;
        }

        addNodes( result, rootSet ) ;
        addNodes( result, nonRootSet ) ;

        return result.toArray(new String[result.size()]) ;
    }

    private void addNodes( List<String> resultList, Set<NodeImpl> nodeSet )
    {
        for (NodeImpl node : nodeSet) {
            String typeId = node.getTypeId() ;
            resultList.add( typeId ) ;
        }
    }

    private static class NodeImpl implements Node<NodeImpl>
    {
        private Class<?> interf ;

        public Class<?> getInterface()
        {
            return interf ;
        }

        NodeImpl( Class<?> interf )
        {
            this.interf = interf ;
        }

        public String getTypeId()
        {
            return RepositoryId.createForJavaType( interf ) ;
            
        }

        public Set<NodeImpl> getChildren()
        {
            Set<NodeImpl> result = new HashSet<NodeImpl>() ;
            Class<?>[] interfaces = interf.getInterfaces() ;
            for (int ctr=0; ctr<interfaces.length; ctr++) {
                Class<?> cls = interfaces[ctr] ;
                ClassInfoCache.ClassInfo cinfo = 
                    ClassInfoCache.get( cls ) ;
                if (cinfo.isARemote(cls) &&
                    !Remote.class.equals(cls)) {
                    result.add(new NodeImpl(cls));
                }
            }

            return result ;
        }

        @Override
        public String toString() 
        {
            return "NodeImpl[" + interf + "]" ;
        }

        @Override
        public int hashCode()
        {
            return interf.hashCode() ;
        }

        @Override
        public boolean equals( Object obj )
        {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof NodeImpl)) {
                return false;
            }

            NodeImpl other = (NodeImpl)obj ;

            return other.getInterface().equals( interf ) ;
        }
    }

    
    public void enableDebug( PrintStream ps ) {
        this.debug = true ;
        this.ps = ps ;
    }

    public void disableDebug() {
        this.debug = false ;
        this.ps = null ;
    }

    public boolean getDebug() {
        return debug ;
    }

    public PrintStream getPrintStream() {
        return ps ;
    }
}
