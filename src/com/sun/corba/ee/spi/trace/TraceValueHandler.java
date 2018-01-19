

package xxxx;



@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@MethodMonitorGroup({ ValueHandlerRead.class, ValueHandlerWrite.class })
public @interface TraceValueHandler {
}
