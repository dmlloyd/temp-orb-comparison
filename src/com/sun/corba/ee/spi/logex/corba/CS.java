


package com.sun.corba.ee.spi.logex.corba ;




@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CS {
    
    CSValue value() default CSValue.NO ;
}

