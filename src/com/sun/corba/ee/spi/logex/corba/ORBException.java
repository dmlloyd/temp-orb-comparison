

package com.sun.corba.ee.spi.logex.corba ;


import java.lang.annotation.Documented ;
import java.lang.annotation.Target ;
import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;


@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ORBException {
    
    boolean omgException() default false ;

    
    int group() ;
}

