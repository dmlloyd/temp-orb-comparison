

package xxxx;


public interface ValueHandlerMultiFormat extends ValueHandler {

    
    byte getMaximumStreamFormatVersion();

    
    void writeValue(org.omg.CORBA.portable.OutputStream out,
                    java.io.Serializable value,
                    byte streamFormatVersion);
}
