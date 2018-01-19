

package xxxx;



public class CompositeResolverImpl implements Resolver {
    private Resolver first ;
    private Resolver second ;

    public CompositeResolverImpl( Resolver first, Resolver second ) 
    {
        this.first = first ;
        this.second = second ;
    }

    public org.omg.CORBA.Object resolve( String name ) 
    {
        org.omg.CORBA.Object result = first.resolve( name ) ;
        if (result == null) 
            result = second.resolve( name ) ;
        return result ;
    }

    public Set<String> list()
    {
        Set<String> result = new HashSet() ;
        result.addAll( first.list() ) ;
        result.addAll( second.list() ) ;
        return result ;
    }
}
