


package xxxx;

public interface ClassCodeBaseHandler {
    
    String getCodeBase( Class<?> cls ) ;

    
    Class<?> loadClass( String codebase, String className ) ;
}
