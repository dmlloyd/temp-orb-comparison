


package xxxx;



public interface LocalResolver extends Resolver {
    
    void register( String name, NullaryFunction<org.omg.CORBA.Object> closure ) ;
}
