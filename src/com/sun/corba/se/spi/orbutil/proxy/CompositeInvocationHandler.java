

package xxxx;


public interface CompositeInvocationHandler extends InvocationHandler,
    Serializable
{
    
    void addInvocationHandler( Class interf, InvocationHandler handler ) ;

    
    void setDefaultHandler( InvocationHandler handler ) ;
}
