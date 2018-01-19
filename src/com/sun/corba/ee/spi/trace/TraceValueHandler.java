

package com.sun.corba.ee.spi.trace ;



@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@MethodMonitorGroup({ ValueHandlerRead.class, ValueHandlerWrite.class })
public @interface TraceValueHandler {
}
