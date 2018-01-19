

package xxxx;


public interface ParserAction {
    
    String getPropertyName() ;

    
    boolean isPrefix() ;

    
    String getFieldName() ;

    
    Object apply( Properties props ) ;
}
