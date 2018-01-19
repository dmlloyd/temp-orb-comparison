

package xxxx;




@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ORBException {
    
    boolean omgException() default false ;

    
    int group() ;
}

