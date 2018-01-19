


package com.sun.corba.ee.impl.resolver ;


public class SplitLocalResolverImpl implements LocalResolver 
{
    private Resolver resolver ;
    private LocalResolver localResolver ;

    public SplitLocalResolverImpl( Resolver resolver, 
        LocalResolver localResolver ) {
        this.resolver = resolver ;
        this.localResolver = localResolver ;
    }

    public void register( String name, 
        NullaryFunction<org.omg.CORBA.Object> closure ) {
        localResolver.register( name, closure ) ;
    }

    public org.omg.CORBA.Object resolve( String name ) {
        return resolver.resolve( name ) ;
    }

    public Set<String> list() {
        return resolver.list() ;
    }
}

