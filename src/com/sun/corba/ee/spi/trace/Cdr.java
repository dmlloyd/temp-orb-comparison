

package xxxx;



@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@MethodMonitorGroup({ CdrRead.class, CdrWrite.class })
public @interface Cdr {
}

