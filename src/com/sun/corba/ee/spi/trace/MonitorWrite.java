

package com.sun.corba.ee.spi.trace ;

import java.lang.annotation.Target ;
import java.lang.annotation.ElementType ;
import java.lang.annotation.Retention ;
import java.lang.annotation.RetentionPolicy ;
import org.glassfish.pfl.tf.spi.annotation.MethodMonitorGroup;


@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@MethodMonitorGroup
public @interface MonitorWrite {
}
