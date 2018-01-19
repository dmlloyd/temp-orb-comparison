

package com.sun.corba.ee.spi.logex.corba ;




@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ORBException {
    
    boolean omgException() default false ;

    
    int group() ;
}

